---
title: "ICU Bed Reservation Gateway"
type: app_lab
module: concurrency
unit: unit_2_2_java_concurrency_utilities
lab_number: 1
difficulty: easy
use_case: icu_bed_reservation_gateway
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - concurrency
    - utilities
  subtopics:
    - semaphore
    - reentrant-lock
    - resource-throttling
    - reservation-gateway
---

# Lab Level 1: ICU Bed Reservation Gateway (Java)
**Module**: Concurrency
**Objective**: Build a high-reliability ICU bed reservation gateway in Java using `Semaphore` and `ReentrantLock` to coordinate finite bed availability across emergency departments without overselling.
**Difficulty**: Easy
**Context**: Regional Hospital Emergency Bed Allocation Network

## Generic Information
**Problem Statement**: During regional health crises, 10 regional emergency wards compete for a limited number of specialized ICU negative-pressure isolation beds (e.g. 5 total beds). If multiple triage doctors request beds concurrently, the allocation system must enforce strict permit accounting using a `Semaphore`. Once admitted, patient-bed assignments are recorded in a thread-safe registry protected by a `ReentrantLock`.
**Goals**:
- Implement `ICUBedReservationGateway(totalBeds)`.
- Use a `Semaphore` to manage finite physical bed permits.
- Implement `reserveBed(patientId, timeoutMs)` returning whether reservation succeeded.
- Implement `releaseBed(patientId)` releasing the permit back to the hospital network.

## Use Case
**Title**: Atomic Bounded Bed Allocation
**Description**: Concurrent ER requests acquire permits from a finite bed pool. If all beds are occupied, further requests wait up to `timeoutMs` before failing gracefully.

### Rules
- Never allocate more than `totalBeds` simultaneous reservations.
- Released beds must be immediately available for waiting patients.

### Test Cases
- Case 1: 5 beds available. 5 requests succeed immediately.
- Case 2: 5 beds available. 6th request times out within 50ms while beds are held.
- Case 3: Releasing a bed allows the next waiting request to acquire it.

## Overview
You will implement `ICUBedReservationGateway` in `StarterCode.java`.

## How to Use This Lab
1. Read `README.md` and `tasks.md`.
2. Implement methods in `StarterCode.java`.
3. Compile and run `LabTests.java` to verify your implementation.
4. Review `solution/Solution.java` after completion.
