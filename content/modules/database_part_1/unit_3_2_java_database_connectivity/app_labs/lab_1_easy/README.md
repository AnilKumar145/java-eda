# Lab 1 Easy: Clinic Telemetry Pipeline & Pooled Engine

## Overview
In this lab, you will build an enterprise JDBC telemetry pipeline for an Intensive Care Unit (ICU) monitoring network. The pipeline ingests bedside vitals streams, performs batch database insertions, and queries time-series vitals using parameterized statements.

## Domain Scenario
ICU bedside monitors continuously stream high-frequency vital statistics (heart rate, SpO2, blood pressure). To prevent connection thrashing and SQL injection, telemetry packets must be ingested using precompiled `PreparedStatement` instances.

## Running Tests
```bash
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_2_java_database_connectivity/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_1/unit_3_2_java_database_connectivity/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests
```
