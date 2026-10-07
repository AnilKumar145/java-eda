# Application Lab 1: Clinical Audit Log Quality Gate and Coverage Harness

## Clinical & Architectural Context
Under HIPAA and medical software regulatory standards, any software system accessing electronic Protected Health Information (ePHI) must maintain an immutable, tamper-evident audit trail. In this lab, you will engineer a **cryptographically hashed clinical audit ledger** in Java and an accompanying **100% branch-coverage test suite**.

---

## Architecture Overview

```
                      +-----------------------------------+
                      |            LabTests               |
                      |   - 100% Branch Coverage          |
                      |   - Tamper Detection Test Cases   |
                      +-----------------+-----------------+
                                        |
                                        v
                      +-----------------------------------+
                      |       ClinicalAuditLedger         |
                      |   - Append-Only Event Chain       |
                      |   - SHA-256 Block Hashing         |
                      |   - Cryptographic Integrity Check |
                      +-----------------------------------+
```

---

## Lab Deliverables
1. **`StarterCode.java`**: Stubs and model declarations.
2. **`tasks.md`**: Specification and requirements.
3. **`solution/Solution.java`**: Complete reference implementation.
4. **`LabTests.java`**: Verification suite with 100% branch coverage.

---

## Verification
Compile and run:
```bash
javac -d .temp_bin content/modules/unit_testing/unit_5_7_test_coverage_and_best_practices/app_labs/lab_1_easy/solution/Solution.java content/modules/unit_testing/unit_5_7_test_coverage_and_best_practices/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
