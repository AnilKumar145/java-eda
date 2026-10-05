---
title: "Patient Vital Metric Tracker"
type: app_lab
module: concurrency
unit: unit_2_4_atomic_operations_and_memory
lab_number: 1
difficulty: easy
use_case: patient_vital_metric_tracker
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - concurrency
    - memory-model
  subtopics:
    - atomic-integer
    - atomic-reference
    - compare-and-swap
    - telemetry-tracker
---

# Lab Level 1: Patient Vital Metric Tracker (Java)
**Module**: Concurrency
**Objective**: Build a real-time lock-free vital signs telemetry monitor using `AtomicInteger` for pulse rates and `AtomicReference` for patient condition state transitions.
**Difficulty**: Easy
**Context**: Intensive Care Unit (ICU) Patient Monitoring Telemetry System

## Generic Information
**Problem Statement**: ICU bedside monitors record arterial pulse rate and blood oxygenation 100 times per minute. Multiple medical staff may simultaneously acknowledge alarms or update the patient's triage status. Using synchronized blocks on every sensor tick causes unacceptable latency. We need lock-free atomic tracking for vital metrics and state transitions.

**Goals**:
- Use `AtomicInteger` to record heart rate samples and track max observed peak.
- Use `AtomicReference` with CAS to manage patient triage status transitions (`STABLE`, `ELEVATED`, `CRITICAL`, `DISCHARGED`).
- Use `volatile boolean` for active telemetry sensor streaming control.

## Use Case
**Title**: Lock-Free Patient Vital Signs Stream
**Description**: Continuous sensor stream updates patient pulse without locking, and alarm threshold status changes are managed atomically.

### Rules
- Heart rate updates must use `AtomicInteger`.
- Peak tracking must use CAS (`accumulateAndGet` or CAS loop).
- Status changes must use `compareAndSet()`.

### Test Cases
- Case 1: 10 concurrent sensor threads send 100 heart rate readings each. Peak tracking accurately records maximum value.
- Case 2: Concurrent status transition attempt allows only one winning transition.
