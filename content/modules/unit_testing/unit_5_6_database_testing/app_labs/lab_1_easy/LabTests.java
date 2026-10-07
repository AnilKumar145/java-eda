import java.sql.*;
import java.util.*;

public class LabTests {

    private static final String H2_URL = "jdbc:h2:mem:pharmacy_lab_test;DB_CLOSE_DELAY=-1";

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(H2_URL, "sa", "");
    }

    public static void initSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS inventory_items (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    sku VARCHAR(50) NOT NULL UNIQUE,
                    name VARCHAR(100) NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    quantity INT NOT NULL DEFAULT 0,
                    reorder_threshold INT NOT NULL DEFAULT 10,
                    unit_cost DOUBLE NOT NULL
                )
            """);
        }
    }

    public static void main(String[] args) throws SQLException {
        testAddAndRetrieveItem();
        testDuplicateSkuThrowsSqlException();
        testDispenseMedicationSuccess();
        testDispenseInsufficientStockThrowsStockDiscrepancyException();
        testRestockMedicationSuccess();
        testGetItemsRequiringReorder();
        testCalculateInventoryValue();
        testIsolationRollbackLeavesCleanSchema();
        System.out.println("All Unit 5.6 Lab 1 Tests Passed Successfully!");
    }

    public static void testAddAndRetrieveItem() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                Solution.PharmacyInventoryRepository repo = new Solution.PharmacyInventoryRepository(conn);
                Solution.InventoryItem item = repo.addItem("cef-500", "Cefazolin 500mg IV", "ANTIBIOTIC", 100, 20, 14.50);

                assert item.id() > 0 : "Generated ID required";
                assert "CEF-500".equals(item.sku()) : "SKU must be normalized to uppercase";

                Optional<Solution.InventoryItem> fetched = repo.getBySku("cef-500");
                assert fetched.isPresent() : "Item must be present";
                assert fetched.get().quantity() == 100 : "Quantity should be 100";
                System.out.println("Lab 1 Test 1 Passed: Add and retrieve item verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testDuplicateSkuThrowsSqlException() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                Solution.PharmacyInventoryRepository repo = new Solution.PharmacyInventoryRepository(conn);
                repo.addItem("van-1000", "Vancomycin 1g", "ANTIBIOTIC", 50, 15, 32.00);

                boolean caught = false;
                try {
                    repo.addItem("VAN-1000", "Vancomycin Generic", "ANTIBIOTIC", 10, 5, 30.00);
                } catch (SQLException e) {
                    caught = true;
                }
                assert caught : "Duplicate SKU must throw SQLException";
                System.out.println("Lab 1 Test 2 Passed: Duplicate SKU rejection verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testDispenseMedicationSuccess() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                Solution.PharmacyInventoryRepository repo = new Solution.PharmacyInventoryRepository(conn);
                repo.addItem("fent-50", "Fentanyl 50mcg", "ANALGESIC", 60, 15, 18.75);

                Solution.InventoryItem updated = repo.dispenseMedication("FENT-50", 25);
                assert updated.quantity() == 35 : "Expected quantity 35";
                assert repo.getBySku("FENT-50").get().quantity() == 35 : "Persisted quantity must be 35";
                System.out.println("Lab 1 Test 3 Passed: Dispense medication verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testDispenseInsufficientStockThrowsStockDiscrepancyException() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                Solution.PharmacyInventoryRepository repo = new Solution.PharmacyInventoryRepository(conn);
                repo.addItem("prop-10", "Propofol 10mg/mL", "ANESTHETIC", 5, 10, 42.00);

                boolean caught = false;
                try {
                    repo.dispenseMedication("PROP-10", 12);
                } catch (Solution.StockDiscrepancyException ex) {
                    caught = true;
                    assert ex.getRequested() == 12 : "Requested should be 12";
                    assert ex.getAvailable() == 5 : "Available should be 5";
                }
                assert caught : "Insufficient stock must throw StockDiscrepancyException";
                assert repo.getBySku("PROP-10").get().quantity() == 5 : "Inventory must remain untouched";
                System.out.println("Lab 1 Test 4 Passed: Insufficient stock discrepancy verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testRestockMedicationSuccess() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                Solution.PharmacyInventoryRepository repo = new Solution.PharmacyInventoryRepository(conn);
                repo.addItem("norepi-4", "Norepinephrine 4mg", "VASOPRESSOR", 15, 20, 55.00);

                Solution.InventoryItem updated = repo.restockMedication("norepi-4", 40);
                assert updated.quantity() == 55 : "Expected quantity 55";
                assert repo.getBySku("NOREPI-4").get().quantity() == 55 : "Persisted quantity must be 55";
                System.out.println("Lab 1 Test 5 Passed: Restock medication verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testGetItemsRequiringReorder() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                Solution.PharmacyInventoryRepository repo = new Solution.PharmacyInventoryRepository(conn);
                repo.addItem("MED-001", "Vasopressin", "VASOPRESSOR", 3, 10, 80.00);   // reorder (3 <= 10)
                repo.addItem("MED-002", "Ondansetron", "ANTIEMETIC", 100, 25, 4.50);    // ok
                repo.addItem("MED-003", "Dopamine", "VASOPRESSOR", 8, 15, 30.00);       // reorder (8 <= 15)
                repo.addItem("MED-004", "Lorazepam", "SEDATIVE", 12, 12, 11.00);         // reorder (12 <= 12)

                List<Solution.InventoryItem> reorderList = repo.getItemsRequiringReorder();
                assert reorderList.size() == 3 : "Expected 3 items requiring reorder";
                assert "MED-001".equals(reorderList.get(0).sku()) : "First should be MED-001 (qty 3)";
                assert "MED-003".equals(reorderList.get(1).sku()) : "Second should be MED-003 (qty 8)";
                assert "MED-004".equals(reorderList.get(2).sku()) : "Third should be MED-004 (qty 12)";
                System.out.println("Lab 1 Test 6 Passed: Reorder items query and sorting verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testCalculateInventoryValue() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                Solution.PharmacyInventoryRepository repo = new Solution.PharmacyInventoryRepository(conn);
                repo.addItem("A1", "Med A", "CAT1", 10, 5, 10.50); // 105.0
                repo.addItem("B2", "Med B", "CAT2", 20, 5, 5.25);  // 105.0
                repo.addItem("C3", "Med C", "CAT3", 4, 2, 50.00);  // 200.0

                double total = repo.calculateInventoryValue();
                assert Math.abs(total - 410.00) < 0.001 : "Expected 410.00 total value, got " + total;
                System.out.println("Lab 1 Test 7 Passed: Total inventory value calculation verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testIsolationRollbackLeavesCleanSchema() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            Solution.PharmacyInventoryRepository repo = new Solution.PharmacyInventoryRepository(conn);
            assert repo.getItemsRequiringReorder().isEmpty() : "Table must be completely empty across tests";
            assert repo.calculateInventoryValue() == 0.0 : "Inventory value must be 0.0";
            System.out.println("Lab 1 Test 8 Passed: Schema isolation verified.");
        }
    }
}
