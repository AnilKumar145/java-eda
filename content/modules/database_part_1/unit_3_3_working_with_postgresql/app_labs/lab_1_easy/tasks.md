# Tasks: Radiology Study Store & Structured Metrics

## Task 1: Initialize Database Schema
In `RadiologyStudyRepository.initSchema()`, execute DDL to create table `radiology_studies`:
- `study_id UUID PRIMARY KEY`
- `patient_mrn VARCHAR(64) NOT NULL`
- `modality VARCHAR(16) NOT NULL` (e.g. `'CT'`, `'MRI'`)
- `radiation_dose_msv NUMERIC(6, 2)` (nullable for non-ionizing modalities like MRI)
- `scanner_metadata_json TEXT NOT NULL`
- `created_at TIMESTAMP NOT NULL`

## Task 2: Insert Radiology Study with Typed Mappings
In `RadiologyStudyRepository.recordStudy(StudyRecord study)`, use a parameterized `PreparedStatement` to insert the study:
- Bind `UUID` via `pstmt.setObject(1, study.studyId())`
- Bind nullable `radiationDoseMsv` (if null, call `pstmt.setNull(4, Types.NUMERIC)`)
- Return the inserted record's UUID.

## Task 3: Calculate Cumulative Radiation Exposure
In `RadiologyStudyRepository.getCumulativeDose(String patientMrn)`, execute a query using ANSI SQL `COALESCE(SUM(radiation_dose_msv), 0.00)` to calculate the total radiation dose for the patient, returning a `BigDecimal` with 2 decimal places.
