# Unit 5.2: JUnit 5 Lifecycle, Assertions API, and Test Execution Model

---

## Part 1: Clinical & Conceptual Motivation

In hospital electronic prescribing and pediatric dosing, weight-based calculations require zero tolerance for calculation mistakes. If an 8 kg infant is prescribed intravenous ampicillin at 50 mg/kg, the total dose must be 400 mg. If a unit conversion error produces 4,000 mg (a 10-fold overdose), the infant faces lethal renal toxicity.

JUnit is the ubiquitous testing framework of the Java ecosystem. In this lesson, we explore how JUnit 5 structures test suites, manages lifecycle callbacks, and provides assertions that turn medical algorithms into verifiable code specifications.

---

## Part 2: Core Concepts & Definitions

### 1. JUnit 5 Architecture
JUnit 5 is composed of three distinct sub-projects:
- **JUnit Platform**: The foundation for launching testing frameworks on the JVM (used by IDEs, Maven, and Gradle).
- **JUnit Jupiter**: The modern programming model and extension framework (`@Test`, `@BeforeEach`, `Assertions`).
- **JUnit Vintage**: The backward-compatibility engine for running JUnit 3 and JUnit 4 tests.

### 2. Test Lifecycle Hooks
| Annotation | Execution Timing | Scope | Typical Usage |
| :--- | :--- | :--- | :--- |
| `@BeforeAll` | Once before all tests in class | `static` method | Heavy resource initialization (test DB container) |
| `@BeforeEach` | Before every `@Test` method | Instance method | Resetting domain objects to pristine state |
| `@Test` | Executes the test logic | Instance method | The unit test |
| `@AfterEach` | After every `@Test` method | Instance method | Cleaning up temporary files, resetting mocks |
| `@AfterAll` | Once after all tests in class | `static` method | Disposing heavy shared resources |

### 3. Lifecycle Instance Per Method
By default, JUnit creates a **fresh instance of the test class for each test method**. This ensures that instance variables modified in Test 1 cannot leak into Test 2.

---

## Part 3: Code Architecture & Implementation Patterns

### Core Assertions API
```java
import static org.junit.jupiter.api.Assertions.*;

class DosageCalculatorTest {

    @Test
    void testStandardPediatricDose() {
        double result = PediatricDosageCalculator.calculateMg(10.0, 15.0);
        // assertEquals(expected, actual, delta, message)
        assertEquals(150.0, result, 0.001, "10 kg at 15 mg/kg should yield 150 mg");
    }

    @Test
    void testGroupedAssertionsWithAssertAll() {
        DoseRecommendation rec = PediatricDosageCalculator.recommend(12.0, "AMOXICILLIN");
        
        // assertAll runs all assertions and reports all failures together
        assertAll("Pediatric Amoxicillin Recommendation",
            () -> assertEquals(300.0, rec.totalDoseMg(), 0.001, "Dose mg mismatch"),
            () -> assertEquals(6.0, rec.volumeMl(), 0.001, "Syrup volume mL mismatch"),
            () -> assertFalse(rec.exceedsAdultCap(), "Should not exceed adult cap")
        );
    }
}
```

### Exception Verification with `assertThrows`
```java
@Test
void testNegativeWeightThrowsException() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> PediatricDosageCalculator.calculateMg(-5.0, 15.0),
        "Negative weight must be rejected"
    );
    assertTrue(exception.getMessage().contains("must be strictly positive"));
}
```

---

## Part 4: Common Pitfalls & Anti-Patterns

### 1. Inverted Arguments in `assertEquals`
Putting the actual value first: `assertEquals(actual, expected)`. When the test fails, JUnit's error message will read:
`Expected: <actual_value> but was: <expected_value>`, confusing developers during debugging.
*Rule*: Always `assertEquals(expected, actual)`.

### 2. Missing Floating-Point Epsilon (Delta)
Comparing floating-point values without delta: `assertEquals(150.0, 150.000000001)`. Due to IEEE 754 precision, this will frequently fail unpredictably.
*Fix*: Always supply an epsilon: `assertEquals(150.0, actual, 0.001)`.

### 3. Asserting Only Exception Type Without Checking Context
Using `assertThrows(Exception.class, ...)` without validating the specific exception message or sub-type. A `NullPointerException` could pass the test even if the code was supposed to throw a domain validation error.

---

## Part 5: Production Patterns & Enterprise Recipes

### Pattern: Self-Resetting Test Fixtures with `@BeforeEach`
```java
public class InfusionPumpTestHarness {

    private InfusionPump pump;

    @BeforeEach
    void setUp() {
        // Guarantee every test receives a freshly calibrated pump with zero occlusion
        this.pump = new InfusionPump(100.0 /* ml/hr */);
    }

    @AfterEach
    void tearDown() {
        if (this.pump.isRunning()) {
            this.pump.emergencyStop();
        }
    }
}
```

---

## Part 6: Hands-On Walkthrough & Analysis

Let's trace a pediatric dosage calculator test case:
1. Arrange: Patient weight = 16.0 kg, prescribed dose rate = 25 mg/kg, adult maximum ceiling = 500 mg.
2. Act: $16 \times 25 = 400$ mg. Since $400 < 500$, capped = false.
3. Assert: `assertEquals(400.0, result.doseMg(), 0.001)`.
4. Now test adult ceiling: Patient weight = 30.0 kg. $30 \times 25 = 750$ mg. Capped at 500 mg.
5. Assert: `assertEquals(500.0, result.doseMg(), 0.001)` and `assertTrue(result.cappedAtAdultMax())`.

---

## Part 7: Exercises & Application Lab Preview

- **Exercises (`exercises/`)**:
  - Implement and test insulin bolus calculators.
  - Test grouped assertions and exception throwing.
- **Application Lab (`app_labs/lab_1_easy/`)**:
  - Build and verify the **Pediatric Dosage Calculator Test Harness**, testing liquid volume computations, concentration conversions, and adult ceiling constraints.

---

## Part 8: Key Takeaways & Review Checklist

- [ ] JUnit Jupiter uses `@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`.
- [ ] By default, test instances are created per test method (`PER_METHOD`).
- [ ] In `assertEquals`, pass `expected` first, `actual` second, and specify epsilon for doubles.
- [ ] Use `assertAll` to see all failures across a complex clinical object at once.
