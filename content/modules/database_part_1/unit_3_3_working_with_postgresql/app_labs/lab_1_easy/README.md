# Lab 1 Easy: Radiology Study Store & Structured Metrics

## Overview
In this lab, you will develop a clinical Radiology Study Repository in Java. The repository models CT and MRI scans with native UUID primary keys, high-precision BigDecimal radiation dosimetry, and semi-structured metadata.

## Domain Scenario
Metropolitan Imaging Center operates multi-slice CT scanners. To maintain regulatory compliance with radiation safety boards, patient cumulative radiation doses must be recorded using exact decimal arithmetic (`NUMERIC` / `BigDecimal`). In addition, scans require globally unique UUIDs to integrate with PACS (Picture Archiving and Communication System) archives.

## Running Tests
```bash
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_3_working_with_postgresql/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_1/unit_3_3_working_with_postgresql/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests
```
