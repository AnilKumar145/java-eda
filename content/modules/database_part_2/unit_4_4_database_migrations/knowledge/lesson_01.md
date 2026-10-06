---
title: "Database Schema Evolution with Flyway and Liquibase"
module: "database_part_2"
unit: "unit_4_4_database_migrations"
order: 1
type: "knowledge"
difficulty: "intermediate"
tags:
  topics: ["database-migrations", "flyway", "liquibase", "schema-history", "checksums"]
  subtopics: ["versioning", "expand-contract", "repeatable-migrations", "ci-cd"]
use_case: "Safely evolving database schemas across development, staging, and production in Java enterprise applications."
domain: "Enterprise Java Database Architecture"
duration_hours: 1.5
---

# Lesson 4.4: Database Schema Evolution with Flyway and Liquibase

---

## 1. Why Database Migrations Matter & The Schema Drift Nightmare

In modern software development, application code is strictly versioned using Git. Every commit, feature branch, and pull request is tracked and reviewed.

However, a database is stateful. You cannot simply "recompile" or replace a production database because it contains millions of real patient records, transactions, and audit logs.

### The Horror of Manual Schema Changes:
Without an automated migration tool, teams fall into **Schema Drift**:
- Developer Alice adds column `phone_number` locally using a manual `ALTER TABLE` statement.
- Developer Bob creates a feature that relies on Alice's branch, but his local database lacks the column.
- The code is merged and deployed to staging. Staging crashes because the column is missing!
- A DBA manually runs a hotfix SQL snippet in production. Now production has a different schema than staging and local machines.

```
+--------------------+        +--------------------+        +--------------------+
|  Alice's Machine   |        |  Staging Database  |        |  Production Cluster|
+--------------------+        +--------------------+        +--------------------+
| patients (v3)      |  =/=   | patients (v1)      |  =/=   | patients (v2)      |
| has phone_number   |        | missing column     |        | manual DBA tweak   |
+--------------------+        +--------------------+        +--------------------+
                      Result: Crashes, Inconsistencies, Outages!
```

**Database Migrations** treat database schema changes as first-class, version-controlled code. Every change is an immutable script executed automatically in exact numerical order across every environment.

---

## 2. Migration Frameworks in Java: Flyway vs Liquibase

The Java ecosystem is home to two industry-standard migration tools: **Flyway** and **Liquibase**.

```
+------------------------------------+          +------------------------------------+
|            Flyway ORM              |          |             Liquibase              |
+------------------------------------+          +------------------------------------+
| * Pure ANSI SQL migration scripts  |          | * XML, YAML, JSON, or SQL format   |
| * Simple, transparent, zero magic  |          | * Database-agnostic changesets     |
| * Preferred by 80%+ Java engineers |          | * Powerful multi-database support  |
| * Filename-driven versioning       |          | * Complex rollback tags and preconditions |
+------------------------------------+          +------------------------------------+
```

### Why Flyway is Preferred:
Flyway uses **plain SQL**. There is no XML or proprietary YAML syntax to memorize. Developers write the exact DDL statements they understand (`CREATE TABLE`, `CREATE INDEX`, `ALTER TABLE`), and Flyway executes them reliably.

---

## 3. Flyway File Naming Conventions

Flyway discovers migration scripts on the classpath (typically under `src/main/resources/db/migration`). Flyway determines execution order and behavior purely from **file naming conventions**:

```
V1_0__create_patients_table.sql
^   ^ ^
|   | +--- Description (separated by two underscores '__')
|   +----- Version Number (numbers and underscores/dots)
+--------- Prefix: 'V' for Versioned, 'U' for Undo, 'R' for Repeatable
```

### The Three Migration Types:
1. **Versioned Migrations (`V`)**:
   - Format: `V<Version>__<Description>.sql` (e.g., `V1__init.sql`, `V2_1__add_allergies.sql`)
   - Executed **exactly once** in numerical order.
   - Once executed, the script is **immutable** and must never be edited.
2. **Repeatable Migrations (`R`)**:
   - Format: `R__<Description>.sql` (e.g., `R__vw_active_patients.sql`)
   - Executed whenever their content/checksum changes.
   - Ideal for database views, stored procedures, and triggers that can be safely dropped and recreated (`CREATE OR REPLACE VIEW`).
3. **Undo Migrations (`U`)**:
   - Format: `U<Version>__<Description>.sql` (available in Flyway Teams/Commercial).

