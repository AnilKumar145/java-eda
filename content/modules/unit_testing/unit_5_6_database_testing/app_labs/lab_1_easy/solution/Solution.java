import java.sql.*;
import java.util.*;

public class Solution {

    public record InventoryItem(
        long id,
        String sku,
        String name,
        String category,
        int quantity,
        int reorderThreshold,
        double unitCost
    ) {}

    public static class StockDiscrepancyException extends RuntimeException {
        private final String sku;
        private final int requested;
        private final int available;

        public StockDiscrepancyException(String sku, int requested, int available) {
            super("Cannot dispense " + requested + " units of SKU '" + sku + "': only " + available + " units available.");
            this.sku = sku;
            this.requested = requested;
            this.available = available;
        }

        public String getSku() { return sku; }
        public int getRequested() { return requested; }
        public int getAvailable() { return available; }
    }

    public static class PharmacyInventoryRepository {
        private final Connection conn;

        public PharmacyInventoryRepository(Connection conn) {
            this.conn = conn;
        }

        public InventoryItem addItem(
            String sku,
            String name,
            String category,
            int quantity,
            int reorderThreshold,
            double unitCost
        ) throws SQLException {
            String normSku = sku.trim().toUpperCase();
            String sql = """
                INSERT INTO inventory_items (sku, name, category, quantity, reorder_threshold, unit_cost)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, normSku);
                stmt.setString(2, name.trim());
                stmt.setString(3, category.trim().toUpperCase());
                stmt.setInt(4, quantity);
                stmt.setInt(5, reorderThreshold);
                stmt.setDouble(6, unitCost);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        return new InventoryItem(id, normSku, name.trim(), category.trim().toUpperCase(), quantity, reorderThreshold, unitCost);
                    }
                }
            }
            throw new SQLException("Failed to retrieve generated key for item: " + sku);
        }

        public Optional<InventoryItem> getBySku(String sku) throws SQLException {
            String normSku = sku.trim().toUpperCase();
            String sql = "SELECT id, sku, name, category, quantity, reorder_threshold, unit_cost FROM inventory_items WHERE sku = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, normSku);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(new InventoryItem(
                            rs.getLong("id"),
                            rs.getString("sku"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getInt("reorder_threshold"),
                            rs.getDouble("unit_cost")
                        ));
                    }
                }
            }
            return Optional.empty();
        }

        public InventoryItem dispenseMedication(String sku, int amount) throws SQLException {
            if (amount <= 0) {
                throw new IllegalArgumentException("Dispense amount must be strictly positive: " + amount);
            }
            InventoryItem item = getBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Medication SKU '" + sku + "' not found"));

            if (item.quantity() < amount) {
                throw new StockDiscrepancyException(item.sku(), amount, item.quantity());
            }

            int newQty = item.quantity() - amount;
            String sql = "UPDATE inventory_items SET quantity = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newQty);
                stmt.setLong(2, item.id());
                stmt.executeUpdate();
            }

            return new InventoryItem(item.id(), item.sku(), item.name(), item.category(), newQty, item.reorderThreshold(), item.unitCost());
        }

        public InventoryItem restockMedication(String sku, int amount) throws SQLException {
            if (amount <= 0) {
                throw new IllegalArgumentException("Restock amount must be strictly positive: " + amount);
            }
            InventoryItem item = getBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Medication SKU '" + sku + "' not found"));

            int newQty = item.quantity() + amount;
            String sql = "UPDATE inventory_items SET quantity = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newQty);
                stmt.setLong(2, item.id());
                stmt.executeUpdate();
            }

            return new InventoryItem(item.id(), item.sku(), item.name(), item.category(), newQty, item.reorderThreshold(), item.unitCost());
        }

        public List<InventoryItem> getItemsRequiringReorder() throws SQLException {
            String sql = "SELECT id, sku, name, category, quantity, reorder_threshold, unit_cost FROM inventory_items WHERE quantity <= reorder_threshold ORDER BY quantity ASC";
            List<InventoryItem> list = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new InventoryItem(
                        rs.getLong("id"),
                        rs.getString("sku"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getInt("reorder_threshold"),
                        rs.getDouble("unit_cost")
                    ));
                }
            }
            return list;
        }

        public double calculateInventoryValue() throws SQLException {
            String sql = "SELECT SUM(quantity * unit_cost) FROM inventory_items";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double val = rs.getDouble(1);
                    return Math.round(val * 100.0) / 100.0;
                }
            }
            return 0.0;
        }
    }
}
