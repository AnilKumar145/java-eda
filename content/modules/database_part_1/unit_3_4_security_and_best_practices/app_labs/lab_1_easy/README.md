# Lab 1 Easy: Prescription Audit Ledger & Transaction Guard

## Overview
In this lab, you will engineer a tamper-evident Prescription Audit Ledger using HikariCP connection pooling, parameterized query validation, and multi-stage transactional savepoints.

## Domain Scenario
When a hospital pharmacy dispenses a medication, patient safety demands immediate inventory deduction and an immutable audit log entry. However, auxiliary operations (such as notifying an insurance clearinghouse) may encounter network transient faults. You will use JDBC `Savepoint` semantics to roll back the auxiliary insurance claim while successfully committing the core prescription dispensation.

## Running Tests
```bash
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_4_security_and_best_practices/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_1/unit_3_4_security_and_best_practices/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests
```
