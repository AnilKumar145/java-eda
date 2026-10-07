# Unit 5.1: Software Testing Foundations, the AAA Pattern, and Test Isolation

---

## Part 1: Clinical & Conceptual Motivation

In healthcare software development, a single bug in dosage calculation or triage score calculation can endanger human life. Imagine an emergency department triage application: if a patient presenting with an abnormally high heart rate (e.g., 145 bpm) and critically low oxygen saturation (e.g., 86%) is mistakenly classified as "Non-Urgent" due to an arithmetic inversion error, that patient could wait hours in a lobby and suffer cardiac arrest.

Automated software testing is our primary defense against regressions and logic bugs. By verifying that every component of our healthcare systems behaves exactly as specified before deploying to production, we ensure patient safety, regulatory compliance (FDA/HIPAA/CE-mark), and high system reliability.

---

## Part 2: Core Concepts & Definitions

### 1. What is a Unit Test?
A **unit test** verifies the smallest testable piece of code (usually a single method or class) in total isolation from the rest of the application. Unit tests:
- Execute in memory in single-digit milliseconds.
- Do not make network calls, access physical disks, or query production databases.
- Are 100% deterministic: running a test 1,000 times produces the exact same outcome every time.

### 2. Unit vs. Integration vs. End-to-End (E2E) Testing
| Dimension | Unit Testing | Integration Testing | End-to-End Testing |
| :--- | :--- | :--- | :--- |
| **Scope** | Single class or method | Interaction between 2+ modules (e.g., Service + Database) | Full application stack (UI, backend, DB) |
| **Execution Speed** | Milliseconds | Seconds | Tens of seconds to minutes |
| **Isolation** | Completely isolated | Semi-isolated (in-memory DB or test containers) | Full live environment |
| **Debugging** | Pinpoints exact line of failure | Identifies wiring or contract mismatches | Hard to diagnose root cause |

### 3. The AAA Pattern (Arrange - Act - Assert)
Every clean unit test is structured into three clear phases:
1. **Arrange**: Set up the necessary objects, inputs, and preconditions.
2. **Act**: Invoke the single method under test.
3. **Assert**: Verify that the actual output matches the expected result.

---

## Part 3: Code Architecture & Implementation Patterns

### The AAA Pattern in Java
```java
public class TriageScorerTest {

    public void testCriticalHypoxiaScoresLevel1Resuscitation() {
        // 1. Arrange: Prepare clinical inputs
        PatientVitals vitals = new PatientVitals(75, 18, 84, 120); // SpO2 = 84% is critical
        TriageScorer scorer = new TriageScorer();

        // 2. Act: Execute the method under test
        TriageAssessment assessment = scorer.evaluate(vitals);

        // 3. Assert: Verify the clinical outcome
        assert assessment.getAcuityLevel() == 1 : "Expected Acuity Level 1 (Resuscitation) for SpO2 < 85%";
        assert assessment.isImmediateInterventionRequired() : "Immediate intervention must be flagged";
    }
}
```

### Test Isolation and Determinism
Tests must never depend on:
1. The execution order of other tests (test A must not modify static data that test B relies on).
2. The current wall-clock system time (use injected clocks or fixed timestamps).
3. Random numbers (always use fixed seeds).

---

## Part 4: Common Pitfalls & Anti-Patterns

### 1. The "Mystery Guest" Anti-Pattern
A test that depends on external data files or hidden global state that is not visible within the test method itself. Anyone reading the test cannot tell why it passed or failed.
*Fix*: Make all inputs explicit in the `Arrange` step.

### 2. Multiple Unrelated Actions in One Test
Executing five different method calls in a single test method. If the second action fails, the remaining three are never tested, masking multiple defects.
*Fix*: Follow the principle of **One Logical Concept per Test**.

### 3. Shared Mutable State (Static Leakage)
Storing shared lists or counters in `static` variables across test cases. If test 1 adds an item, test 2 fails unexpectedly when run after test 1.
*Fix*: Instantiate new objects per test method.

---

## Part 5: Production Patterns & Enterprise Recipes

### Pattern: Clinical Diagnostic Severity Evaluator
```java
public class ClinicalSeverityEvaluator {

    public enum Severity { MILD, MODERATE, CRITICAL }

    public record DiagnosticMeasurement(String biomarker, double value, double normalUpperLimit) {}

    public Severity evaluate(DiagnosticMeasurement measurement) {
        if (measurement.value() <= 0) {
            throw new IllegalArgumentException("Biomarker value must be strictly positive");
        }
        if (measurement.value() > measurement.normalUpperLimit() * 3.0) {
            return Severity.CRITICAL;
        } else if (measurement.value() > measurement.normalUpperLimit()) {
            return Severity.MODERATE;
        } else {
            return Severity.MILD;
        }
    }
}
```

---

## Part 6: Hands-On Walkthrough & Analysis

To test `ClinicalSeverityEvaluator` cleanly:
1. Test mild: value below upper limit (e.g. 5.0 vs 10.0 -> MILD).
2. Test moderate: value above upper limit but <= 3x (e.g. 15.0 vs 10.0 -> MODERATE).
3. Test critical: value above 3x upper limit (e.g. 35.0 vs 10.0 -> CRITICAL).
4. Test invalid: negative or zero value throws `IllegalArgumentException`.

---

## Part 7: Exercises & Application Lab Preview

- **Exercises (`exercises/`)**:
  - Implement and test emergency cardiac index scorers.
  - Apply the AAA pattern with custom assertion error messages.
  - Verify boundary conditions without shared static state.
- **Application Lab (`app_labs/lab_1_easy/`)**:
  - Build and verify an **Emergency Triage Patient Scoring Test Suite** that computes modified early warning scores (MEWS) and classifies patients across 4 clinical urgency levels.

---

## Part 8: Key Takeaways & Review Checklist

- [ ] Unit tests test individual methods in memory within milliseconds.
- [ ] Always follow Arrange, Act, Assert (AAA).
- [ ] Each test must be completely independent and order-agnostic.
- [ ] Meaningful assert error messages make diagnosing failures instantaneous.
