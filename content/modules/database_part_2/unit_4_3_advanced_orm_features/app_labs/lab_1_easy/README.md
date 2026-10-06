# Application Lab 1: Patient Allergy Cross-Reference & Eager Loading Hub

## Scenario
In an Electronic Health Record (EHR) system, patient safety requires immediate, zero-latency access to severe drug allergy cross-references. In this lab, you will build the `ClinicalAllergyHub` managing Many-to-Many associations between Patients and Allergens, cascade operations, N+1 query detection, and optimized batch data retrieval using single JOIN queries.

## Learning Objectives
- Model Many-to-Many associations using join tables and bidirectional relationship management.
- Detect and prevent N+1 query patterns that degrade clinical application latency.
- Implement optimized batch fetch hydration that reconstructs entire Many-to-Many object graphs from a single joined relational result.

## Lab Structure
- `tasks.md`: Detailed specifications for Tasks 1, 2, and 3.
- `StarterCode.java`: Starter skeleton containing class stubs and TODO markers.
- `solution/Solution.java`: Complete reference implementation.
- `LabTests.java`: Assertion test suite validating your implementation.

## How to Test
```bash
# Test Reference Solution
javac -d .temp_bin content/modules/database_part_2/unit_4_3_advanced_orm_features/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_3_advanced_orm_features/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
