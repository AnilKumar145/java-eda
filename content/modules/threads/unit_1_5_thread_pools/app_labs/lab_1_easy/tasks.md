# Lab 1 Tasks: Multi-Service Clinical Diagnostics Aggregator (Java)

Follow these steps to complete `StarterCode.java`.

---

### Task 1: Define `ServiceQuery` and `ServiceResult` Records
- `ServiceQuery(String serviceName, Callable<String> queryTask)`
- `ServiceResult(String serviceName, String status, String data)`

---

### Task 2: Implement `DiagnosticAggregator.aggregateProfile`
1. Use a `ThreadPoolExecutor` (e.g. `Executors.newFixedThreadPool(maxWorkers)`).
2. Submit each `ServiceQuery` to the pool, storing `Map<String, Future<String>>`.
3. For each service, call `future.get(timeoutMs, TimeUnit.MILLISECONDS)`.
   - On success: Record `ServiceResult(name, "OK", data)`.
   - On `TimeoutException`: Call `future.cancel(true)` and record `ServiceResult(name, "TIMEOUT", null)`.
   - On `Exception`: Record `ServiceResult(name, "ERROR", e.getMessage())`.
4. Shut down the pool.
5. Return the list of `ServiceResult` objects.
