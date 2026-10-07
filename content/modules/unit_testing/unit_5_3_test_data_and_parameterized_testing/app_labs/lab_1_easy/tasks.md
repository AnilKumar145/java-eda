# Lab Tasks: Clinical Lab Test Reference Range and Reagent Harness

## Task 1: Declare Domain Models
In `Solution.java`:
- `record ReagentCartridge(String analyte, int remainingTests)`
- `record LabTestResult(String analyte, double value, String flag, int remainingReagents)`

## Task 2: Implement Chemistry Analyzer Logic
Implement `ChemistryAnalyzer`:
- `loadCartridge(String analyte, int initialTests)`: adds or replaces cartridge.
- `evaluateSample(String analyte, double value)`:
  - If cartridge missing or remainingTests <= 0, throw `IllegalStateException("Reagent cartridge depleted for " + analyte)`.
  - Decrement remainingTests by 1.
  - Classify value according to reference ranges:
    - `GLUCOSE`: 70.0 - 99.0 mg/dL (`LOW`, `NORMAL`, `HIGH`)
    - `POTASSIUM`: 3.5 - 5.0 mmol/L (`LOW`, `NORMAL`, `HIGH`)
    - `SODIUM`: 135.0 - 145.0 mmol/L (`LOW`, `NORMAL`, `HIGH`)
  - Return `LabTestResult`.
- `exportAuditReport(java.nio.file.Path destinationFile)`:
  - Writes summary of processed test count and remaining cartridge levels to destination file.

## Task 3: Author Verification Tests
In `LabTests.java`:
1. Parameterized reference range matrix test covering LOW, NORMAL, HIGH across GLUCOSE, POTASSIUM, and SODIUM.
2. Reagent consumption test verifying count decrements with each run, and throws `IllegalStateException` once depleted.
3. Temporary file export test using `Files.createTempDirectory()` verifying file creation, non-empty size, and proper cleanup.
