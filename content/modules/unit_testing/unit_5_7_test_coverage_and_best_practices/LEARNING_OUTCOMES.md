# Learning Outcomes: Unit 5.7 - Test Coverage and Best Practices

By completing this unit, students will be able to:

1. **Measure Code Coverage with JaCoCo**:
   - Differentiate statement (line) coverage from branch (decision) coverage on the JVM.
   - Understand why high line coverage can still miss critical Boolean branches in emergency scoring algorithms.

2. **Establish Continuous Quality Gates**:
   - Enforce minimum branch coverage thresholds (e.g. 90% or 95%) in build tools (Maven/Gradle).
   - Configure exclusion patterns for generated boilerplate, DTO records, and configuration beans.

3. **Eliminate Test Flakiness & Fragility**:
   - Identify and remove unseeded random values, system clock dependencies, and static state leaks.
   - Refactor brittle tests that assert on internal implementation details into robust contract-based tests.

4. **Verify Cryptographic Invariants**:
   - Test append-only cryptographic ledgers using SHA-256 block hashing with 100% decision branch coverage.
