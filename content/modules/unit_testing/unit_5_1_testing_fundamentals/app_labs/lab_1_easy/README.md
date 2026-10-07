# Application Lab 1: Emergency Triage Patient Scoring Test Suite

## Clinical & Architectural Context
In an emergency department, rapid triage categorization dictates which patients receive immediate resuscitation and which can safely wait in sub-acute areas. Errors in vital sign thresholding or cumulative score calculation can have fatal consequences.

In this lab, you will design and test an **Emergency Triage Scoring Engine** in Java. You will verify triage level assignments across 4 acuity tiers using the **Arrange-Act-Assert (AAA)** pattern with deterministic, isolated test cases.

---

## Architecture Overview

```
                      +-----------------------------------+
                      |            LabTests               |
                      |   - Arrange, Act, Assert          |
                      |   - Pure In-Memory Verification   |
                      +-----------------+-----------------+
                                        |
                                        v
                      +-----------------------------------+
                      |       ClinicalTriageEngine        |
                      |   - MEWS Vital Sign Scoring       |
                      |   - Tier 1: Resuscitation (Score>=7)|
                      |   - Tier 2: Emergent (Score 4-6)  |
                      |   - Tier 3: Urgent (Score 1-3)    |
                      |   - Tier 4: Non-Urgent (Score 0)  |
                      +-----------------------------------+
```

---

## Lab Deliverables
1. **`StarterCode.java`**: Stubs and score calculation interfaces.
2. **`tasks.md`**: Implementation and testing specifications.
3. **`solution/Solution.java`**: Reference implementation.
4. **`LabTests.java`**: Standalone AAA test suite.

---

## Verification
Compile and run with Java assertions enabled:
```bash
javac -d .temp_bin content/modules/unit_testing/unit_5_1_testing_fundamentals/app_labs/lab_1_easy/solution/Solution.java content/modules/unit_testing/unit_5_1_testing_fundamentals/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
