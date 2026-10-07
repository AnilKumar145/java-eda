import java.sql.*;
import java.util.*;

public class DatabaseTestingSolutions {

    public record Medication(long id, String name, int stock, double unitPrice) {}

    public static class MedicationRepository {
        private final Connection conn;

        public MedicationRepository(Connection conn) {
            this.conn = conn;
        }

        public Medication add(String name, int stock, double unitPrice) throws SQLException {
            String sql = "INSERT INTO medications (name, stock, unit_price) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.setInt(2, stock);
                stmt.setDouble(3, unitPrice);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        return new Medication(id, name, stock, unitPrice);
                    }
                }
            }
            throw new SQLException("Failed to retrieve generated key");
        }

        public Medication getById(long id) throws SQLException {
            String sql = "SELECT id, name, stock, unit_price FROM medications WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Medication(rs.getLong("id"), rs.getString("name"), rs.getInt("stock"), rs.getDouble("unit_price"));
                    }
                }
            }
            return null;
        }

        public List<Medication> findLowStock(int threshold) throws SQLException {
            String sql = "SELECT id, name, stock, unit_price FROM medications WHERE stock <= ? ORDER BY stock ASC";
            List<Medication> list = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, threshold);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(new Medication(rs.getLong("id"), rs.getString("name"), rs.getInt("stock"), rs.getDouble("unit_price")));
                    }
                }
            }
            return list;
        }

        public Medication updateStock(long id, int delta) throws SQLException {
            Medication existing = getById(id);
            if (existing == null) {
                throw new IllegalArgumentException("Medication ID " + id + " not found");
            }
            int newStock = existing.stock() + delta;
            if (newStock < 0) {
                throw new IllegalArgumentException("Insufficient stock: available " + existing.stock() + ", delta " + delta);
            }
            String sql = "UPDATE medications SET stock = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newStock);
                stmt.setLong(2, id);
                stmt.executeUpdate();
            }
            return new Medication(id, existing.name(), newStock, existing.unitPrice());
        }
    }

    // ==========================================
    // Test Harness Infrastructure
    // ==========================================

    private static final String H2_URL = "jdbc:h2:mem:pharmacy_exercises;DB_CLOSE_DELAY=-1";

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(H2_URL, "sa", "");
    }

    public static void initSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medications (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL UNIQUE,
                    stock INT NOT NULL,
                    unit_price DOUBLE NOT NULL
                )
            """);
        }
    }

    // ==========================================
    // Test Suites (with Rollback Isolation)
    // ==========================================

    public static void testAddAndRetrieveMedication() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                MedicationRepository repo = new MedicationRepository(conn);
                Medication med = repo.add("Amoxicillin 500mg", 150, 12.50);

                assert med.id() > 0 : "ID must be generated";
                assert "Amoxicillin 500mg".equals(med.name());

                Medication fetched = repo.getById(med.id());
                assert fetched != null : "Medication must be found";
                assert fetched.stock() == 150 : "Stock should be 150";
                System.out.println("Test 1 Passed: Add and retrieve medication verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testDuplicateMedicationNameThrowsSqlException() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                MedicationRepository repo = new MedicationRepository(conn);
                repo.add("Paracetamol 650mg", 200, 5.00);

                boolean caught = false;
                try {
                    repo.add("Paracetamol 650mg", 50, 5.00);
                } catch (SQLException e) {
                    caught = true;
                }
                assert caught : "Duplicate name must throw SQLException (UNIQUE constraint)";
                System.out.println("Test 2 Passed: Duplicate medication rejection verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testFindLowStockFiltersAndSortsCorrectly() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                MedicationRepository repo = new MedicationRepository(conn);
                repo.add("Insulin Glargine", 8, 45.00);
                repo.add("Atorvastatin 20mg", 45, 15.00);
                repo.add("Epinephrine 1mg", 4, 60.00);
                repo.add("Metformin 500mg", 120, 8.00);

                List<Medication> lowStock = repo.findLowStock(10);
                assert lowStock.size() == 2 : "Expected 2 items with stock <= 10";
                assert lowStock.get(0).name().equals("Epinephrine 1mg") : "First should be Epinephrine (stock 4)";
                assert lowStock.get(1).name().equals("Insulin Glargine") : "Second should be Insulin (stock 8)";
                System.out.println("Test 3 Passed: Low stock query filtering and sorting verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testUpdateStockAndInsufficientError() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            conn.setAutoCommit(false);
            try {
                MedicationRepository repo = new MedicationRepository(conn);
                Medication med = repo.add("Morphine 10mg", 10, 35.00);

                Medication updated = repo.updateStock(med.id(), -4);
                assert updated.stock() == 6 : "Expected stock 6 after dispensing 4";

                boolean caught = false;
                try {
                    repo.updateStock(med.id(), -10);
                } catch (IllegalArgumentException e) {
                    caught = true;
                }
                assert caught : "Should reject delta exceeding stock";
                assert repo.getById(med.id()).stock() == 6 : "Stock should remain 6";
                System.out.println("Test 4 Passed: Stock update and insufficient error verified.");
            } finally {
                conn.rollback();
            }
        }
    }

    public static void testIsolationRollbackLeavesEmptyTable() throws SQLException {
        try (Connection conn = createConnection()) {
            initSchema(conn);
            MedicationRepository repo = new MedicationRepository(conn);
            assert repo.findLowStock(1000).isEmpty() : "Previous rollbacks must leave medications table completely empty";
            System.out.println("Test 5 Passed: Zero side-effect transaction rollback isolation verified.");
        }
    }

    public static void main(String[] args) throws SQLException {
        testAddAndRetrieveMedication();
        testDuplicateMedicationNameThrowsSqlException();
        testFindLowStockFiltersAndSortsCorrectly();
        testUpdateStockAndInsufficientError();
        testIsolationRollbackLeavesEmptyTable();
        System.out.println("All Unit 5.6 Exercise Solutions Passed Successfully!");
    }
}
