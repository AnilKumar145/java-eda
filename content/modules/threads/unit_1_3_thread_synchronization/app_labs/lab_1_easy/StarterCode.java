import java.util.HashMap;
import java.util.Map;

/**
 * Hospital Pharmacy Inventory Dispenser - Starter Code
 */
public class StarterCode {

    public static class PharmacyInventoryDispenser {
        // TODO: Define internal inventory map and private lock object

        public void restock(String medicationId, int units) {
            // TODO: Synchronize and add units
        }

        public boolean dispense(String medicationId, int units) {
            // TODO: Synchronize and deduct units if stock >= units
            return false;
        }

        public int getStock(String medicationId) {
            // TODO: Synchronize and return current stock
            return 0;
        }
    }
}
