# Lab Tasks: Pharmacy Drug Formulary Schema Evolution Engine

## Task 1: Initialize Schema History Table
Implement `FormularyMigrationEngine.initHistoryTable(Connection conn)`:
- Create the metadata table `pharmacy_schema_history` if it does not already exist:
  ```sql
  CREATE TABLE IF NOT EXISTS pharmacy_schema_history (
      installed_rank INT AUTO_INCREMENT PRIMARY KEY,
      version VARCHAR(50) NOT NULL UNIQUE,
      description VARCHAR(200) NOT NULL,
      script_name VARCHAR(150) NOT NULL,
      checksum BIGINT NOT NULL,
      installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      success BOOLEAN NOT NULL
  );
  ```

## Task 2: Validate Checksums Against Existing History
Implement `FormularyMigrationEngine.validateHistoricalScripts(Connection conn, List<MigrationScript> scripts)`:
- Query `pharmacy_schema_history` for all already-applied versions and their recorded checksums.
- For each previously applied version, compare its recorded checksum against the computed checksum of the supplied `MigrationScript`.
- If any applied version has an altered checksum, throw `IllegalStateException("Migration checksum mismatch for version " + version)`.

## Task 3: Execute Pending Migrations Transactionally
Implement `FormularyMigrationEngine.migrate(Connection conn, List<MigrationScript> scripts)`:
- Sort scripts in ascending version order.
- Validate historical checksums.
- Identify pending scripts that have not yet been applied.
- For each pending script:
  - Execute its SQL statement on the connection.
  - Insert a record into `pharmacy_schema_history` with its rank, version, description, script name, checksum, and success=true.
- Return the list of newly applied versions.
