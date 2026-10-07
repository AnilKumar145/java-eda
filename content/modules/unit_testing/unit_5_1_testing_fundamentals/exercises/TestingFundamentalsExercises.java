public class TestingFundamentalsExercises {

    public static class CardiacOutputCalculator {
        public double calculateCardiacIndex(double cardiacOutputLpm, double bsaM2) {
            // TODO: Validate cardiacOutputLpm > 0 and bsaM2 > 0 (else throw IllegalArgumentException).
            // Calculate cardiac index = cardiacOutputLpm / bsaM2 rounded to 2 decimal places.
            throw new UnsupportedOperationException("TODO: Implement calculateCardiacIndex");
        }

        public boolean isCardiogenicShock(double cardiacIndex) {
            // TODO: Return true if cardiacIndex < 2.2
            throw new UnsupportedOperationException("TODO: Implement isCardiogenicShock");
        }
    }

    public static class ArterialBloodGasEvaluator {
        public String evaluatePh(double ph) {
            // TODO: Validate 6.8 <= ph <= 7.8 (else throw IllegalArgumentException).
            // ph < 7.35 -> "ACIDOSIS"
            // ph > 7.45 -> "ALKALOSIS"
            // otherwise -> "NORMAL"
            throw new UnsupportedOperationException("TODO: Implement evaluatePh");
        }
    }

    // ==========================================
    // Test Suites (AAA Pattern)
    // ==========================================

    public static void testCardiacIndexNormalCalculation() {
        CardiacOutputCalculator calc = new CardiacOutputCalculator();
        double index = calc.calculateCardiacIndex(5.0, 1.8);
        assert Math.abs(index - 2.78) < 0.001 : "Expected 2.78";
        assert !calc.isCardiogenicShock(index) : "Should not be in shock";
        System.out.println("Test 1 Passed: Cardiac index normal calculation verified.");
    }

    public static void testCardiacIndexShockState() {
        CardiacOutputCalculator calc = new CardiacOutputCalculator();
        double index = calc.calculateCardiacIndex(3.0, 2.0);
        assert Math.abs(index - 1.50) < 0.001 : "Expected 1.50";
        assert calc.isCardiogenicShock(index) : "Index 1.50 must trigger cardiogenic shock";
        System.out.println("Test 2 Passed: Cardiogenic shock threshold verified.");
    }

    public static void testCardiacIndexRejectsNonPositiveInputs() {
        CardiacOutputCalculator calc = new CardiacOutputCalculator();
        boolean caughtInvalidCo = false;
        try {
            calc.calculateCardiacIndex(-1.0, 1.8);
        } catch (IllegalArgumentException e) {
            caughtInvalidCo = true;
        }
        assert caughtInvalidCo : "Should throw IllegalArgumentException for negative cardiac output";
        System.out.println("Test 3 Passed: Invalid cardiac input rejection verified.");
    }

    public static void testBloodGasEvaluatorClassifications() {
        ArterialBloodGasEvaluator abg = new ArterialBloodGasEvaluator();
        assert "NORMAL".equals(abg.evaluatePh(7.40)) : "pH 7.40 should be NORMAL";
        assert "ACIDOSIS".equals(abg.evaluatePh(7.30)) : "pH 7.30 should be ACIDOSIS";
        assert "ALKALOSIS".equals(abg.evaluatePh(7.50)) : "pH 7.50 should be ALKALOSIS";
        System.out.println("Test 4 Passed: ABG classifications and bounds verified.");
    }

    public static void main(String[] args) {
        testCardiacIndexNormalCalculation();
        testCardiacIndexShockState();
        testCardiacIndexRejectsNonPositiveInputs();
        testBloodGasEvaluatorClassifications();
        System.out.println("All Unit 5.1 Exercises Passed Successfully!");
    }
}
