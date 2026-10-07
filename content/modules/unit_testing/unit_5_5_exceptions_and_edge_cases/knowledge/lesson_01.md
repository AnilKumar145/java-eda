# Unit 5.5: Exception Assertions, Boundary Value Analysis, and Custom Error Payloads

---

## Part 1: Clinical & Conceptual Motivation

In hospital blood transfusion medicine, administering incompatible blood (such as infusing Type A packed red blood cells into a Type B patient) triggers acute intravascular hemolytic reaction. The recipient's antibodies destroy donor red blood cells, causing acute renal failure, disseminated intravascular coagulation (DIC), and death.

To guarantee zero tolerance for errors, healthcare software cannot simply log a warning; it must **throw explicit, typed exceptions** that immediately halt clinical workflows and trigger safety interlocks. Furthermore, automated unit test suites must rigorously prove that:
1. Every illegal transfusion pair throws a specific, typed exception.
2. The exception contains diagnostic metadata for forensic root-cause analysis.
3. Every edge case (nulls, whitespace strings, expired units) fails safely.

---

## Part 2: Core Concepts & Definitions

### 1. Exception Testing with `assertThrows`
In JUnit 5, `assertThrows(Class<T> expectedType, Executable executable)` executes the provided lambda and verifies that it throws an exception of type `T` (or a subclass). It returns the thrown exception instance for further assertions.

### 2. Custom Domain Exception Payloads
Instead of throwing generic `RuntimeException` or `IllegalArgumentException`, medical systems throw rich, domain-specific exceptions carrying context:
```java
public class IncompatibleTransfusionException extends RuntimeException {
    private final BloodProfile donor;
    private final BloodProfile recipient;
    private final String clinicalReason;

    public IncompatibleTransfusionException(BloodProfile donor, BloodProfile recipient, String reason) {
        super("Incompatible blood transfusion: " + reason);
        this.donor = donor;
        this.recipient = recipient;
        this.clinicalReason = reason;
    }
    // Getters for forensic audit logging
}
```

### 3. Boundary Value Analysis (BVA)
Most programming errors occur at the edges of equivalence partitions. If patient age is valid from 0 to 125:
- Lower boundary: `-1` (Invalid), `0` (Valid), `1` (Valid)
- Upper boundary: `124` (Valid), `125` (Valid), `126` (Invalid)

---

## Part 3: Code Architecture & Implementation Patterns

### 1. Asserting Custom Exception Payloads
```java
@Test
void testAboIncompatibilityThrowsCustomExceptionWithMetadata() {
    BloodProfile donor = new BloodProfile("B", true);
    BloodProfile recipient = new BloodProfile("A", true);

    IncompatibleTransfusionException ex = assertThrows(
        IncompatibleTransfusionException.class,
        () -> TransfusionGuard.authorize(donor, recipient),
        "B to A transfusion must be blocked"
    );

    // Verify structured payload fields, not just message text
    assertEquals("B", ex.getDonor().abo());
    assertEquals("A", ex.getRecipient().abo());
    assertTrue(ex.getClinicalReason().contains("ABO mismatch"));
}
```

### 2. Testing Boundary Values
```java
@ParameterizedTest
@ValueSource(doubles = {0.9, 500.1, -15.0})
void testWeightBoundaryViolationsThrowIllegalArgumentException(double invalidWeight) {
    assertThrows(
        IllegalArgumentException.class,
        () -> DosageValidator.validateWeight(invalidWeight)
    );
}
```

---

## Part 4: Common Pitfalls & Anti-Patterns

### 1. "Swallowing" Exceptions in Tests
Using a `try-catch` block inside a test method without re-asserting or failing:
```java
// ANTI-PATTERN: If the method does NOT throw, the test PASSES!
try {
    calc.divide(10, 0);
} catch (ArithmeticException e) {
    // caught!
}
```
*Fix*: Always use `assertThrows` or call `fail("Expected exception was not thrown")` inside `try`.

### 2. Asserting Only on Error Message Substrings
Checking `e.getMessage().equals("Error")`. If a developer refactors the message text, tests break even though the error logic is completely valid. Always assert on typed exceptions and structured getter properties.

### 3. Neglecting "Zero" and Empty Strings
Testing `"ABC"` and `"XYZ"` but forgetting `""`, `"   "`, `null`, `0`, and `-1`.

---

## Part 5: Production Patterns & Enterprise Recipes

### Pattern: Blood Transfusion ABO and Rh Compatibility Matrix
```java
public class TransfusionGuard {

    private static final Map<String, Set<String>> ABO_COMPATIBILITY = Map.of(
        "O", Set.of("O", "A", "B", "AB"),
        "A", Set.of("A", "AB"),
        "B", Set.of("B", "AB"),
        "AB", Set.of("AB")
    );

    public static boolean checkCompatibility(BloodProfile donor, BloodProfile recipient) {
        // Rh check: Rh- can donate to Rh- and Rh+; Rh+ can ONLY donate to Rh+
        if (donor.rhPositive() && !recipient.rhPositive()) {
            throw new IncompatibleTransfusionException(donor, recipient, "Rh incompatibility: Rh+ to Rh-");
        }

        Set<String> compatibleRecipients = ABO_COMPATIBILITY.get(donor.abo());
        if (!compatibleRecipients.contains(recipient.abo())) {
            throw new IncompatibleTransfusionException(donor, recipient, "ABO incompatibility: " + donor.abo() + " to " + recipient.abo());
        }

        return true;
    }
}
```

---

## Part 6: Hands-On Walkthrough & Analysis

Let's trace test cases for blood transfusion safety:
1. Universal donor: O Negative (donor) to AB Positive (recipient) -> `True`.
2. Rh mismatch: A Positive (donor) to A Negative (recipient) -> `IncompatibleTransfusionException` ("Rh incompatibility").
3. ABO mismatch: B Negative (donor) to A Negative (recipient) -> `IncompatibleTransfusionException` ("ABO incompatibility").
4. Malformed blood group input: `"X"`, `""`, `null` -> `IllegalArgumentException`.

---

## Part 7: Exercises & Application Lab Preview

- **Exercises (`exercises/`)**:
  - Implement and test BMI boundary thresholds and blood gas exceptions.
- **Application Lab (`app_labs/lab_1_easy/`)**:
  - Build and verify the **Blood Bank Transfusion Compatibility Guard**, asserting ABO compatibility, Rh factor matching, and custom exception payloads.

---

## Part 8: Key Takeaways & Review Checklist

- [ ] Use `assertThrows` to verify expected error conditions.
- [ ] Inspect the returned exception object for specific metadata and message patterns.
- [ ] Implement typed domain exceptions rather than generic exceptions for safety-critical logic.
- [ ] Test the exact boundaries: one unit below, exactly on the bound, and one unit above.
