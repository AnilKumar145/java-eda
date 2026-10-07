import java.util.*;

public class Solution {

    public record BloodProfile(String abo, boolean rhPositive) {}

    public record TransfusionApproval(boolean isApproved, String riskRating, String notes) {}

    public static class TransfusionIncompatibilityException extends RuntimeException {
        private final BloodProfile donor;
        private final BloodProfile recipient;
        private final String detail;

        public TransfusionIncompatibilityException(BloodProfile donor, BloodProfile recipient, String detail) {
            super("Incompatible transfusion: " + detail);
            this.donor = donor;
            this.recipient = recipient;
            this.detail = detail;
        }

        public BloodProfile getDonor() { return donor; }
        public BloodProfile getRecipient() { return recipient; }
        public String getDetail() { return detail; }
    }

    public static class TransfusionCompatibilityGuard {
        private static final Set<String> VALID_ABO = Set.of("O", "A", "B", "AB");
        private static final Map<String, Set<String>> ABO_MAP = Map.of(
            "O", Set.of("O", "A", "B", "AB"),
            "A", Set.of("A", "AB"),
            "B", Set.of("B", "AB"),
            "AB", Set.of("AB")
        );

        public static TransfusionApproval authorizeTransfusion(BloodProfile donor, BloodProfile recipient) {
            if (donor == null || recipient == null) {
                throw new IllegalArgumentException("Blood profiles cannot be null");
            }

            String donorAbo = normalizeAbo(donor.abo());
            String recipAbo = normalizeAbo(recipient.abo());

            // Rh factor check: Rh+ cannot donate to Rh-
            if (donor.rhPositive() && !recipient.rhPositive()) {
                throw new TransfusionIncompatibilityException(
                    donor, recipient, "Fatal Rh mismatch: Rh+ donor cannot donate to Rh- recipient"
                );
            }

            // ABO compatibility check
            if (!ABO_MAP.get(donorAbo).contains(recipAbo)) {
                throw new TransfusionIncompatibilityException(
                    donor, recipient, "ABO mismatch: " + donorAbo + " cannot donate to " + recipAbo
                );
            }

            return new TransfusionApproval(true, "STANDARD_CROSSMATCH", "Compatible blood units verified");
        }

        private static String normalizeAbo(String raw) {
            if (raw == null || raw.isBlank()) {
                throw new IllegalArgumentException("Blood group cannot be empty");
            }
            String norm = raw.trim().toUpperCase();
            if (!VALID_ABO.contains(norm)) {
                throw new IllegalArgumentException("Invalid ABO blood group: " + raw);
            }
            return norm;
        }
    }
}
