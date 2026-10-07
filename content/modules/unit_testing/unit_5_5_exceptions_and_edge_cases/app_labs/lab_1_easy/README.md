# Application Lab 1: Blood Bank Transfusion Compatibility Guard

## Clinical & Architectural Context
Blood transfusions save lives in operating rooms and trauma bays, but infusing incompatible packed red blood cells (PRBCs) triggers catastrophic intravascular hemolysis. Hospital blood bank software must enforce strict safety rules considering both ABO groups and Rhesus (Rh) factor antigens.

In this lab, you will design and test a **Blood Transfusion Compatibility Guard** in Java, asserting compatibility rules, expiration dates, edge case string inputs, and custom exception payloads.

---

## Architecture Overview

```
                      +-----------------------------------+
                      |            LabTests               |
                      |   - Universal Donor/Recipient     |
                      |   - ABO / Rh Incompatible Pairs  |
                      |   - Custom Exception Payloads     |
                      +-----------------+-----------------+
                                        |
                                        v
                      +-----------------------------------+
                      |   TransfusionCompatibilityGuard   |
                      |   - BloodProfile (ABO + Rh)       |
                      |   - authorizeTransfusion(...)     |
                      |   - TransfusionIncompatibilityEx  |
                      +-------------------+---------------+
```

---

## Lab Deliverables
1. **`StarterCode.java`**: Stubs and exception models.
2. **`tasks.md`**: Implementation specifications.
3. **`solution/Solution.java`**: Complete reference implementation.
4. **`LabTests.java`**: Comprehensive test suite.

---

## Verification
Compile and run:
```bash
javac -d .temp_bin content/modules/unit_testing/unit_5_5_exceptions_and_edge_cases/app_labs/lab_1_easy/solution/Solution.java content/modules/unit_testing/unit_5_5_exceptions_and_edge_cases/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
