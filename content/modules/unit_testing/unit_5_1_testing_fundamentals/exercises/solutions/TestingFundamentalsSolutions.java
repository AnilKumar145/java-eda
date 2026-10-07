public class TestingFundamentalsSolutions {

    public static class CardiacOutputCalculator {
        public double calculateCardiacIndex(double cardiacOutputLpm, double bsaM2) {
            if (cardiacOutputLpm <= 0) {
                throw new IllegalArgumentException("Cardiac output must be strictly positive: " + cardiacOutputLpm);
            }
            if (bsaM2 <= 0) {
                throw new IllegalArgumentException("Body surface area must be strictly positive: " + bsaM2);
            }
            return Math.round((cardiacOutputLpm / bsaM2) * 100.0) / 100.0;
        }

        public boolean isCardiogenicShock(double cardiacIndex) {
            return cardiacIndex < 2.2;
        }
    }

    public static class ArterialBloodGasEvaluator {
        public String evaluatePh(double ph) {
            if (ph < 6.8 || ph > 7.8) {
                throw new IllegalArgumentException("pH outside physiological survival range (6.8 - 7.8): " + ph);
            }
            if (ph < 7.35) {
                return "ACIDOSIS";
            } else if (ph > 7.45) {
                return "ALKALOSIS";
            } else {
                return "NORMAL";
            }
        }
    }

    // ==========================================
    // Test Suites (AAA Pattern)
    // ==========================================

    public static void testCardiacIndexNormalCalculation() {
        // Arrange
        CardiacOutputCalculator calc = new CardiacOutputCalculator();
        double co = 5.0; // L/min
        double bsa = 1.8; // m^2

        // Act
        double index = calc.calculateCardiacIndex(co, bsa);

        // Assert
        assert Math.abs(index - 2.78) < 0.001 : "Expected cardiac index 2.78 L/min/m^2, got " + index;
        assert !calc.isCardiogenicShock(index) : "Index 2.78 should not be in cardiogenic shock";
        System.out.println("Test 1 Passed: Cardiac index normal calculation verified.");
    }

    public static void testCardiacIndexShockState() {
        // Arrange
        CardiacOutputCalculator calc = new CardiacOutputCalculator();
        double co = 3.0;
        double bsa = 2.0;

        // Act
        double index = calc.calculateCardiacIndex(co, bsa);

        // Assert
        assert Math.abs(index - 1.50) < 0.001 : "Expected cardiac index 1.50, got " + index;
        assert calc.isCardiogenicShock(index) : "Index 1.50 must trigger cardiogenic shock flag";
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

        boolean caughtInvalidBsa = false;
        try {
            calc.calculateCardiacIndex(5.0, 0.0);
        } catch (IllegalArgumentException e) {
            caughtInvalidBsa = true;
        }
        assert caughtInvalidBsa : "Should throw IllegalArgumentException for zero BSA";
        System.out.println("Test 3 Passed: Invalid cardiac input rejection verified.");
    }

    public static void testBloodGasEvaluatorClassifications() {
        ArterialBloodGasEvaluator abg = new ArterialBloodGasEvaluator();

        assert "NORMAL".equals(abg.evaluatePh(7.40)) : "pH 7.40 should be NORMAL";
        assert "ACIDOSIS".equals(abg.evaluatePh(7.30)) : "pH 7.30 should be ACIDOSIS";
        assert "ALKALOSIS".equals(abg.evaluatePh(7.50)) : "pH 7.50 should be ALKALOSIS";

        boolean caughtExtreme = false;
        try {
            abg.evaluatePh(6.50);
        } catch (IllegalArgumentException e) {
            caughtExtreme = true;
        }
        assert caughtExtreme : "Should reject pH < 6.8";
        System.out.println("Test 4 Passed: ABG classifications and bounds verified.");
    }

    public static void main(String[] args) {
        testCardiacIndexNormalCalculation();
        testCardiacIndexShockState();
        testCardiacIndexRejectsNonPositiveInputs();
        testBloodGasEvaluatorClassifications();
        System.out.println("All Unit 5.1 Exercise Solutions Passed Successfully!");
    }
}
