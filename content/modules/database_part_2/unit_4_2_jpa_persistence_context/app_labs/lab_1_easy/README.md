# Application Lab 1: Surgical Schedule & Operating Theatre Dispatcher

## Scenario
You are developing an operating room dispatching system for a high-volume surgical center. In this lab, you will build a Persistence Context manager that handles entity state lifecycles, dirty checking on procedure scheduling changes, first-level caching, and JPQL query parameterization.

## Learning Objectives
- Implement an in-memory Persistence Context that manages entity lifecycle states.
- Cache loaded entities in an Identity Map to prevent redundant lookups.
- Implement automatic dirty checking that detects mutated attributes and emits SQL `UPDATE` statements.
- Bind JPQL queries with named parameters and filters.

## Lab Structure
- `tasks.md`: Detailed specifications for Tasks 1, 2, and 3.
- `StarterCode.java`: Starter skeleton containing class stubs and TODO markers.
- `solution/Solution.java`: Complete reference implementation.
- `LabTests.java`: Assertion test suite validating your implementation.

## How to Test
```bash
# Test Reference Solution
javac -d .temp_bin content/modules/database_part_2/unit_4_2_jpa_persistence_context/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_2_jpa_persistence_context/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
