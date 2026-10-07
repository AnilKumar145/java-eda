# Application Lab 1: Clinical Lab Test Reference Range and Reagent Harness

## Clinical & Architectural Context
Modern hospital laboratories run high-throughput automated analyzers (e.g., Roche Cobas, Abbott Architect) processing thousands of serum samples per hour. Analyzers monitor reagent cartridge volumes, flag depleted consumables, classify results against analyte reference ranges, and export machine telemetry into audit files.

In this lab, you will design and test a **Clinical Chemistry Analyzer Engine** in Java. You will test analyte reference range matrices, reagent consumption and depletion states, and temporary file report generation.

---

## Architecture Overview

```
                      +-----------------------------------+
                      |            LabTests               |
                      |   - Parameterized Range Matrix    |
                      |   - Reagent Depletion Tests       |
                      |   - Temporary File Audit Tests    |
                      +-----------------+-----------------+
                                        |
                                        v
                      +-----------------------------------+
                      |        ChemistryAnalyzer          |
                      |   - ReagentCartridge Inventory    |
                      |   - evaluateSample(...)           |
                      |   - exportAuditReport(Path file)  |
                      +-----------------------------------+
```

---

## Lab Deliverables
1. **`StarterCode.java`**: Domain models and analyzer stubs.
2. **`tasks.md`**: Specification and requirements.
3. **`solution/Solution.java`**: Complete reference implementation.
4. **`LabTests.java`**: Automated test suite.

---

## Verification
Compile and run:
```bash
javac -d .temp_bin content/modules/unit_testing/unit_5_3_test_data_and_parameterized_testing/app_labs/lab_1_easy/solution/Solution.java content/modules/unit_testing/unit_5_3_test_data_and_parameterized_testing/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
