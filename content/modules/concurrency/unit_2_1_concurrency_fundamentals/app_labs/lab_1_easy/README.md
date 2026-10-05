---
title: "Immutable Patient Medical Profile Cache"
type: app_lab
module: concurrency
unit: unit_2_1_concurrency_fundamentals
lab_number: 1
difficulty: easy
use_case: immutable_patient_cache
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - concurrency
    - immutability
  subtopics:
    - java-records
    - defensive-copying
    - thread-safe-cache
    - safe-publication
---

# Lab Level 1: Immutable Patient Medical Profile Cache (Java)
**Module**: Concurrency
**Objective**: Build a high-performance in-memory patient clinical profile cache in Java using immutable `record` objects and defensive copying to guarantee 100% thread safety without read locks.
**Difficulty**: Easy
**Context**: Hospital Clinical Information System

## Generic Information
**Problem Statement**: In a hospital electronic health records (EHR) platform, thousands of nurse workstations and doctor tablets query patient medication allergy lists concurrently. Using mutable objects protected by locks creates read bottlenecks and risks external modification of allergy lists. Using immutable `record` structures with defensive collection copies guarantees that any number of threads can read patient profiles simultaneously at full RAM speed with zero locks.
**Goals**:
- Define an immutable `PatientProfile` record with defensive list copying.
- Implement `PatientProfileCache` with thread-safe `put(profile)` and `get(patientId)`.
- Verify that modifying an external list after insertion does not alter the cached record.

## Use Case
**Title**: Zero-Lock Immutable Clinical Profile Reading
**Description**: Multiple threads query patient records concurrently while updates occur via atomic reference replacement.

### Rules
- All profile objects must be immutable records.
- Collections in records must be unmodifiable copies (`List.copyOf()`).

### Test Cases
- Case 1: Insert patient record with allergies. Mutate the original list outside the cache; verify cached profile is unchanged.
- Case 2: Concurrent multi-threaded reads return consistent profile records.

## Overview
You will implement `PatientProfileCache` in `StarterCode.java`.

## How to Use This Lab
1. Read `README.md` and `tasks.md`.
2. Implement methods in `StarterCode.java`.
3. Compile and run `LabTests.java` to verify your implementation.
4. Review `solution/Solution.java` after completion.
