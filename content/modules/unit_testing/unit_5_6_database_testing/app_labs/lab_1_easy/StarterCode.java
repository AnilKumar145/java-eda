import java.sql.*;
import java.util.*;

public class StarterCode {

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
        public StockDiscrepancyException(String sku, int requested, int available) {
            super("Cannot dispense " + requested + " units of SKU '" + sku + "': only " + available + " units available.");
        }
    }

    public static class PharmacyInventoryRepository {
        public PharmacyInventoryRepository(Connection conn) {
            // TODO: Store connection.
        }

        public InventoryItem addItem(
            String sku,
            String name,
            String category,
            int quantity,
            int reorderThreshold,
            double unitCost
        ) throws SQLException {
            // TODO: Execute INSERT statement and return InventoryItem with generated ID.
            throw new UnsupportedOperationException("TODO: Implement addItem");
        }

        public Optional<InventoryItem> getBySku(String sku) throws SQLException {
            // TODO: SELECT item by uppercase SKU.
            throw new UnsupportedOperationException("TODO: Implement getBySku");
        }

        public InventoryItem dispenseMedication(String sku, int amount) throws SQLException {
            // TODO: Decrement quantity. If amount > quantity throw StockDiscrepancyException.
            throw new UnsupportedOperationException("TODO: Implement dispenseMedication");
        }

        public InventoryItem restockMedication(String sku, int amount) throws SQLException {
            // TODO: Increment quantity.
            throw new UnsupportedOperationException("TODO: Implement restockMedication");
        }

        public List<InventoryItem> getItemsRequiringReorder() throws SQLException {
            // TODO: SELECT items where quantity <= reorderThreshold ORDER BY quantity ASC.
            throw new UnsupportedOperationException("TODO: Implement getItemsRequiringReorder");
        }

        public double calculateInventoryValue() throws SQLException {
            // TODO: Return sum of (quantity * unitCost) rounded to 2 decimal places.
            throw new UnsupportedOperationException("TODO: Implement calculateInventoryValue");
        }
    }
}
