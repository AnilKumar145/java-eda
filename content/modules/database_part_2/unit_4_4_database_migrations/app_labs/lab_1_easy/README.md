# Application Lab 1: Pharmacy Drug Formulary Schema Evolution Engine

## Scenario
You are tasked with building a lightweight, production-grade schema migration runner for the hospital pharmacy's drug formulary database. The engine must discover migration scripts, maintain a `flyway_schema_history` table in H2, compute and verify checksums to detect unauthorized script tampering, and execute versioned DDL/DML statements within atomic database transactions.

## Learning Objectives
- Design and maintain a `schema_history` migration metadata table using JDBC and H2.
- Parse version numbers from migration scripts and execute them in strict sequential order.
- Compute script checksums to ensure migration immutability and prevent schema drift.
- Execute migrations transactionally, rolling back upon DDL failure.

## Lab Structure
- `tasks.md`: Detailed specifications for Tasks 1, 2, and 3.
- `StarterCode.java`: Starter skeleton containing class stubs and TODO markers.
- `solution/Solution.java`: Complete reference implementation.
- `LabTests.java`: Assertion test suite validating your implementation on live H2 database instances.

## How to Test
```bash
# Test Reference Solution
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_2/unit_4_4_database_migrations/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_4_database_migrations/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests
```
