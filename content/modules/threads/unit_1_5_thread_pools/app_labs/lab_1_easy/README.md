---
title: "Multi-Service Clinical Diagnostics Aggregator"
type: app_lab
module: threads
unit: unit_1_5_thread_pools
lab_number: 1
difficulty: easy
use_case: clinical_diagnostics_aggregator
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - threads
    - pools
  subtopics:
    - executor-service
    - callable-future
    - fan-out-fan-in
    - timeout-enforcement
---

# Lab Level 1: Multi-Service Clinical Diagnostics Aggregator (Java)
**Module**: Threads
**Objective**: Build a high-throughput diagnostic aggregator in Java using `ExecutorService`, `Callable<V>`, and `Future<V>` to fetch EHR records, laboratory tests, and bedside telemetry concurrently.
**Difficulty**: Easy
**Context**: Hospital Clinical Decision Support System

## Generic Information
**Problem Statement**: When an emergency physician assesses a patient, clinical observations reside across independent microservices: EHR, Pathology Lab, and Bedside Telemetry. Sequential retrieval delays care decisions. The aggregator must fan-out queries using an `ExecutorService`, enforce per-service timeouts, and consolidate healthy results into a single composite report.
**Goals**:
- Implement `DiagnosticAggregator.aggregatePatientProfile(patientId, services, timeoutMs)`.
- Use a `FixedThreadPool` to submit `Callable` queries in parallel.
- Handle timeouts gracefully, recording `"TIMEOUT"` for slow services while preserving healthy responses.

## Use Case
**Title**: Aggregate Patient Diagnostics via Thread Pool
**Description**: Query multiple clinical services concurrently with a fixed thread pool and assemble an aggregated report.

### Rules
- Dispatches must run concurrently using an `ExecutorService`.
- If an individual service exceeds `timeoutMs`, record `"TIMEOUT"` without failing the whole report.
- Cleanly shut down thread pools.

### Test Cases
- Case 1: 3 responsive services complete within 120ms total time.
- Case 2: 1 stalled service times out while 2 healthy services succeed.

## Overview
You will implement `DiagnosticAggregator` in `StarterCode.java`.

## How to Use This Lab
1. Read `README.md` and `tasks.md`.
2. Implement methods in `StarterCode.java`.
3. Compile and run `LabTests.java` to verify your implementation.
4. Review `solution/Solution.java` after completion.