---

## 4. The Migration Execution Lifecycle & `flyway_schema_history`

When your Java application boots up, Flyway checks the target database for a special metadata table called **`flyway_schema_history`**:

```sql
SELECT installed_rank, version, description, type, script, checksum, installed_on, execution_time, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

### The 5 Lifecycle Steps:
```
1. LOCATE     ---> Discovers all V*.sql scripts in classpath.
2. VALIDATE   ---> Checks if previously executed scripts still have identical checksums.
3. COMPARE    ---> Compares discovered versions against flyway_schema_history.
4. MIGRATE    ---> Executes any pending scripts inside database transactions.
5. RECORD     ---> Inserts new rows into flyway_schema_history with checksum & duration.
```

If the database is clean, Flyway creates the `flyway_schema_history` table automatically and runs all scripts from `V1` to the latest version.

---

## 5. Checksum Integrity & Tamper Detection

One of Flyway's most important safety mechanisms is **Checksum Validation**.

When Flyway executes a script (e.g., `V1__init.sql`), it calculates a 32-bit CRC or SHA-256 hash of the file contents and stores that checksum in the database row.

### What Happens if a Developer Alters an Old Migration?
Suppose Developer Charlie opens `V1__init.sql` and changes a column name from `mrn` to `patient_mrn`.
When the application starts up:
1. Flyway calculates the checksum of the modified `V1__init.sql` on disk.
2. Flyway reads the recorded checksum in `flyway_schema_history` from when V1 was originally executed.
3. The checksums **do not match**!
4. Flyway **immediately aborts startup** with a `FlywayValidateException`:
   ```
   Migration checksum mismatch for migration version 1
   -> Applied to database : -182938192
   -> Resolved locally    : 489201948
   ```

> **Golden Rule**: Never modify a migration script that has already been deployed to any shared environment or committed to main! Always create a new versioned script (`V2__rename_mrn.sql`) to apply changes.

---

## 6. Repeatable Migrations (Views, Functions, Procedures)

Some database objects do not store tabular state. For example:
- Analytical Views (`CREATE OR REPLACE VIEW patient_summary_vw AS ...`)
- Stored Procedures and Triggers

Managing views in versioned files (`V1`, `V2`, `V3`) leads to clutter because you end up with 10 scripts defining the same view over time.

Instead, use **Repeatable Migrations**:
- Save your view in `R__patient_summary_view.sql`.
- Whenever you need to add a column or modify the view's query, edit `R__patient_summary_view.sql` directly.
- On startup, Flyway detects that the checksum of `R__patient_summary_view.sql` changed, and re-executes the file!

---

## 7. Rollbacks vs Forward-Fix in Production

A common beginner misconception is that when a migration fails or a bug is found in production, the team should execute an automated "Down" or "Rollback" script.

In real-world enterprise databases with billions of rows, **automated rollbacks are notoriously dangerous**:
- Dropping a newly added column drops all data written to it by production users during the 20 minutes the new version was live.
- Down scripts are rarely tested with realistic production concurrency and volume.

### Enterprise Best Practice: Forward-Fix
Instead of rolling back:
1. Keep the schema intact.
2. Create a new forward migration (e.g., `V4__revert_column_change.sql`).
3. Deploy forward through the standard CI/CD pipeline.

---

## 8. Zero-Downtime Schema Evolution: The Expand-and-Contract Pattern

In continuous deployment (CD), web servers and database instances are updated with **zero downtime**. Old application versions (v1) and new application versions (v2) run concurrently during a rolling deploy!

If a migration deletes or renames a column immediately, running v1 instances will immediately crash with SQL exceptions!

### The 3-Phase Expand-and-Contract Solution:

```
Phase 1: EXPAND
  - Add new column 'medical_record_num' alongside old 'mrn'.
  - Deploy code that reads 'mrn' but writes to BOTH 'mrn' and 'medical_record_num'.

Phase 2: MIGRATE DATA
  - Run background migration copying historical data from 'mrn' to 'medical_record_num'.
  - Deploy v2 application reading and writing solely from 'medical_record_num'.

Phase 3: CONTRACT
  - Verify all v1 instances are decommissioned.
  - Run migration dropping old column 'mrn'.
```

By separating schema additions from schema removals across deployment cycles, you achieve 100% uptime with zero service interruptions.
