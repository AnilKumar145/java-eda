---
title: "Hospital Incident Audit Stream"
type: app_lab
module: concurrency
unit: unit_2_3_concurrent_collections
lab_number: 1
difficulty: easy
use_case: hospital_incident_audit_stream
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - concurrency
    - collections
  subtopics:
    - concurrent-hash-map
    - blocking-queue
    - copy-on-write
    - audit-stream
---

# Lab Level 1: Hospital Incident Audit Stream (Java)
**Module**: Concurrency
**Objective**: Build a high-throughput incident audit pipeline in Java using `ConcurrentHashMap` for ward metrics aggregation and `ArrayBlockingQueue` for incident ingestion.
**Difficulty**: Easy
**Context**: Hospital Clinical Safety & Incident Reporting Unit

## Generic Information
**Problem Statement**: When clinical incidents occur (e.g. medication discrepancy, patient fall, device alarm timeout), multiple ward systems submit audit events simultaneously. An incident aggregator must ingest events via a bounded queue and atomically track incident counts per ward in a `ConcurrentHashMap` without blocking reporters.
**Goals**:
- Implement `IncidentAuditStream` with a bounded queue.
- Use `ConcurrentHashMap.merge()` to maintain atomic ward counts.
- Broadcast completed audit summaries to an observer list.

## Use Case
**Title**: Ingest & Aggregate Ward Safety Incidents
**Description**: Concurrent clinical ward systems submit events. Counts are aggregated atomically, and observer listeners are notified without concurrent modification errors.

### Rules
- Ingestion must use `BlockingQueue`.
- Aggregation must use `ConcurrentHashMap`.

### Test Cases
- Case 1: Ingest 100 incidents across 3 wards concurrently. Verify exact totals.
- Case 2: Verify safe iteration when notifying observers.

## Overview
You will implement `IncidentAuditStream` in `StarterCode.java`.

## How to Use This Lab
1. Read `README.md` and `tasks.md`.
2. Implement methods in `StarterCode.java`.
3. Compile and run `LabTests.java` to verify your implementation.
4. Review `solution/Solution.java` after completion.
