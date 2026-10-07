import java.util.*;

public class LabTests {

    public static void main(String[] args) {
        testEmptyLedgerIsIntact();
        testGenesisHashAndChaining();
        testFieldValidationExceptions();
        testInvalidSeverityException();
        testTamperDetectionAlteredAction();
        testTamperDetectionForgedPreviousHash();
        testFindCriticalBreaches();
        System.out.println("All Unit 5.7 Lab 1 Tests Passed Successfully!");
    }

    public static void testEmptyLedgerIsIntact() {
        Solution.ClinicalAuditLedger ledger = new Solution.ClinicalAuditLedger();
        assert ledger.verifyIntegrity() : "Empty ledger must be valid";
        assert ledger.findCriticalBreaches().isEmpty() : "No breaches in empty ledger";
        System.out.println("Lab 1 Test 1 Passed: Empty ledger integrity verified.");
    }

    public static void testGenesisHashAndChaining() {
        Solution.ClinicalAuditLedger ledger = new Solution.ClinicalAuditLedger();
        Solution.AuditRecord r1 = ledger.appendEvent("EVT-1", "dr_house", "PAT-10", "READ_EHR", "INFO");

        assert Solution.ClinicalAuditLedger.GENESIS_HASH.equals(r1.previousHash()) : "First record must have GENESIS_HASH";
        assert r1.currentHash() != null && !r1.currentHash().isBlank() : "Current hash must be generated";

        Solution.AuditRecord r2 = ledger.appendEvent("EVT-2", "dr_wilson", "PAT-10", "WRITE_PRESCRIPTION", "WARNING");
        assert r2.previousHash().equals(r1.currentHash()) : "Second record previousHash must match first record currentHash";
        assert ledger.verifyIntegrity() : "Ledger must be cryptographically intact";
        System.out.println("Lab 1 Test 2 Passed: Genesis hash and chaining verified.");
    }

    public static void testFieldValidationExceptions() {
        Solution.ClinicalAuditLedger ledger = new Solution.ClinicalAuditLedger();
        String[][] badEvents = new String[][] {
            {"", "ACTOR", "PAT", "ACTION", "INFO"},
            {"EVT", "", "PAT", "ACTION", "INFO"},
            {"EVT", "ACTOR", "", "ACTION", "INFO"},
            {"EVT", "ACTOR", "PAT", "", "INFO"},
            {"EVT", "ACTOR", "PAT", "ACTION", ""}
        };

        for (String[] ev : badEvents) {
            boolean caught = false;
            try {
                ledger.appendEvent(ev[0], ev[1], ev[2], ev[3], ev[4]);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assert caught : "Should reject empty fields: " + Arrays.toString(ev);
        }
        System.out.println("Lab 1 Test 3 Passed: Empty field validations verified.");
    }

    public static void testInvalidSeverityException() {
        Solution.ClinicalAuditLedger ledger = new Solution.ClinicalAuditLedger();
        boolean caught = false;
        try {
            ledger.appendEvent("EVT-1", "dr_house", "PAT-10", "READ_EHR", "SUPER_CRITICAL");
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assert caught : "Should reject unknown severity level";
        System.out.println("Lab 1 Test 4 Passed: Invalid severity rejection verified.");
    }

    public static void testTamperDetectionAlteredAction() {
        Solution.ClinicalAuditLedger ledger = new Solution.ClinicalAuditLedger();
        ledger.appendEvent("EVT-1", "dr_house", "PAT-10", "READ_EHR", "INFO");
        ledger.appendEvent("EVT-2", "nurse_jackie", "PAT-10", "DISPENSE_MED", "INFO");

        // Tamper with record 1 action in-place
        Solution.AuditRecord original = ledger.getRecords().get(0);
        Solution.AuditRecord tampered = new Solution.AuditRecord(
            original.eventId(),
            original.actorId(),
            original.patientId(),
            "ALTERED_ACTION_FORGERY", // tampered action
            original.severity(),
            original.previousHash(),
            original.currentHash()
        );
        ledger.getRecords().set(0, tampered);

        assert !ledger.verifyIntegrity() : "Tampered action must fail cryptographic integrity check";
        System.out.println("Lab 1 Test 5 Passed: Tamper detection (modified action) verified.");
    }

    public static void testTamperDetectionForgedPreviousHash() {
        Solution.ClinicalAuditLedger ledger = new Solution.ClinicalAuditLedger();
        ledger.appendEvent("EVT-1", "dr_house", "PAT-10", "READ_EHR", "INFO");
        ledger.appendEvent("EVT-2", "nurse_jackie", "PAT-10", "DISPENSE_MED", "INFO");

        Solution.AuditRecord r2 = ledger.getRecords().get(1);
        Solution.AuditRecord forged = new Solution.AuditRecord(
            r2.eventId(),
            r2.actorId(),
            r2.patientId(),
            r2.action(),
            r2.severity(),
            "FORGED_PREVIOUS_HASH_00000000000000000000000000000000000000000000",
            r2.currentHash()
        );
        ledger.getRecords().set(1, forged);

        assert !ledger.verifyIntegrity() : "Forged previous hash must fail integrity check";
        System.out.println("Lab 1 Test 6 Passed: Tamper detection (forged previous hash) verified.");
    }

    public static void testFindCriticalBreaches() {
        Solution.ClinicalAuditLedger ledger = new Solution.ClinicalAuditLedger();
        ledger.appendEvent("EVT-1", "dr_house", "PAT-10", "READ_EHR", "INFO");
        ledger.appendEvent("EVT-2", "admin", "PAT-10", "OVERRIDE_WARNING", "WARNING");
        ledger.appendEvent("EVT-3", "unknown_user", "PAT-10", "EXPORT_PHI", "CRITICAL");

        List<Solution.AuditRecord> breaches = ledger.findCriticalBreaches();
        assert breaches.size() == 1 : "Expected 1 critical breach";
        assert "EVT-3".equals(breaches.get(0).eventId()) : "Breach event should be EVT-3";
        System.out.println("Lab 1 Test 7 Passed: Critical breach detection verified.");
    }
}
