# Lab 1 Tasks: Hospital Pharmacy Inventory Dispenser (Java)

Follow these steps to complete `StarterCode.java`.

---

### Task 1: Create `PharmacyInventoryDispenser`
1. Use an internal `Map<String, Integer> inventory = new HashMap<>()`.
2. Use a dedicated `private final Object lock = new Object()` for synchronization.
3. Implement `public void restock(String medicationId, int units)`:
   - Synchronize on `lock`.
   - Add `units` to the current stock of `medicationId`.
4. Implement `public boolean dispense(String medicationId, int units)`:
   - Synchronize on `lock`.
   - Check if current stock of `medicationId` is `>= units`.
   - If yes, decrement stock by `units` and return `true`.
   - If no, return `false` without modifying stock.
5. Implement `public int getStock(String medicationId)`:
   - Synchronize on `lock`.
   - Return the current stock (defaulting to 0 if not present).
