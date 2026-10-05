---
title: "ICU Watchdog Telemetry Supervisor"
type: app_lab
module: threads
unit: unit_1_2_thread_management
lab_number: 1
difficulty: easy
use_case: icu_watchdog_supervisor
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - threads
    - management
  subtopics:
    - daemon-threads
    - cooperative-interruption
    - timed-join
    - watchdog-supervision
---

# Lab Level 1: ICU Watchdog Telemetry Supervisor (Java)
**Module**: Threads
**Objective**: Build an ICU telemetry supervisor in Java that manages background daemon watchdog threads and cooperative interruption for hung bedside telemetry streams.
**Difficulty**: Easy
**Context**: Intensive Care Digital Telemetry Center

## Generic Information
**Problem Statement**: In a hospital intensive care digital center, bedside sensor pollers occasionally freeze due to patient room Wi-Fi drops. A supervisor must monitor telemetry threads with strict deadlines using `join(timeoutMs)` and interrupt unresponsive threads gracefully without crashing the telemetry gateway. A background daemon watchdog continuously tracks heartbeats.
**Goals**:
- Implement an interruptible poller worker responding to `Thread.interrupt()`.
- Implement `ICUWatchdogSupervisor.supervisePoller(pollerThread, deadlineMs)`.
- Launch a background daemon heartbeat reporter.

## Use Case
**Title**: Supervise and Recover Hung ICU Monitors
**Description**: Execute a telemetry poller with a timeout deadline. If the poller finishes within the deadline, return `STATUS_OK`. If the poller hangs, interrupt it and return `STATUS_TIMED_OUT`.

### Rules
- Unresponsive threads must be interrupted using `thread.interrupt()`.
- Supervisor must never hang indefinitely.

### Test Cases
- Case 1: Healthy poller completing in 50ms with 200ms deadline returns `STATUS_OK`.
- Case 2: Stalled poller sleeping 2000ms with 100ms deadline is interrupted and returns `STATUS_TIMED_OUT`.

## Overview
You will implement `ICUWatchdogSupervisor` in `StarterCode.java`.

## How to Use This Lab
1. Read `README.md` and `tasks.md`.
2. Implement methods in `StarterCode.java`.
3. Compile and run `LabTests.java` to verify your implementation.
4. Review `solution/Solution.java` after completion.
