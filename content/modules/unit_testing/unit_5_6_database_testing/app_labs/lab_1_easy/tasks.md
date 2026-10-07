# Lab Tasks: Pharmacy Medication Inventory Repository Test Harness

## Task 1: Declare Models and Exceptions
In `Solution.java`:
- `record InventoryItem(long id, String sku, String name, String category, int quantity, int reorderThreshold, double unitCost)`
- `class StockDiscrepancyException extends RuntimeException`: contains `sku`, `requested`, `available`.

## Task 2: Implement Repository CRUD Methods
Implement `PharmacyInventoryRepository(Connection conn)`:
- `addItem(sku, name, category, quantity, reorderThreshold, unitCost) -> InventoryItem`: Inserts into `inventory_items` table (normalizing SKU to uppercase).
- `getBySku(sku) -> Optional<InventoryItem>`
- `dispenseMedication(sku, amount) -> InventoryItem`: Decrements quantity. Throws `IllegalArgumentException` if amount <= 0; throws `StockDiscrepancyException` if stock < amount.
- `restockMedication(sku, amount) -> InventoryItem`: Increments quantity.
- `getItemsRequiringReorder() -> List<InventoryItem>`: Returns items where `quantity <= reorderThreshold` ordered by quantity ASC.
- `calculateInventoryValue() -> double`: Returns sum of `quantity * unitCost` rounded to 2 decimal places.

## Task 3: Author In-Memory H2 Test Harness
In `LabTests.java`:
- Initialize in-memory H2 schema (`jdbc:h2:mem:pharmacy_lab;DB_CLOSE_DELAY=-1`).
- Wrap test cases with `conn.setAutoCommit(false)` and `conn.rollback()` in `finally`.
- Verify:
  1. Add and retrieve item by SKU.
  2. Duplicate SKU throws `SQLException`.
  3. Dispense reduces stock correctly.
  4. Dispense with insufficient stock throws `StockDiscrepancyException`.
  5. Reorder threshold query returns low-stock items sorted ascending.
  6. Calculate inventory value computes accurate sum.
  7. Verification of clean isolation between test runs.
