import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LabTests {

    public static final String TEST_DB_URL = "jdbc:h2:mem:rx_ledger_lab_db;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        testSuccessfulDispensationAndClaim();
        testPartialRollbackOnInsuranceFailure();
        testStockExhaustionRollsBackEntireTransaction();
        System.out.println("All Unit 3.4 Lab 1 Tests Passed Successfully!");
    }

    public static void testSuccessfulDispensationAndClaim() throws SQLException {
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, "sa", "")) {
            Solution.PrescriptionLedger ledger = new Solution.PrescriptionLedger();
            ledger.initSchema(conn);
            ledger.setInventory(conn, "AMOXICILLIN-500", 100);

            boolean claimOk = ledger.dispenseMedication(conn, "PATIENT-RX-1", "AMOXICILLIN-500", 10, false);
            assert claimOk : "Expected claim to succeed";

            int remaining = ledger.getInventory(conn, "AMOXICILLIN-500");
            assert remaining == 90 : "Expected 90 remaining stock, got " + remaining;

            assert ledger.getAuditCount(conn, "PATIENT-RX-1") == 1 : "Audit record missing";
            assert ledger.getClaimCount(conn, "PATIENT-RX-1") == 1 : "Insurance claim record missing";
            System.out.println("Lab Test 1 Passed: Complete dispensation with claim committed.");
        }
    }

    public static void testPartialRollbackOnInsuranceFailure() throws SQLException {
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, "sa", "")) {
            Solution.PrescriptionLedger ledger = new Solution.PrescriptionLedger();
            ledger.initSchema(conn);
            ledger.setInventory(conn, "INSULIN-GLARGINE", 50);

            // Dispense with simulated insurance failure
            boolean claimOk = ledger.dispenseMedication(conn, "PATIENT-RX-2", "INSULIN-GLARGINE", 5, true);
            assert !claimOk : "Expected claim to fail and trigger savepoint rollback";

            // Inventory MUST still be decremented (from 50 down to 45)!
            int remaining = ledger.getInventory(conn, "INSULIN-GLARGINE");
            assert remaining == 45 : "Inventory should remain decremented (45), got " + remaining;

            // Audit record MUST exist
            assert ledger.getAuditCount(conn, "PATIENT-RX-2") == 1 : "Dispense audit record missing";

            // Insurance claim MUST NOT exist (rolled back to savepoint)
            assert ledger.getClaimCount(conn, "PATIENT-RX-2") == 0 : "Insurance claim was not rolled back to savepoint";
            System.out.println("Lab Test 2 Passed: Savepoint correctly rolled back insurance stage while preserving inventory decrement.");
        }
    }

    public static void testStockExhaustionRollsBackEntireTransaction() throws SQLException {
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, "sa", "")) {
            Solution.PrescriptionLedger ledger = new Solution.PrescriptionLedger();
            ledger.initSchema(conn);
            ledger.setInventory(conn, "MORPHINE-10", 2);

            boolean caught = false;
            try {
                // Request 10 units when only 2 in stock
                ledger.dispenseMedication(conn, "PATIENT-RX-3", "MORPHINE-10", 10, false);
            } catch (SQLException ex) {
                caught = true;
            }
            assert caught : "Expected exception on out of stock";

            // Inventory untouched
            assert ledger.getInventory(conn, "MORPHINE-10") == 2 : "Inventory altered on failed transaction";
            assert ledger.getAuditCount(conn, "PATIENT-RX-3") == 0 : "Audit log written despite stock failure";
            System.out.println("Lab Test 3 Passed: Out-of-stock failure rolled back entire transaction.");
        }
    }
}
