# Tasks: Hospital Patient Registry & Schema Architect

## Task 1: Define Patient Schema Specifications
Implement `SchemaDefinition` record to store:
- `tableName`: Name of the database table (e.g., `"patients"`).
- `primaryKey`: Name of the primary key column (e.g., `"patient_id"`).
- `columns`: Map of column name to SQL datatype (e.g., `"mrn" -> "VARCHAR(64) NOT NULL UNIQUE"`).
- `foreignKeys`: Map of child column to `"parent_table(parent_column)"`.

## Task 2: Implement DDL SQL Generator
In `HospitalRegistryArchitect.generateDdl(SchemaDefinition schema)`, generate the complete ANSI SQL string:
- `CREATE TABLE IF NOT EXISTS <table_name> (`
- Column definitions comma-separated
- Primary key constraint: `CONSTRAINT pk_<table_name> PRIMARY KEY (<primary_key>)`
- Foreign key constraints: `CONSTRAINT fk_<child_col> FOREIGN KEY (<child_col>) REFERENCES <target>`
- Trailing `);`

## Task 3: Implement In-Memory Referential Integrity Guard
In `HospitalRegistryArchitect.validateEncounterBatch(Set<Long> registeredPatientIds, List<EncounterRecord> encounters)`, verify that every encounter's `patientId` exists in `registeredPatientIds`. Return a list of all invalid encounter records that violate referential integrity.
