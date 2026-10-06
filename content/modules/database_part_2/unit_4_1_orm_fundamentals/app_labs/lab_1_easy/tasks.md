# Lab Tasks: Inpatient Bed Allocation & Ward Registry

## Task 1: Generate Relational DDL for Wards and Beds
Implement `WardBedOrmEngine.generateSchemaDdl(TableSchema wardSchema, TableSchema bedSchema)`:
- Construct the `CREATE TABLE IF NOT EXISTS` statement for the `wards` table containing `id BIGINT PRIMARY KEY`, `name VARCHAR(100) NOT NULL`, and `capacity INT NOT NULL`.
- Construct the `CREATE TABLE IF NOT EXISTS` statement for the `beds` table containing `id BIGINT PRIMARY KEY`, `bed_code VARCHAR(30) NOT NULL UNIQUE`, and foreign key column `ward_id BIGINT NOT NULL` referencing `wards(id)`.
- Return a list containing both DDL statements in dependency order (wards table first, then beds table).

## Task 2: Bidirectional Association Invariant Enforcement
Implement `Ward.assignBed(Bed bed)` and `Ward.releaseBed(Bed bed)`:
- `assignBed(bed)`: Check if ward is at capacity. If so, throw `IllegalStateException("Ward is at full capacity")`. If not, add bed to `beds` collection and set `bed.setWard(this)`.
- `releaseBed(bed)`: Remove bed from `beds` collection and clear `bed.setWard(null)`.

## Task 3: Relational Row Hydration Engine
Implement `WardBedOrmEngine.hydrateWardGraph(Map<String, Object> wardRow, List<Map<String, Object>> bedRows)`:
- Construct a `Ward` instance using `wardRow` columns (`id`, `name`, `capacity`).
- For each row in `bedRows`, construct a `Bed` instance (`id`, `bed_code`, `status`), assign it to the ward via `assignBed()`, and ensure bidirectional references are established.
- Return the fully hydrated `Ward` aggregate root.
