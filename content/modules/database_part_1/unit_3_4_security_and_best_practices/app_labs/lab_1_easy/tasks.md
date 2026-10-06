# Tasks: Prescription Audit Ledger & Transaction Guard

## Task 1: Initialize Database Tables
In `PrescriptionLedger.initSchema(Connection conn)`:
1. `pharmacy_inventory`:
   - `medication_code VARCHAR(32) PRIMARY KEY`
   - `quantity INT NOT NULL CHECK (quantity >= 0)`
2. `dispense_audit`:
   - `id BIGINT AUTO_INCREMENT PRIMARY KEY`
   - `patient_mrn VARCHAR(64) NOT NULL`
   - `medication_code VARCHAR(32) NOT NULL`
   - `dispensed_units INT NOT NULL`
3. `insurance_claims`:
   - `claim_id BIGINT AUTO_INCREMENT PRIMARY KEY`
   - `patient_mrn VARCHAR(64) NOT NULL`
   - `status VARCHAR(32) NOT NULL`

## Task 2: Implement Dispense Transaction with Savepoint Guard
In `PrescriptionLedger.dispenseMedication(Connection conn, String mrn, String medCode, int units, boolean simulateInsuranceFailure)`:
1. Turn off autocommit (`conn.setAutoCommit(false)`).
2. Decrement inventory: `UPDATE pharmacy_inventory SET quantity = quantity - ? WHERE medication_code = ? AND quantity >= ?`. If 0 rows updated, throw `IllegalStateException("Insufficient stock")`.
3. Insert into `dispense_audit`.
4. Create `Savepoint sp = conn.setSavepoint("InsuranceClaimStage")`.
5. Try inserting into `insurance_claims`. If `simulateInsuranceFailure` is true, catch/rollback to `sp`.
6. Commit transaction and restore autocommit. Return `boolean` indicating whether insurance claim was successful.
