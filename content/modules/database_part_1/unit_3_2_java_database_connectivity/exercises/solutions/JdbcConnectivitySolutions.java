import java.sql.*;

/**
 * Unit 3.2 Exercises: Java Database Connectivity (JDBC) - Solutions
 */
public class JdbcConnectivitySolutions {

    public static final String JDBC_URL = "jdbc:h2:mem:unit32db_sol;DB_CLOSE_DELAY=-1";

    public static void initTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS vitals_telemetry (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                mrn VARCHAR(50) NOT NULL,
                heart_rate INT NOT NULL,
                systolic INT NOT NULL
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static int insertVital(Connection conn, String mrn, int hr, int systolic) throws SQLException {
        String sql = "INSERT INTO vitals_telemetry (mrn, heart_rate, systolic) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mrn);
            pstmt.setInt(2, hr);
            pstmt.setInt(3, systolic);
            return pstmt.executeUpdate();
        }
    }

    public static double queryAvgHeartRate(Connection conn, String mrn) throws SQLException {
        String sql = "SELECT AVG(heart_rate) FROM vitals_telemetry WHERE mrn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mrn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double val = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : val;
                }
            }
        }
        return 0.0;
    }

    public static void executeTransaction(Connection conn, String mrn, int hr, int sys, boolean triggerRollback) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            insertVital(conn, mrn, hr, sys);
            if (triggerRollback) {
                conn.rollback();
            } else {
                conn.commit();
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "")) {
            initTable(conn);
            System.out.println("Exercise 1 passed: table initialized.");

            int rows = insertVital(conn, "MRN-101", 72, 120);
            assert rows == 1 : "Expected 1 row inserted, got " + rows;
            insertVital(conn, "MRN-101", 78, 124);
            System.out.println("Exercise 2 passed: rows inserted.");

            double avgHr = queryAvgHeartRate(conn, "MRN-101");
            assert Math.abs(avgHr - 75.0) < 0.001 : "Expected avg HR 75.0, got " + avgHr;
            System.out.println("Exercise 3 passed: avg HR = " + avgHr);

            // Test rollback
            executeTransaction(conn, "MRN-999", 100, 140, true);
            double rolledBackAvg = queryAvgHeartRate(conn, "MRN-999");
            assert rolledBackAvg == 0.0 : "Transaction should have rolled back for MRN-999";

            // Test commit
            executeTransaction(conn, "MRN-999", 100, 140, false);
            double committedAvg = queryAvgHeartRate(conn, "MRN-999");
            assert Math.abs(committedAvg - 100.0) < 0.001 : "Committed record missing";
            System.out.println("Exercise 4 passed: transaction rollback and commit verified.");

            System.out.println("All Unit 3.2 Solutions Passed Successfully!");
        }
    }
}
