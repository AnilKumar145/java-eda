---
title: "Clinical Metric Benchmark Harness"
type: app_lab
module: concurrency
unit: unit_2_7_choosing_the_right_concurrency_model
lab_number: 1
difficulty: easy
use_case: clinical_metric_benchmark_harness
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - concurrency
    - architecture
  subtopics:
    - benchmark
    - contention-comparison
    - synchronized-vs-atomic
    - metrics-harness
---

# Lab Level 1: Clinical Metric Benchmark Harness (Java)
**Module**: Concurrency
**Objective**: Build a multi-threaded benchmark harness comparing throughput and accuracy across 3 concurrency architectures: synchronized block, ReentrantLock, and AtomicLong under high-volume patient telemetry loads.
**Difficulty**: Easy
**Context**: Hospital Clinical Data Architecture Benchmarking Lab

## Generic Information
**Problem Statement**: When designing high-frequency patient telemetry ingestion services for a multi-hospital health network, systems architects must decide between mutual exclusion locks and lock-free atomic counters. To justify architectural recommendations with empirical data, you will implement a benchmark harness that subjects different telemetry counter implementations to concurrent updates and compares their elapsed times and correctness.

**Goals**:
- Implement `TelemetryCounter` interface with 3 implementations:
  1. `SynchronizedCounter`
  2. `ReentrantLockCounter`
  3. `AtomicCounter`
- Run a benchmark executor across $N$ threads and verify all 3 counters achieve exact target totals.

## Use Case
**Title**: Multi-Threaded Telemetry Counter Benchmarking
**Description**: Execute concurrent telemetry increments on Synchronized, Lock, and Atomic strategies, validating data integrity and performance profiling.

### Rules
- All implementations must correctly implement `TelemetryCounter`.
- Counters must not suffer data races or lost updates.

### Test Cases
- Case 1: Run 10 threads $\times$ 1,000 increments each. Verify all 3 implementations reach exactly 10,000.
- Case 2: Verify non-negative elapsed durations for benchmark execution.
