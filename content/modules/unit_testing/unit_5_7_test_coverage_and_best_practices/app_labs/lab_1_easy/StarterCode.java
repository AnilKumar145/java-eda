import java.util.*;

public class StarterCode {

    public record AuditRecord(
        String eventId,
        String actorId,
        String patientId,
        String action,
        String severity,
        String previousHash,
        String currentHash
    ) {}

    public static class ClinicalAuditLedger {
        public static final String GENESIS_HASH = "GENESIS_00000000000000000000000000000000000000000000000000000000";

        public AuditRecord appendEvent(
            String eventId,
            String actorId,
            String patientId,
            String action,
            String severity
        ) {
            // TODO: Validate arguments. Calculate previousHash and SHA-256 currentHash.
            throw new UnsupportedOperationException("TODO: Implement appendEvent");
        }

        public boolean verifyIntegrity() {
            // TODO: Verify entire hash chain. Return true if intact, false if tampered.
            throw new UnsupportedOperationException("TODO: Implement verifyIntegrity");
        }

        public List<AuditRecord> findCriticalBreaches() {
            // TODO: Filter records where severity is CRITICAL.
            throw new UnsupportedOperationException("TODO: Implement findCriticalBreaches");
        }
    }
}
