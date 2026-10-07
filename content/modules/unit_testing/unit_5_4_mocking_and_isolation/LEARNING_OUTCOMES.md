# Learning Outcomes: Unit 5.4 - Mocking and Isolation

By completing this unit, students will be able to:

1. **Categorize Gerard Meszaros's Test Doubles**:
   - Differentiate Dummies (placeholders), Stubs (hardcoded indirect inputs), Mocks (expected interaction verifiers), Spies (wrappers recording calls), and Fakes (working in-memory simplifications).

2. **Master Mockito Configuration & Stubbing**:
   - Create mock objects using `Mockito.mock(Class.class)` and `@Mock` / `@InjectMocks` annotations.
   - Program indirect inputs with `when(mock.method()).thenReturn(value)` and simulate failures with `when(mock.method()).thenThrow(exception)`.

3. **Verify Method Invocations and Argument Passing**:
   - Use `verify(mock).method(arg)` to assert method executions.
   - Enforce call frequency with `times(n)`, `never()`, and `atLeastOnce()`.
   - Use `verifyNoInteractions(mock)` to ensure secondary or fallback systems are never contacted during primary success paths.

4. **Isolate Code from External Clinical Network Gateways**:
   - Test critical emergency alerting logic (SMS gateways, hospital paging antennas, remote REST EHR APIs) without emitting real network requests or sending live SMS texts.
