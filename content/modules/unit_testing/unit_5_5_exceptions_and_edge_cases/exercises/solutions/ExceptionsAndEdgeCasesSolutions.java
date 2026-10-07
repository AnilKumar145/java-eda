import java.util.*;

public class ExceptionsAndEdgeCasesSolutions {

    public static class IncompatibleBloodTransfusionException extends RuntimeException {
        private final String donor;
        private final String recipient;
        private final String reason;

        public IncompatibleBloodTransfusionException(String donor, String recipient, String reason) {
            super("Incompatible transfusion: " + donor + " to " + recipient + " (" + reason + ")");
            this.donor = donor;
            this.recipient = recipient;
            this.reason = reason;
        }

        public String getDonor() { return donor; }
        public String getRecipient() { return recipient; }
        public String getReason() { return reason; }
    }

    public static class BmiCalculator {
        public static double calculateBmi(double weightKg, double heightM) {
            if (weightKg < 1.0 || weightKg > 500.0) {
                throw new IllegalArgumentException("Weight outside physiological range (1.0 - 500.0 kg): " + weightKg);
            }
            if (heightM < 0.3 || heightM > 2.8) {
                throw new IllegalArgumentException("Height outside physiological range (0.3 - 2.8 m): " + heightM);
            }
            double bmi = weightKg / (heightM * heightM);
            return Math.round(bmi * 10.0) / 10.0;
        }
    }

    public static class BloodCompatibilityValidator {
        private static final Set<String> VALID_ABO = Set.of("O", "A", "B", "AB");
        private static final Map<String, Set<String>> COMPATIBILITY = Map.of(
            "O", Set.of("O", "A", "B", "AB"),
            "A", Set.of("A", "AB"),
            "B", Set.of("B", "AB"),
            "AB", Set.of("AB")
        );

        public static boolean validateAboCompatibility(String donor, String recipient) {
            if (donor == null || !VALID_ABO.contains(donor.trim().toUpperCase())) {
                throw new IllegalArgumentException("Invalid blood group: " + donor);
            }
            if (recipient == null || !VALID_ABO.contains(recipient.trim().toUpperCase())) {
                throw new IllegalArgumentException("Invalid blood group: " + recipient);
            }

            String d = donor.trim().toUpperCase();
            String r = recipient.trim().toUpperCase();

            if (COMPATIBILITY.get(d).contains(r)) {
                return true;
            }

            throw new IncompatibleBloodTransfusionException(d, r, "ABO group mismatch");
        }
    }

    // ==========================================
    // Test Suites
    // ==========================================

    public static void testBmiValidCalculation() {
        double bmi = BmiCalculator.calculateBmi(70.0, 1.75);
        assert Math.abs(bmi - 22.9) < 0.001 : "Expected BMI 22.9, got " + bmi;
        System.out.println("Test 1 Passed: Valid BMI calculation verified.");
    }

    public static void testBmiBoundaryViolations() {
        double[][] invalidInputs = new double[][] {
            {0.5, 1.75},   // weight too low
            {600.0, 1.75}, // weight too high
            {70.0, 0.2},   // height too low
            {70.0, 3.0},   // height too high
            {-10.0, 1.75}  // negative weight
        };

        for (double[] input : invalidInputs) {
            boolean caught = false;
            try {
                BmiCalculator.calculateBmi(input[0], input[1]);
            } catch (IllegalArgumentException e) {
                caught = true;
                assert e.getMessage().contains("physiological range") : "Unexpected message: " + e.getMessage();
            }
            assert caught : "Should reject invalid input: " + Arrays.toString(input);
        }
        System.out.println("Test 2 Passed: 5 BMI boundary violations rejected.");
    }

    public static void testBloodCompatibilitySuccess() {
        assert BloodCompatibilityValidator.validateAboCompatibility("O", "AB") : "O can donate to AB";
        assert BloodCompatibilityValidator.validateAboCompatibility("A", "A") : "A can donate to A";
        assert BloodCompatibilityValidator.validateAboCompatibility("B", "AB") : "B can donate to AB";
        System.out.println("Test 3 Passed: Compatible transfusions passed.");
    }

    public static void testBloodCompatibilityIncompatibleThrowsCustomException() {
        boolean caught = false;
        try {
            BloodCompatibilityValidator.validateAboCompatibility("A", "B");
        } catch (IncompatibleBloodTransfusionException e) {
            caught = true;
            assert "A".equals(e.getDonor()) : "Donor should be A";
            assert "B".equals(e.getRecipient()) : "Recipient should be B";
            assert "ABO group mismatch".equals(e.getReason()) : "Reason mismatch";
        }
        assert caught : "A to B must throw IncompatibleBloodTransfusionException";
        System.out.println("Test 4 Passed: Incompatible transfusion custom exception payload verified.");
    }

    public static void testBloodCompatibilityInvalidGroupThrowsIllegalArgumentException() {
        boolean caught = false;
        try {
            BloodCompatibilityValidator.validateAboCompatibility("X", "O");
        } catch (IllegalArgumentException e) {
            caught = true;
            assert e.getMessage().contains("Invalid blood group") : "Unexpected message";
        }
        assert caught : "Invalid blood group X must throw IllegalArgumentException";
        System.out.println("Test 5 Passed: Invalid blood group input rejected.");
    }

    public static void main(String[] args) {
        testBmiValidCalculation();
        testBmiBoundaryViolations();
        testBloodCompatibilitySuccess();
        testBloodCompatibilityIncompatibleThrowsCustomException();
        testBloodCompatibilityInvalidGroupThrowsIllegalArgumentException();
        System.out.println("All Unit 5.5 Exercise Solutions Passed Successfully!");
    }
}
