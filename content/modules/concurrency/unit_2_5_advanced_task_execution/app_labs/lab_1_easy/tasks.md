# Lab 1 Tasks: Healthcare Diagnostic Report Aggregator

## Task 1: Clinical Model Definitions
- Define `DiagnosticSummary` record:
  - `patientId` (String)
  - `bloodLab` (String)
  - `radiology` (String)
  - `allergies` (String)
  - `status` (String: "COMPLETE" or "DEGRADED")

## Task 2: Service Interfaces & Mock Implementations
- Provide functional suppliers or service methods:
  - `fetchBloodLabs(String patientId)`
  - `fetchRadiology(String patientId)`
  - `fetchAllergies(String patientId)`

## Task 3: Asynchronous Aggregation Pipeline
- In `DiagnosticReportAggregator`, implement:
  - `CompletableFuture<DiagnosticSummary> generateReportAsync(String patientId, ExecutorService pool)`
  - Use `CompletableFuture.supplyAsync()` to trigger each service.
  - Attach `.exceptionally()` to each stage with fallback strings if an exception occurs.
  - Combine the 3 futures into a `DiagnosticSummary`.
  - Mark status as `"DEGRADED"` if any service failed, otherwise `"COMPLETE"`.
