# Learning Outcomes: Unit 5.1 - Testing Fundamentals

By completing this unit, students will be able to:

1. **Differentiate Unit, Integration, and System Tests**:
   - Understand the boundaries of unit testing: testing isolated units in milliseconds without real network or database calls.
   - Contrast unit tests with integration and end-to-end tests across the software pyramid.

2. **Apply the Arrange-Act-Assert (AAA) Pattern**:
   - Structure readable, robust test cases into 3 explicit phases: Arrange (setup), Act (execution), Assert (verification).
   - Write clear failure messages that pinpoint the exact clinical invariant violated.

3. **Ensure Deterministic Test Isolation**:
   - Eliminate shared mutable state, static field bleed, and order-dependent test failures.
   - Design test cases that pass independently in any execution sequence.

4. **Practice Test-Driven Development (TDD)**:
   - Follow the Red-Green-Refactor cycle: write a failing test first, write minimal implementation code to pass, and refactor cleanly.
