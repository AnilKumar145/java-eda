# Learning Outcomes: Unit 5.3 - Test Data and Parameterized Testing

By completing this unit, students will be able to:

1. **Implement Parameterized Test Suites in JUnit 5**:
   - Eliminate repetitive test methods by feeding arrays of clinical arguments via `@ParameterizedTest`.
   - Use `@ValueSource` for scalar inputs, `@CsvSource` for multi-column inputs, and `@MethodSource` for complex domain objects.

2. **Structure Maintainable Test Data with Enterprise Creational Patterns**:
   - Implement the **Test Data Builder Pattern** to generate complex, valid medical records with fluent overriding methods.
   - Implement the **Object Mother Pattern** to provide standardized domain presets (e.g., `PatientMothers.diabeticHypertensiveElderly()`).

3. **Manage Temporary File Resources with `@TempDir`**:
   - Create isolated temporary directories and files on disk that JUnit cleans up automatically on teardown.
   - Test CSV/JSON export and import operations without polluting the developer's filesystem.
