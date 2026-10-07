public class StarterCode {

    public record BloodProfile(String abo, boolean rhPositive) {}

    public record TransfusionApproval(boolean isApproved, String riskRating, String notes) {}

    public static class TransfusionIncompatibilityException extends RuntimeException {
        public TransfusionIncompatibilityException(BloodProfile donor, BloodProfile recipient, String detail) {
            super("Incompatible transfusion: " + detail);
        }
    }

    public static class TransfusionCompatibilityGuard {
        public static TransfusionApproval authorizeTransfusion(BloodProfile donor, BloodProfile recipient) {
            // TODO: Validate inputs non-null and ABO groups in {O, A, B, AB}.
            // Check Rh compatibility (Rh+ donor cannot donate to Rh- recipient).
            // Check ABO compatibility matrix.
            // Throw TransfusionIncompatibilityException if incompatible.
            // Return TransfusionApproval if compatible.
            throw new UnsupportedOperationException("TODO: Implement authorizeTransfusion");
        }
    }
}
