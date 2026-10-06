# Tasks: Clinic Telemetry Pipeline & Pooled Engine

## Task 1: Initialize Database Schema
In `ClinicTelemetryRepository.initSchema()`, execute DDL to create table `patient_vitals`:
- `id BIGINT AUTO_INCREMENT PRIMARY KEY`
- `mrn VARCHAR(64) NOT NULL`
- `heart_rate INT NOT NULL`
- `spo2 DOUBLE NOT NULL`
- `systolic INT NOT NULL`
- `diastolic INT NOT NULL`
- `recorded_epoch_ms BIGINT NOT NULL`

## Task 2: Implement Batch Telemetry Ingestion
In `ClinicTelemetryRepository.ingestTelemetryBatch(List<TelemetryRecord> records)`, use a parameterized `PreparedStatement` with `addBatch()` and `executeBatch()` to insert all records inside a single transaction.

## Task 3: Query Patient Vitals by Time Window
In `ClinicTelemetryRepository.queryVitalsByTimeRange(String mrn, long startMs, long endMs)`, return all `TelemetryRecord` items matching the MRN between `startMs` and `endMs` inclusive, sorted chronologically ascending.
