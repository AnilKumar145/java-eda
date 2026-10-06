# Lab Tasks: Patient Allergy Cross-Reference & Eager Loading Hub

## Task 1: Many-to-Many Association Invariants
Implement `ClinicalAllergyHub.documentAllergy(Patient patient, Allergen allergen, String severity)`:
- Record the allergy relationship between `patient` and `allergen`.
- Maintain bidirectional in-memory pointers: add `allergen` to `patient.getAllergies()` and add `patient` to `allergen.getPatients()`.
- Add an entry to the join table representation `List<JoinRow>` containing `(patientId, allergenId, severity)`.

## Task 2: N+1 Simulation & Detection Engine
Implement `ClinicalAllergyHub.simulateLazyLoadCount(int patientCount)` vs `simulateEagerLoadCount()`:
- `simulateLazyLoadCount(patientCount)`: Returns total database queries executed when 1 query loads all patients and 1 query is executed per patient to fetch their allergies: `1 + patientCount`.
- `simulateEagerLoadCount()`: Returns total database queries when an eager `JOIN FETCH` query is used: exactly `1`.

## Task 3: Hydrate Many-to-Many Graph from Single Joined Dataset
Implement `ClinicalAllergyHub.hydrateEagerGraph(List<JoinedRowData> rows)`:
- Take a list of flat join rows representing `SELECT p.id, p.name, a.id, a.code, j.severity FROM patients p LEFT JOIN patient_allergies j ON ... LEFT JOIN allergens a ON ...`.
- Reconstruct the distinct `Patient` and `Allergen` entity instances without duplicates.
- Re-establish all bidirectional links and return the collection of hydrated patients.
