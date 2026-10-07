import java.util.*;

public class ExceptionsAndEdgeCasesExercises {

    public static class IncompatibleBloodTransfusionException extends RuntimeException {
        // TODO: Store donor, recipient, reason fields and expose getters.
        public IncompatibleBloodTransfusionException(String donor, String recipient, String reason) {
            super("Incompatible transfusion: " + donor + " to " + recipient);
        }
    }

    public static class BmiCalculator {
        public static double calculateBmi(double weightKg, double heightM) {
            // TODO: Validate 1.0 <= weightKg <= 500.0 and 0.3 <= heightM <= 2.8.
            // Calculate BMI = weightKg / (heightM * heightM) rounded to 1 decimal place.
            throw new UnsupportedOperationException("TODO: Implement calculateBmi");
        }
    }

    public static class BloodCompatibilityValidator {
        public static boolean validateAboCompatibility(String donor, String recipient) {
            // TODO: Validate donor and recipient belong to {"O", "A", "B", "AB"}.
            // Check compatibility map. If incompatible, throw IncompatibleBloodTransfusionException.
            throw new UnsupportedOperationException("TODO: Implement validateAboCompatibility");
        }
    }

    public static void main(String[] args) {
        System.out.println("Unit 5.5 Exercises Starter ready for implementation.");
    }
}
