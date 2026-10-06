# Application Lab 1: Inpatient Bed Allocation & Ward Registry

## Scenario
You are developing the core persistence foundation for a tertiary care hospital's Inpatient Department (IPD). The hospital requires an object-relational mapping layer that bridges object graphs (Wards and Beds) with relational tables in PostgreSQL/H2.

## Learning Objectives
- Map domain models to relational tables with primary and foreign key constraints.
- Generate valid ANSI SQL DDL statements for parent-child relationship structures.
- Implement defensive memory synchronization for bidirectional `@OneToMany` and `@ManyToOne` associations.
- Hydrate nested object graphs from relational database rows.

## Lab Structure
- `tasks.md`: Detailed specifications for Tasks 1, 2, and 3.
- `StarterCode.java`: Starter skeleton containing class stubs and TODO markers.
- `solution/Solution.java`: Complete reference implementation.
- `LabTests.java`: Assertion test suite validating your implementation.

## How to Test
```bash
# Test Reference Solution
javac -d .temp_bin content/modules/database_part_2/unit_4_1_orm_fundamentals/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_1_orm_fundamentals/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
