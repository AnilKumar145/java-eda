---
title: "Hospital Pharmacy Inventory Dispenser"
type: app_lab
module: threads
unit: unit_1_3_thread_synchronization
lab_number: 1
difficulty: easy
use_case: hospital_pharmacy_inventory_dispenser
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - threads
    - synchronization
  subtopics:
    - synchronized-blocks
    - critical-sections
    - atomic-inventory
    - thread-safety
---

# Lab Level 1: Hospital Pharmacy Inventory Dispenser (Java)
**Module**: Threads
**Objective**: Build a thread-safe hospital pharmacy medication dispensing engine in Java that prevents race conditions and inventory deficits during simultaneous nurse requests.
**Difficulty**: Easy
**Context**: Inpatient Hospital Pharmacy Automated Dispensing Unit

## Generic Information
**Problem Statement**: In a 500-bed hospital, multiple automated medication dispensing cabinets across emergency, surgical, and intensive care wards request drug doses concurrently. If two nurses request the last dose of a critical narcotic (e.g. Fentanyl) at the exact same millisecond, an unsynchronized inventory check will approve both, resulting in a physical medication shortage. The system must synchronize inventory deductions cleanly.
**Goals**:
- Implement a thread-safe `PharmacyInventoryDispenser`.
- Maintain stock per medication ID.
- Provide synchronized `dispense(medicationId, units)` returning whether the dispensation succeeded.
- Provide `restock(medicationId, units)` and `getStock(medicationId)`.

## Use Case
**Title**: Atomic Medication Dispensation
**Description**: Concurrent automated ward requests attempt to dispense medications from the central pharmacy. Exactly the available units must be dispensed, with zero inventory deficits.

### Rules
- All inventory mutations must be protected by a synchronized block using a private lock.
- Inventory must never drop below 0.

### Test Cases
- Case 1: Stock is 100. 10 threads each attempt to dispense 10 units concurrently. Exactly 100 units dispensed, final stock is 0.
- Case 2: Stock is 5. 10 threads each attempt to dispense 1 unit. Exactly 5 succeed, 5 are rejected, final stock is 0.

## Overview
You will implement `PharmacyInventoryDispenser` in `StarterCode.java`.

## How to Use This Lab
1. Read `README.md` and `tasks.md`.
2. Implement methods in `StarterCode.java`.
3. Compile and run `LabTests.java` to verify your implementation.
4. Review `solution/Solution.java` after completion.
