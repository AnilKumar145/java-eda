# Application Lab 1: Pharmacy Medication Inventory Repository Test Harness

## Clinical & Architectural Context
In a hospital inpatient pharmacy, automated inventory cabinets must record every unit of medication stocked and dispensed. Stockouts of critical antibiotics or vasopressors risk patient lives, while unrecorded medication movements create hazardous reconciliation errors.

In this lab, you will engineer a **Pharmacy Medication Inventory Repository** in Java and verify it using an **in-memory H2 database test harness**. You will test transactional updates, stock dispenses, reorder threshold queries, total inventory valuation, and database constraints with zero test side-effects.

---

## Architecture Overview

```
                      +-----------------------------------+
                      |            LabTests               |
                      |   - In-Memory H2 DB Lifecycle     |
                      |   - Transaction Rollback Guard    |
                      +-----------------+-----------------+
                                        |
                                        v
                      +-----------------------------------+
                      |   PharmacyInventoryRepository     |
                      |   - CRUD Operations               |
                      |   - dispenseMedication(...)       |
                      |   - getItemsRequiringReorder()    |
                      |   - calculateInventoryValue()     |
                      +-----------------+-----------------+
                                        |
                                        v
                      +-----------------------------------+
                      |      In-Memory H2 Database        |
                      |   - inventory_items Table         |
                      |   - Unique SKU Constraint         |
                      +-----------------------------------+
```

---

## Lab Deliverables
1. **`StarterCode.java`**: Repository and model stubs.
2. **`tasks.md`**: Implementation specifications.
3. **`solution/Solution.java`**: Reference JDBC repository implementation.
4. **`LabTests.java`**: In-memory database test suite with rollback isolation.

---

## Verification
Compile and run:
```bash
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/unit_testing/unit_5_6_database_testing/app_labs/lab_1_easy/solution/Solution.java content/modules/unit_testing/unit_5_6_database_testing/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests
```
