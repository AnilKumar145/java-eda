---
title: "Hospital ER Patient Triage Dispatcher"
type: app_lab
module: threads
unit: unit_1_4_thread_communication
lab_number: 1
difficulty: easy
use_case: hospital_er_triage_dispatcher
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - threads
    - communication
  subtopics:
    - wait-notify
    - producer-consumer
    - priority-dispatch
    - graceful-drain
---

# Lab Level 1: Hospital ER Patient Triage Dispatcher (Java)
**Module**: Threads
**Objective**: Build a multi-threaded emergency room triage dispatcher in Java using `wait()`, `notifyAll()`, and a priority queue to dispatch emergency patients to on-duty doctor threads.
**Difficulty**: Easy
**Context**: Hospital Emergency Department Triage Center

## Generic Information
**Problem Statement**: In a hospital emergency department, triage nurses admit trauma cases of varying acuity (Level 1: Cardiac arrest/trauma, down to Level 5: Minor sprain). A pool of on-duty emergency physicians wait for patients. When doctors are idle, they must sleep in a waiting state (`wait()`). When a patient is admitted, the dispatcher wakes doctor threads (`notifyAll()`). Doctors must treat the highest acuity patients first (Level 1 before Level 5).
**Goals**:
- Implement `ERTriageDispatcher` with a priority queue.
- Implement `admitPatient(TriageCase case)` using `synchronized` and `notifyAll()`.
- Implement `doctorWorker(doctorId)` using `wait()` until cases arrive or shutdown sentinel is encountered.
- Implement `drainAndShutdown()` to ensure all admitted patients are treated before doctor threads terminate.

## Use Case
**Title**: Priority Triage Case Dispatching
**Description**: Admitted cases are prioritized by acuity level. Multiple concurrent doctors treat cases without race conditions.

### Rules
- Level 1 cases must always be treated before Level 2+ cases.
- Doctor threads must wait without burning CPU when the triage queue is empty.

### Test Cases
- Case 1: Admit Level 4, Level 1, Level 2 patients. Verify doctor treats Level 1 first.
- Case 2: Multi-doctor pool: 3 doctors process 9 patients and shut down cleanly.

## Overview
You will implement `ERTriageDispatcher` in `StarterCode.java`.

## How to Use This Lab
1. Read `README.md` and `tasks.md`.
2. Implement methods in `StarterCode.java`.
3. Compile and run `LabTests.java` to verify your implementation.
4. Review `solution/Solution.java` after completion.
