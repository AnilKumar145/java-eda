# Learning Outcomes: Unit 5.2 - JUnit Fundamentals

By completing this unit, students will be able to:

1. **Navigate the JUnit 5 (Jupiter) Architecture**:
   - Understand the decoupling of the JUnit Platform runner, the Jupiter programming model, and the Vintage backward-compatibility engine.
   - Author `@Test` methods with descriptive `@DisplayName` annotations.

2. **Utilize the Full Range of JUnit Assertions**:
   - Use `assertEquals`, `assertNotEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`.
   - Apply `assertAll` (grouped assertions) to report multiple failure points simultaneously rather than stopping at the first failing assert.
   - Employ `assertThrows` to test expected exceptions and validate exception messages.

3. **Master the Test Lifecycle Hierarchy**:
   - Differentiate per-method setup (`@BeforeEach`) and teardown (`@AfterEach`) from per-class lifecycle hooks (`@BeforeAll`, `@AfterAll`).
   - Understand JUnit's default instance lifecycle (`PER_METHOD`) and its benefits for state isolation.
