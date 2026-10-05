import java.util.HashMap;
import java.util.Map;

/**
 * Hospital Pharmacy Inventory Dispenser - Solution
 */
public class Solution {

    public static class PharmacyInventoryDispenser {
        private final Map<String, Integer> inventory = new HashMap<>();
        private final Object lock = new Object();

        public void restock(String medicationId, int units) {
            synchronized (lock) {
                int current = inventory.getOrDefault(medicationId, 0);
                inventory.put(medicationId, current + units);
            }
        }

        public boolean dispense(String medicationId, int units) {
            synchronized (lock) {
                int current = inventory.getOrDefault(medicationId, 0);
                if (current >= units) {
                    inventory.put(medicationId, current - units);
                    return true;
                }
                return false;
            }
        }

        public int getStock(String medicationId) {
            synchronized (lock) {
                return inventory.getOrDefault(medicationId, 0);
            }
        }
    }
}
