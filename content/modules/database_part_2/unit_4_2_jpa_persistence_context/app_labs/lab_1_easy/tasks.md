# Lab Tasks: Surgical Schedule & Operating Theatre Dispatcher

## Task 1: First-Level Cache & State Tracking
Implement `SurgicalPersistenceContext.persist(SurgicalProcedure proc)` and `find(Long id)`:
- `persist(proc)`: Transition `proc` from `TRANSIENT` to `MANAGED`. Record a snapshot of its fields (`roomCode`, `leadSurgeon`, `status`) and store in the internal identity map.
- `find(id)`: Return the cached managed entity if present in the identity map. If not present, query the underlying data store, transition the entity to `MANAGED`, record a snapshot, and store in the identity map.

## Task 2: Automatic Dirty Checking & Flush
Implement `SurgicalPersistenceContext.flush()`:
- Iterate over all `MANAGED` entities.
- Compare each entity's current state with its initial snapshot.
- For each dirty entity, construct a SQL `UPDATE` statement updating the modified columns:
  `UPDATE surgical_procedures SET ... WHERE id = ...;`
- Update the snapshot to match the newly flushed state.
- Return the list of generated SQL `UPDATE` statements.

## Task 3: JPQL Query Filtering & Translation
Implement `SurgicalPersistenceContext.queryProceduresByStatus(String status)`:
- Execute a simulated JPQL query: `SELECT p FROM SurgicalProcedure p WHERE p.status = :status ORDER BY p.id ASC`.
- Filter all managed entities matching the specified `status` string and return them sorted by `id`.
