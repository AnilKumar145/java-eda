import java.sql.*;
import java.util.*;

public class LabTests {

    private static final String JDBC_URL = "jdbc:h2:mem:migration_lab_db;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws Exception {
        testFreshMigrationSequence();
        testIdempotentMigration();
        testTamperedMigrationDetection();
        System.out.println("All Unit 4.4 Lab 1 Tests Passed Successfully!");
    }

    public static void testFreshMigrationSequence() throws SQLException {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "")) {
            Solution.FormularyMigrationEngine engine = new Solution.FormularyMigrationEngine();

            List<Solution.MigrationScript> scripts = List.of(
                new Solution.MigrationScript("1.0", "create medications table", "V1_0__create_meds.sql",
                    "CREATE TABLE IF NOT EXISTS medications (id BIGINT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(50) NOT NULL);"),
                new Solution.MigrationScript("1.1", "add brand name column", "V1_1__add_brand_name.sql",
                    "ALTER TABLE medications ADD COLUMN IF NOT EXISTS brand_name VARCHAR(100);"),
                new Solution.MigrationScript("2.0", "insert emergency stock", "V2_0__seed_emergency.sql",
                    "INSERT INTO medications (code, brand_name) VALUES ('EPI-01', 'Epinephrine');")
            );

            List<String> applied = engine.migrate(conn, scripts);
            assert applied.size() == 3 : "All 3 scripts should be applied on fresh database";
            assert List.of("1.0", "1.1", "2.0").equals(applied) : "Applied version order mismatch";

            // Verify table and data exist
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT brand_name FROM medications WHERE code = 'EPI-01'")) {
                assert rs.next() : "Expected inserted medication row";
                assert "Epinephrine".equals(rs.getString("brand_name")) : "Brand name mismatch";
            }
            System.out.println("Lab 1 Test 1 Passed: Fresh migration sequence executed and verified.");
        }
    }

    public static void testIdempotentMigration() throws SQLException {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "")) {
            Solution.FormularyMigrationEngine engine = new Solution.FormularyMigrationEngine();

            List<Solution.MigrationScript> scripts = List.of(
                new Solution.MigrationScript("1.0", "create medications table", "V1_0__create_meds.sql",
                    "CREATE TABLE IF NOT EXISTS medications (id BIGINT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(50) NOT NULL);"),
                new Solution.MigrationScript("1.1", "add brand name column", "V1_1__add_brand_name.sql",
                    "ALTER TABLE medications ADD COLUMN IF NOT EXISTS brand_name VARCHAR(100);"),
                new Solution.MigrationScript("2.0", "insert emergency stock", "V2_0__seed_emergency.sql",
                    "INSERT INTO medications (code, brand_name) VALUES ('EPI-01', 'Epinephrine');")
            );

            // Re-running same scripts should result in 0 new migrations
            List<String> reapplied = engine.migrate(conn, scripts);
            assert reapplied.isEmpty() : "No new migrations should be executed on up-to-date schema";
            System.out.println("Lab 1 Test 2 Passed: Migration idempotency validated.");
        }
    }

    public static void testTamperedMigrationDetection() throws SQLException {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "")) {
            Solution.FormularyMigrationEngine engine = new Solution.FormularyMigrationEngine();

            // Alter the SQL of already-applied version 1.1
            List<Solution.MigrationScript> tamperedScripts = List.of(
                new Solution.MigrationScript("1.0", "create medications table", "V1_0__create_meds.sql",
                    "CREATE TABLE IF NOT EXISTS medications (id BIGINT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(50) NOT NULL);"),
                new Solution.MigrationScript("1.1", "add brand name column", "V1_1__add_brand_name.sql",
                    "ALTER TABLE medications ADD COLUMN IF NOT EXISTS brand_name VARCHAR(250);"), // Altered length!
                new Solution.MigrationScript("2.0", "insert emergency stock", "V2_0__seed_emergency.sql",
                    "INSERT INTO medications (code, brand_name) VALUES ('EPI-01', 'Epinephrine');")
            );

            boolean caughtTamperException = false;
            try {
                engine.migrate(conn, tamperedScripts);
            } catch (IllegalStateException e) {
                if (e.getMessage().contains("checksum mismatch")) {
                    caughtTamperException = true;
                }
            }
            assert caughtTamperException : "Expected checksum mismatch IllegalStateException";
            System.out.println("Lab 1 Test 3 Passed: Tampered historical migration detected and blocked.");
        }
    }
}
