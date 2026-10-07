import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Solution {

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
        public static final Set<String> VALID_SEVERITIES = Set.of("INFO", "WARNING", "CRITICAL");

        private final List<AuditRecord> records = new ArrayList<>();

        public static String computeHash(
            String eventId,
            String actorId,
            String patientId,
            String action,
            String severity,
            String previousHash
        ) {
            String payload = String.format("%s|%s|%s|%s|%s|%s",
                eventId, actorId, patientId, action, severity, previousHash);
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
                StringBuilder hex = new StringBuilder();
                for (byte b : hashBytes) {
                    hex.append(String.format("%02x", b));
                }
                return hex.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 algorithm missing", e);
            }
        }

        public AuditRecord appendEvent(
            String eventId,
            String actorId,
            String patientId,
            String action,
            String severity
        ) {
            if (eventId == null || eventId.isBlank()
                || actorId == null || actorId.isBlank()
                || patientId == null || patientId.isBlank()
                || action == null || action.isBlank()
                || severity == null || severity.isBlank()) {
                throw new IllegalArgumentException("All audit log fields must be non-empty strings.");
            }

            String normSev = severity.trim().toUpperCase();
            if (!VALID_SEVERITIES.contains(normSev)) {
                throw new IllegalArgumentException("Invalid severity: " + severity);
            }

            String prevHash = records.isEmpty() ? GENESIS_HASH : records.get(records.size() - 1).currentHash();
            String currHash = computeHash(eventId.trim(), actorId.trim(), patientId.trim(), action.trim().toUpperCase(), normSev, prevHash);

            AuditRecord rec = new AuditRecord(
                eventId.trim(),
                actorId.trim(),
                patientId.trim(),
                action.trim().toUpperCase(),
                normSev,
                prevHash,
                currHash
            );
            records.add(rec);
            return rec;
        }

        public boolean verifyIntegrity() {
            if (records.isEmpty()) {
                return true;
            }

            String expectedPrev = GENESIS_HASH;
            for (AuditRecord rec : records) {
                if (!rec.previousHash().equals(expectedPrev)) {
                    return false;
                }

                String recalculated = computeHash(
                    rec.eventId(),
                    rec.actorId(),
                    rec.patientId(),
                    rec.action(),
                    rec.severity(),
                    rec.previousHash()
                );
                if (!rec.currentHash().equals(recalculated)) {
                    return false;
                }

                expectedPrev = rec.currentHash();
            }
            return true;
        }

        public List<AuditRecord> findCriticalBreaches() {
            List<AuditRecord> critical = new ArrayList<>();
            for (AuditRecord rec : records) {
                if ("CRITICAL".equals(rec.severity())) {
                    critical.add(rec);
                }
            }
            return critical;
        }

        public List<AuditRecord> getRecords() {
            return records;
        }
    }
}
