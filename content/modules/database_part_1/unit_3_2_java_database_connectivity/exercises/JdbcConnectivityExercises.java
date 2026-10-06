import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit 3.2 Exercises: Java Database Connectivity (JDBC)
 * Implement each method to pass all assertion tests.
 */
public class JdbcConnectivityExercises {

    public static final String JDBC_URL = "jdbc:h2:mem:unit32db;DB_CLOSE_DELAY=-1";

    /**
     * Exercise 1: Table Initialization
     * Create table vitals_telemetry if not exists with columns:
     * id BIGINT AUTO_INCREMENT PRIMARY KEY,
     * mrn VARCHAR(50) NOT NULL,
     * heart_rate INT NOT NULL,
     * systolic INT NOT NULL
     */
    public static void initTable(Connection conn) throws SQLException {
        // TODO: Execute DDL using Statement to create the vitals_telemetry table.
    }

    /**
     * Exercise 2: Parameterized Insert via PreparedStatement
     * Insert a record into vitals_telemetry using PreparedStatement with 1-based index placeholders.
     * Return the number of rows affected.
     */
    public static int insertVital(Connection conn, String mrn, int hr, int systolic) throws SQLException {
        // TODO: Prepare insert statement, bind parameters, executeUpdate, and return rows affected.
        return 0;
    }

    /**
     * Exercise 3: Aggregate Query via ResultSet
     * Query average heart_rate for the given MRN using PreparedStatement and ResultSet.
     * Return 0.0 if no records found.
     */
    public static double queryAvgHeartRate(Connection conn, String mrn) throws SQLException {
        // TODO: Prepare SELECT AVG(heart_rate) FROM vitals_telemetry WHERE mrn = ?, executeQuery, read double.
        return 0.0;
    }

    /**
     * Exercise 4: Atomic Transaction with Rollback
     * Disable autocommit. Insert a record with mrn.
     * If triggerRollback is true, rollback the transaction and restore autocommit.
     * If triggerRollback is false, commit the transaction and restore autocommit.
     */
    public static void executeTransaction(Connection conn, String mrn, int hr, int sys, boolean triggerRollback) throws SQLException {
        // TODO: Manage transaction boundaries with commit() and rollback().
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

            System.out.println("All Unit 3.2 Exercises Passed Successfully!");
        }
    }
}
