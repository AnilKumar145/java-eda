# Unit 5.3: Parameterized Testing, Test Data Builders, and Temporary Resources

---

## Part 1: Clinical & Conceptual Motivation

In hospital laboratory information systems (LIS), clinical biochemistry analyzers evaluate dozens of blood analytes—such as serum glucose, creatinine, electrolytes, and liver enzymes. Each analyte has a strict reference interval (e.g., normal serum sodium: 135 to 145 mmol/L). Values below this range indicate hyponatremia (`LOW`), within range indicate normal homeostasis (`NORMAL`), and above range indicate hypernatremia (`HIGH`).

Writing separate unit test methods for every possible analyte and every boundary condition would lead to massive code duplication and maintenance headaches. **Parameterized testing** enables developers to write a single parameterized test method that executes automatically against an entire table of clinical test cases. Coupled with **Test Data Builders** and **temporary file management**, we can test complex laboratory workloads cleanly and safely.

---

## Part 2: Core Concepts & Definitions

### 1. Parameterized Testing in JUnit 5
A test method annotated with `@ParameterizedTest` that receives arguments from one or more data sources:
- `@ValueSource`: Supplies an array of literal values (integers, strings, doubles).
- `@CsvSource`: Supplies comma-delimited strings parsed into multiple method parameters.
- `@MethodSource`: References a factory method returning a `Stream`, `List`, or `Iterable` of complex arguments.

### 2. The Test Data Builder Pattern
A creational design pattern tailored for tests. Instead of calling telescoping constructors with dozens of `null` or placeholder arguments, a builder supplies sensible clinical defaults while allowing test cases to override only the fields relevant to the specific test scenario:

```java
Patient patient = new PatientBuilder()
    .withAge(65)
    .withDiagnosis("TYPE_2_DIABETES")
    .build();
```

### 3. Temporary Resources with `@TempDir`
In JUnit 5, annotating a `Path` or `File` parameter with `@TempDir` creates an isolated directory in the system temporary folder before test execution and recursively deletes it on test completion.

---

## Part 3: Code Architecture & Implementation Patterns

### 1. Multi-Column Parameterized Testing with `@CsvSource`
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChemistryReferenceRangeTest {

    @ParameterizedTest(name = "{0}: value {1} should be {2}")
    @CsvSource({
        "GLUCOSE, 65.0,  LOW",
        "GLUCOSE, 85.0,  NORMAL",
        "GLUCOSE, 140.0, HIGH",
        "SODIUM,  130.0, LOW",
        "SODIUM,  140.0, NORMAL",
        "SODIUM,  152.0, HIGH"
    })
    void testAnalyteClassification(String analyte, double value, String expectedStatus) {
        String actual = ClinicalAnalyzer.classify(analyte, value);
        assertEquals(expectedStatus, actual);
    }
}
```

### 2. The Test Data Builder Pattern
```java
public class LabSampleBuilder {
    private String sampleId = "SMP-DEFAULT-001";
    private String patientId = "PAT-100";
    private String analyte = "POTASSIUM";
    private double measuredValue = 4.2; // mmol/L (Normal)
    private boolean hemolyzed = false;

    public LabSampleBuilder withAnalyte(String analyte) {
        this.analyte = analyte;
        return this;
    }

    public LabSampleBuilder withMeasuredValue(double value) {
        this.measuredValue = value;
        return this;
    }

    public LabSampleBuilder asHemolyzed() {
        this.hemolyzed = true;
        return this;
    }

    public LabSample build() {
        return new LabSample(sampleId, patientId, analyte, measuredValue, hemolyzed);
    }
}
```

---

## Part 4: Common Pitfalls & Anti-Patterns

### 1. Incomplete Test Matrices
Testing only the happy path without including lower boundary, exact lower threshold, within-range, exact upper threshold, and upper boundary values.

### 2. File Leakage Without Isolated Temporary Directories
Writing test files directly to hardcoded paths like `/tmp/report.csv` or `C:\temp\data.csv`. If tests fail midway or run concurrently in CI, files collide, fail randomly, or leave gigabytes of residual junk on disk.
*Fix*: Always use JUnit's `@TempDir` or `Files.createTempDirectory()`.

### 3. Over-Engineering Builders (The Giant God-Builder)
Adding business validation inside test builders that prevents testing invalid states. Test builders should permit setting invalid or boundary fields specifically so developers can verify that the system under test rejects them.

---

## Part 5: Production Patterns & Enterprise Recipes

### Pattern: Self-Cleaning Temporary Audit Log Export
```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LabAuditExportTest {

    @Test
    void testAuditLogWrittenToDisk(@TempDir Path tempDir) throws Exception {
        Path exportFile = tempDir.resolve("lab_audit.json");

        AuditExporter.exportRecords(exportFile, List.of(
            new AuditRecord("EVT-1", "CALIBRATION_OK"),
            new AuditRecord("EVT-2", "SPECIMEN_PROCESSED")
        ));

        assertTrue(Files.exists(exportFile), "Audit export file must exist");
        assertTrue(Files.size(exportFile) > 0, "File should not be empty");
    }
}
```

---

## Part 6: Hands-On Walkthrough & Analysis

Let's trace how a reference range classifier is tested with 9 boundary points:
- Glucose (normal 70.0 - 99.0 mg/dL):
  - 69.9 -> `LOW`
  - 70.0 -> `NORMAL`
  - 85.0 -> `NORMAL`
  - 99.0 -> `NORMAL`
  - 99.1 -> `HIGH`
By organizing these into parameterized matrices, each boundary point is tested independently and reported clearly in test reports.

---

## Part 7: Exercises & Application Lab Preview

- **Exercises (`exercises/`)**:
  - Implement and test analyte range evaluators across multi-column test data sets.
  - Implement a `LabOrderBuilder` with fluent configuration.
- **Application Lab (`app_labs/lab_1_easy/`)**:
  - Build and verify the **Clinical Lab Test Reference Range and Reagent Harness**, testing analyzer reagent consumption, depletion flags, and CSV/JSON reporting using isolated temporary directories.

---

## Part 8: Key Takeaways & Review Checklist

- [ ] Use `@ParameterizedTest` with `@ValueSource` or `@CsvSource` to run test matrices cleanly.
- [ ] Implement Test Data Builders to decouple test scenarios from constructor changes.
- [ ] Use `@TempDir` to guarantee pristine file system state per test.
- [ ] Parameterized tests must include both strict boundary edges and middle-of-the-road values.
