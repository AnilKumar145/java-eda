# Application Lab 1: Pediatric Dosage Calculator Test Harness

## Clinical & Architectural Context
Pediatric patients are not simply miniature adults; their pharmacokinetic metabolism varies dramatically by body mass. Medications are formulated as liquid syrups or suspensions where physicians order a dosage in milligrams per kilogram, and nurses administer liquid volumes in milliliters (mL).

In this lab, you will engineer a **Pediatric Dosage Calculator Test Harness** in Java, validating liquid volume calculations, concentration conversions, ceiling clamps against maximum adult doses, and comprehensive exception assertions.

---

## Architecture Overview

```
                      +-----------------------------------+
                      |            LabTests               |
                      |   - Setup & Teardown Lifecycle    |
                      |   - Boundary & Exception Tests    |
                      +-----------------+-----------------+
                                        |
                                        v
                      +-----------------------------------+
                      |    PediatricDosageCalculator      |
                      |   - calculateLiquidVolume(...)    |
                      |   - Uncapped Standard Dosing      |
                      |   - Adult Maximum Ceiling Clamp   |
                      +-----------------------------------+
```

---

## Lab Deliverables
1. **`StarterCode.java`**: Domain models and calculation interface stubs.
2. **`tasks.md`**: Implementation and test specifications.
3. **`solution/Solution.java`**: Complete reference implementation.
4. **`LabTests.java`**: Structured test suite verifying calculations and assertions.

---

## Verification
Compile and run:
```bash
javac -d .temp_bin content/modules/unit_testing/unit_5_2_junit_fundamentals/app_labs/lab_1_easy/solution/Solution.java content/modules/unit_testing/unit_5_2_junit_fundamentals/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
