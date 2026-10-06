import java.sql.*;

public class Solution {

    public static class PrescriptionLedger {

        public void initSchema(Connection conn) throws SQLException {
            String sql = """
                CREATE TABLE IF NOT EXISTS pharmacy_inventory (
                    medication_code VARCHAR(32) PRIMARY KEY,
                    quantity INT NOT NULL CHECK (quantity >= 0)
                );
                CREATE TABLE IF NOT EXISTS dispense_audit (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    patient_mrn VARCHAR(64) NOT NULL,
                    medication_code VARCHAR(32) NOT NULL,
                    dispensed_units INT NOT NULL,
                    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
                );
                CREATE TABLE IF NOT EXISTS insurance_claims (
                    claim_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    patient_mrn VARCHAR(64) NOT NULL,
                    medication_code VARCHAR(32) NOT NULL,
                    status VARCHAR(32) NOT NULL
                );
                """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        }

        public void setInventory(Connection conn, String medCode, int quantity) throws SQLException {
            String sql = "MERGE INTO pharmacy_inventory (medication_code, quantity) KEY (medication_code) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, medCode);
                pstmt.setInt(2, quantity);
                pstmt.executeUpdate();
            }
        }

        public boolean dispenseMedication(
            Connection conn,
            String mrn,
            String medCode,
            int units,
            boolean simulateInsuranceFailure
        ) throws SQLException {
            boolean origAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            boolean claimSucceeded = false;

            try {
                // 1. Decrement inventory
                String decSql = "UPDATE pharmacy_inventory SET quantity = quantity - ? WHERE medication_code = ? AND quantity >= ?";
                try (PreparedStatement decStmt = conn.prepareStatement(decSql)) {
                    decStmt.setInt(1, units);
                    decStmt.setString(2, medCode);
                    decStmt.setInt(3, units);
                    int updated = decStmt.executeUpdate();
                    if (updated == 0) {
                        throw new IllegalStateException("Insufficient inventory for " + medCode);
                    }
                }

                // 2. Insert into dispense audit
                String auditSql = "INSERT INTO dispense_audit (patient_mrn, medication_code, dispensed_units) VALUES (?, ?, ?)";
                try (PreparedStatement auditStmt = conn.prepareStatement(auditSql)) {
                    auditStmt.setString(1, mrn);
                    auditStmt.setString(2, medCode);
                    auditStmt.setInt(3, units);
                    auditStmt.executeUpdate();
                }

                // 3. Savepoint for auxiliary insurance claim
                Savepoint sp = conn.setSavepoint("InsuranceClaimStage");

                try {
                    if (simulateInsuranceFailure) {
                        throw new SQLException("Simulated Insurance Gateway Timeout");
                    }
                    String claimSql = "INSERT INTO insurance_claims (patient_mrn, medication_code, status) VALUES (?, ?, ?)";
                    try (PreparedStatement claimStmt = conn.prepareStatement(claimSql)) {
                        claimStmt.setString(1, mrn);
                        claimStmt.setString(2, medCode);
                        claimStmt.setString(3, "ADJUDICATED");
                        claimStmt.executeUpdate();
                    }
                    claimSucceeded = true;
                } catch (SQLException ex) {
                    conn.rollback(sp);
                    claimSucceeded = false;
                }

                conn.commit();
                return claimSucceeded;
            } catch (Exception ex) {
                conn.rollback();
                if (ex instanceof SQLException sqlEx) {
                    throw sqlEx;
                }
                throw new SQLException(ex.getMessage(), ex);
            } finally {
                conn.setAutoCommit(origAutoCommit);
            }
        }

        public int getInventory(Connection conn, String medCode) throws SQLException {
            String sql = "SELECT quantity FROM pharmacy_inventory WHERE medication_code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, medCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return 0;
        }

        public int getAuditCount(Connection conn, String mrn) throws SQLException {
            String sql = "SELECT COUNT(*) FROM dispense_audit WHERE patient_mrn = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, mrn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return 0;
        }

        public int getClaimCount(Connection conn, String mrn) throws SQLException {
            String sql = "SELECT COUNT(*) FROM insurance_claims WHERE patient_mrn = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, mrn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return 0;
        }
    }
}
