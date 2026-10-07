# Learning Outcomes: Unit 5.5 - Exceptions and Edge Cases

By completing this unit, students will be able to:

1. **Verify Expected Exceptions with `assertThrows`**:
   - Assert that invalid inputs, missing prerequisites, or illegal states throw precisely expected exception classes.
   - Capture the thrown exception instance to validate diagnostic messages and error payloads.

2. **Engineer Custom Domain Exception Hierarchies**:
   - Design typed exceptions holding clinical metadata (e.g., `IncompatibleTransfusionException` carrying donor and recipient blood profiles).
   - Assert on structured getters within the custom exception rather than relying purely on brittle text matching.

3. **Apply Boundary Value Analysis (BVA)**:
   - Identify critical input thresholds (just below lower bound, exact lower bound, nominal, exact upper bound, just above upper bound).
   - Systematically author negative test cases that prove the system aggressively rejects invalid boundaries.

4. **Validate Fail-Safe Invariants**:
   - Verify that unexpected conditions (empty strings, whitespace-only strings, null values, expired medical products) fail fast before corrupting clinical databases.
