import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.Set;

/**
 * Unit 3.4 Exercises: Database Security, Transactions, and Best Practices - Solutions
 */
public class SecurityAndBestPracticesSolutions {

    public static final String JDBC_URL = "jdbc:h2:mem:unit34db_sol;DB_CLOSE_DELAY=-1";

    public static String validateColumn(String requestedCol, Set<String> allowedColumns) {
        if (requestedCol == null || !allowedColumns.contains(requestedCol)) {
            throw new IllegalArgumentException("Unauthorized column identifier: " + requestedCol);
        }
        return requestedCol;
    }

    public static String findPatientByMrn(Connection conn, String mrn) throws SQLException {
        String sql = "SELECT full_name FROM patients WHERE mrn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mrn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
        }
        return null;
    }

    public static void executeWithSavepoint(Connection conn, String primaryData, String secondaryData, boolean failSecondary) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            String insertSql = "INSERT INTO audit_log (log_msg) VALUES (?)";
            try (PreparedStatement p1 = conn.prepareStatement(insertSql)) {
                p1.setString(1, primaryData);
                p1.executeUpdate();
            }

            Savepoint sp = conn.setSavepoint("sub_action");

            try (PreparedStatement p2 = conn.prepareStatement(insertSql)) {
                p2.setString(1, secondaryData);
                p2.executeUpdate();
                if (failSecondary) {
                    conn.rollback(sp);
                }
            } catch (SQLException ex) {
                conn.rollback(sp);
            }

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public static HikariDataSource createHikariDataSource(String jdbcUrl) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(2000);
        return new HikariDataSource(config);
    }

    public static void main(String[] args) throws Exception {
        // Test 1: Whitelist Validator
        Set<String> validCols = Set.of("full_name", "mrn", "created_at");
        assert "full_name".equals(validateColumn("full_name", validCols)) : "Valid col rejected";
        boolean caught = false;
        try {
            validateColumn("id; DROP TABLE users; --", validCols);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assert caught : "SQL injection payload should have been intercepted";
        System.out.println("Exercise 1 passed: whitelist validated.");

        // Test 2: SQL Injection Defense
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE patients (mrn VARCHAR(50), full_name VARCHAR(100))");
            stmt.execute("INSERT INTO patients VALUES ('MRN-1', 'Alice'), ('MRN-2', 'Bob')");

            String maliciousPayload = "MRN-1' OR '1'='1";
            String result = findPatientByMrn(conn, maliciousPayload);
            assert result == null : "Malicious payload bypassed security and returned: " + result;

            String valid = findPatientByMrn(conn, "MRN-1");
            assert "Alice".equals(valid) : "Expected Alice, got " + valid;
            System.out.println("Exercise 2 passed: SQL injection neutralized.");

            // Test 3: Savepoint Partial Rollback
            stmt.execute("CREATE TABLE audit_log (id BIGINT AUTO_INCREMENT PRIMARY KEY, log_msg VARCHAR(100))");
            executeWithSavepoint(conn, "PRIMARY_EVENT", "SECONDARY_FAIL", true);

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*), log_msg FROM audit_log GROUP BY log_msg")) {
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    assert "PRIMARY_EVENT".equals(rs.getString("log_msg")) : "Expected only PRIMARY_EVENT";
                }
                assert rowCount == 1 : "Secondary event was not rolled back";
                System.out.println("Exercise 3 passed: Savepoint partial rollback verified.");
            }
        }

        // Test 4: HikariCP DataSource
        try (HikariDataSource ds = createHikariDataSource(JDBC_URL)) {
            assert ds.getMaximumPoolSize() == 5 : "Max pool size mismatch";
            assert ds.getConnectionTimeout() == 2000 : "Connection timeout mismatch";
            try (Connection conn = ds.getConnection()) {
                assert conn.isValid(1) : "Connection from pool invalid";
            }
            System.out.println("Exercise 4 passed: HikariCP connection pool initialized.");
        }

        System.out.println("All Unit 3.4 Solutions Passed Successfully!");
    }
}
