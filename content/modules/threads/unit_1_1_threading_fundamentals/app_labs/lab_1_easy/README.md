---
title: "Bedside Vitals Telemetry Poller"
type: app_lab
module: threads
unit: unit_1_1_threading_fundamentals
lab_number: 1
difficulty: easy
use_case: bedside_vitals_telemetry_poller
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - threads
    - concurrency
  subtopics:
    - thread-instantiation
    - runnable-lambdas
    - start-and-join
    - concurrent-aggregation
---

# Lab Level 1: Bedside Vitals Telemetry Poller (Java)
**Module**: Threads
**Objective**: Build a multi-threaded telemetry ingestion engine in Java using `Runnable` lambdas, `Thread.start()`, and `Thread.join()` to poll hospital bedside monitors concurrently.
**Difficulty**: Easy
**Context**: Hospital Intensive Care Unit Monitoring System

## Generic Information
**Problem Statement**: In a hospital ICU, bedside monitors record heart rate, pulse oximetry, and blood pressure. A sequential poller takes too long to poll each device, causing vital alarm delays. A multi-threaded ingestion engine must poll all configured monitors concurrently and consolidate their readings into a shared record without race conditions.
**Goals**:
- Implement a worker method `pollSingleMonitor(deviceId, latencyMs, value)` simulating network read latency.
- Implement `TelemetryIngestionEngine.pollMonitors(configs)` launching one worker thread per monitor.
- Join all threads cleanly to ensure all telemetry data is captured before returning.

## Use Case
**Title**: Concurrent Bedside Device Polling
**Description**: Query all configured monitors concurrently. Total elapsed batch time must not exceed the latency of the slowest individual device plus minimal thread scheduling overhead.

### Rules
- Every monitor query must execute on a dedicated `Thread`.
- Polling logic must handle `InterruptedException` cleanly.
- Results must be collected into a thread-safe `ConcurrentHashMap`.

### Test Cases
- Case 1: Poll 3 monitors with latencies of 50ms, 80ms, and 60ms. Total execution time must be under 120ms (concurrent execution).
- Case 2: Verify all 3 devices exist in the resulting map with their exact polled values.

## Overview
You will implement `TelemetryIngestionEngine` in `StarterCode.java`.

## How to Use This Lab
1. Read `README.md` and `tasks.md`.
2. Implement methods in `StarterCode.java`.
3. Compile and run `LabTests.java` to verify your implementation.
4. Review `solution/Solution.java` after completion.
