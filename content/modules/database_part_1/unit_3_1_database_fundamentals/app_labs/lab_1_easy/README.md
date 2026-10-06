# Lab 1 Easy: Hospital Patient Registry & Schema Architect

## Overview
In this lab, you will engineer a schema builder and referential integrity validator for a real-world healthcare patient management system.

## Domain Scenario
At St. Jude Metropolitan Hospital, patient safety demands strict relational integrity. When a patient is registered, their primary identifier is their Medical Record Number (MRN). Subsequent clinical encounters (emergency admissions, ICU transfers, surgeries) reference this patient via a foreign key. 

Your task is to build the relational schema representation and validation engine in Java to ensure no invalid foreign references can ever be staged for ingestion.

## Running Tests
```bash
# Compile and run with assertions
javac -d .temp_bin content/modules/database_part_1/unit_3_1_database_fundamentals/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_1/unit_3_1_database_fundamentals/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
