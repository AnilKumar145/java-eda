# Lab Tasks: Clinical Audit Log Quality Gate and Coverage Harness

## Task 1: Declare the Audit Record Model
In `Solution.java`:
- `record AuditRecord(String eventId, String actorId, String patientId, String action, String severity, String previousHash, String currentHash)`

## Task 2: Implement Cryptographic Ledger Methods
Implement `ClinicalAuditLedger`:
- `static String computeHash(String eventId, String actorId, String patientId, String action, String severity, String previousHash)`:
  - Generates SHA-256 hex digest of `{eventId}|{actorId}|{patientId}|{action}|{severity}|{previousHash}`.
- `appendEvent(eventId, actorId, patientId, action, severity) -> AuditRecord`:
  - Validates non-empty fields (else throws `IllegalArgumentException`).
  - Validates severity in `{"INFO", "WARNING", "CRITICAL"}` (else throws `IllegalArgumentException`).
  - Sets `previousHash` to `GENESIS_HASH` if first record, else previous record's `currentHash`.
  - Appends and returns `AuditRecord`.
- `verifyIntegrity() -> boolean`:
  - Verifies each record's `previousHash` points to predecessor's `currentHash`.
  - Recomputes SHA-256 hash and verifies matching `currentHash`.
  - Returns `true` if untampered, `false` otherwise.
- `findCriticalBreaches() -> List<AuditRecord>`:
  - Returns list of records with `severity.equals("CRITICAL")`.

## Task 3: Author 100% Branch Coverage Test Suite
In `LabTests.java`, test:
1. Empty ledger integrity is true.
2. Initial genesis event addition and subsequent chaining.
3. Field validation exceptions (empty string for any field).
4. Invalid severity rejection.
5. Tamper detection: modifying record action breaks integrity.
6. Tamper detection: forging previous hash breaks integrity.
7. Filtering critical security breaches.
