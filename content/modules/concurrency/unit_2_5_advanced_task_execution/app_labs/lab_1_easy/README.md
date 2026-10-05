---
title: "Healthcare Diagnostic Report Aggregator"
type: app_lab
module: concurrency
unit: unit_2_5_advanced_task_execution
lab_number: 1
difficulty: easy
use_case: diagnostic_report_aggregator
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - concurrency
    - asynchronous
  subtopics:
    - completable-future
    - parallel-fanout
    - error-fallback
    - report-aggregator
---

# Lab Level 1: Healthcare Diagnostic Report Aggregator (Java)
**Module**: Concurrency
**Objective**: Build a non-blocking diagnostic report aggregator using `CompletableFuture` to fetch blood lab results, radiology scans, and allergy profiles in parallel.
**Difficulty**: Easy
**Context**: Hospital Electronic Health Record (EHR) Clinical Decision Support System

## Generic Information
**Problem Statement**: When an emergency physician assesses a patient, the EHR portal needs to display blood panel metrics, radiology scans, and medication allergies simultaneously. If each query takes 100-200ms sequentially, total latency delays care. The aggregator must query all three services concurrently using `CompletableFuture` and combine the results into a composite `DiagnosticSummary`. If radiology or lab services are down, default degraded fallbacks must prevent the entire report from failing.

**Goals**:
- Implement `CompletableFuture` fan-out for 3 simulated healthcare data sources.
- Use `CompletableFuture.allOf()` or `thenCombine` to merge results.
- Implement `.exceptionally()` fallback handling for degraded services.

## Use Case
**Title**: Composite Clinical Report Generation
**Description**: Asynchronously fetch lab tests, radiology status, and allergy warnings, aggregating them into a unified patient summary with zero blocking threads.

### Rules
- All service fetches must run asynchronously via `CompletableFuture`.
- Fallbacks must handle service failures gracefully.

### Test Cases
- Case 1: All 3 services succeed. Verify combined report contains all three data sections.
- Case 2: One service throws an exception. Verify report successfully generates with fallback status.
