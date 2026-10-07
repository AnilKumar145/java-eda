# Unit 5.7: Code Coverage Metrics, JaCoCo, and Quality Assurance Best Practices

---

## Part 1: Clinical & Conceptual Motivation

In healthcare compliance software (e.g., FDA 21 CFR Part 11, HIPAA ePHI audit trails), any undocumented or untested code path represents a patient safety risk and regulatory liability. Consider an electronic chemotherapy ordering engine: if an error-handling branch that checks for acute kidney injury (elevated creatinine clearance) is never executed during automated tests, a severe drug toxicity bug could go unnoticed into production.

Measuring **code coverage** provides transparency into which lines, branches, and instructions your test suite exercises. However, coverage numbers alone can create a false sense of security:
- A test that executes every line without writing any assertions achieves 100% line coverage but provides zero defect protection!
- Real quality requires **high branch coverage**, **rigorous assertions**, and **independent, isolated tests**.

---

## Part 2: Core Concepts & Definitions

### 1. Statement vs. Branch Coverage
- **Line/Instruction Coverage**: The percentage of JVM bytecode instructions executed at least once during test runs.
- **Branch/Decision Coverage**: Assesses whether every branch of conditional control structures (both `True` and `False` directions of every `if`, `switch`, and ternary operator) was exercised:
$$\text{Branch Coverage} = \frac{\text{Branches Evaluated}}{\text{Total Decision Branches}} \times 100\%$$

### 2. JaCoCo (Java Code Coverage)
JaCoCo is the standard code coverage library for Java. It uses runtime Java bytecode instrumentation (via a Java Agent) to track which bytecode basic blocks and jump instructions are executed during test runs, producing HTML, XML, and CSV reports.

### 3. CI/CD Quality Gates
Automated build rules that fail the build if coverage falls below a specified target:
```xml
<!-- Maven JaCoCo Plugin Quality Gate -->
<rule>
    <element>BUNDLE</element>
    <limits>
        <limit>
            <counter>BRANCH</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.90</minimum>
        </limit>
    </limits>
</rule>
```

---

## Part 3: Code Architecture & Implementation Patterns

### 1. Achieving 100% Branch Coverage on Compound Booleans
Consider the emergency triage rule:
```java
public String classify(int systolicBp, int heartRate, int spo2) {
    if (systolicBp < 90 || heartRate > 130 || spo2 < 90) {
        return "RED_RESUSCITATION";
    } else if (systolicBp < 100 || heartRate > 110 || spo2 < 94) {
        return "ORANGE_EMERGENT";
    } else if (systolicBp > 180 || heartRate > 100) {
        return "YELLOW_URGENT";
    } else {
        return "GREEN_NON_URGENT";
    }
}
```
To achieve 100% branch coverage:
- For the first `if`, test:
  1. `systolicBp < 90` is true (e.g., 85, 80, 98)
  2. `heartRate > 130` is true (e.g., 120, 135, 98)
  3. `spo2 < 90` is true (e.g., 120, 80, 88)
- Test each sub-condition of `ORANGE_EMERGENT`
- Test each sub-condition of `YELLOW_URGENT`
- Test nominal vitals reaching `GREEN_NON_URGENT`
- Test physiological invalid bounds.

---

## Part 4: Common Pitfalls & Anti-Patterns

### 1. "Assertion-Free" Coverage Tests
Invoking methods merely to trigger code paths without asserting return values or state mutations.
*Fix*: Every test method must include explicit assertions.

### 2. Flaky Tests
Tests that pass or fail nondeterministically due to:
- Relying on `System.currentTimeMillis()` or timezone differences
- Shared mutable static variables
- Thread timing races (`Thread.sleep(50)`)
*Fix*: Inject deterministic clocks and isolate state per test instance.

### 3. Testing Private Implementation Details
Using Java reflection to test `private` fields. When the class is refactored, the test breaks even though the public contract behaves identically.

---

## Part 5: Production Patterns & Enterprise Recipes

### Pattern: Cryptographic Audit Ledger with SHA-256 Chaining
Under HIPAA rules, every access to patient charts must be recorded in an immutable ledger where each block links to the previous block's SHA-256 hash:
```java
public record AuditRecord(
    String eventId,
    String actorId,
    String patientId,
    String action,
    String severity,
    String previousHash,
    String currentHash
) {}
```
By testing every decision path—including initial genesis block, subsequent chaining, input validation, and tampering detection—we achieve 100% branch coverage.

---

## Part 6: Hands-On Walkthrough & Analysis

Let's trace ledger verification:
1. Block 1: `previousHash = GENESIS_HASH`, `currentHash = sha256(...)`.
2. Block 2: `previousHash = Block1.currentHash`, `currentHash = sha256(...)`.
3. If an attacker modifies Block 1's action, Block 1's recomputed hash no longer matches its stored hash, or Block 2's `previousHash` is broken.
4. Calling `verifyIntegrity()` returns `false`.

---

## Part 7: Exercises & Application Lab Preview

- **Exercises (`exercises/`)**:
  - Test compound triage branches and audit trail access control.
- **Application Lab (`app_labs/lab_1_easy/`)**:
  - Build and verify the **Clinical Audit Log Quality Gate and Coverage Harness**, implementing SHA-256 tamper-evident chaining and 100% branch-covering test suites.

---

## Part 8: Key Takeaways & Review Checklist

- [ ] Line coverage measures executed lines; branch coverage measures decision outcomes.
- [ ] Strive for high branch coverage backed by strict assertions.
- [ ] Build deterministic, isolated tests without clock or static state dependencies.
- [ ] Test the happy path, boundary edges, and failure branches systematically.
