public class JUnitFundamentalsExercises {

    public static class InsulinBolusCalculator {

        public double calculateCarbBolus(double carbGrams, double icrRatio) {
            // TODO: Validate carbGrams >= 0 and icrRatio > 0 (else throw IllegalArgumentException).
            // Calculate carbGrams / icrRatio rounded to 1 decimal place.
            throw new UnsupportedOperationException("TODO: Implement calculateCarbBolus");
        }

        public double calculateCorrectionBolus(double currentGlucose, double targetGlucose, double isf) {
            // TODO: Validate currentGlucose > 0, targetGlucose > 0, isf > 0.
            // If currentGlucose <= targetGlucose return 0.0.
            // Otherwise return (currentGlucose - targetGlucose) / isf rounded to 1 decimal place.
            throw new UnsupportedOperationException("TODO: Implement calculateCorrectionBolus");
        }

        public double calculateTotalBolus(
            double carbGrams,
            double icrRatio,
            double currentGlucose,
            double targetGlucose,
            double isf
        ) {
            // TODO: Sum carb bolus + correction bolus rounded to 1 decimal place.
            throw new UnsupportedOperationException("TODO: Implement calculateTotalBolus");
        }
    }

    // ==========================================
    // Test Suites (JUnit Simulation Harness)
    // ==========================================

    private InsulinBolusCalculator calculator;

    public void setUp() {
        this.calculator = new InsulinBolusCalculator();
    }

    public void tearDown() {
        this.calculator = null;
    }

    public void testCarbBolusCalculation() {
        setUp();
        double units = calculator.calculateCarbBolus(60.0, 15.0);
        assert Math.abs(units - 4.0) < 0.001 : "Expected 4.0 units";
        tearDown();
        System.out.println("Test 1 Passed: Carb bolus calculation verified.");
    }

    public void testCorrectionBolusHyperglycemia() {
        setUp();
        double corr = calculator.calculateCorrectionBolus(220.0, 100.0, 40.0);
        assert Math.abs(corr - 3.0) < 0.001 : "Expected 3.0 correction units";
        tearDown();
        System.out.println("Test 2 Passed: Correction bolus verified.");
    }

    public void testTotalBolusCombined() {
        setUp();
        double total = calculator.calculateTotalBolus(75.0, 10.0, 180.0, 100.0, 50.0);
        assert Math.abs(total - 9.1) < 0.001 : "Expected 9.1 total units";
        tearDown();
        System.out.println("Test 3 Passed: Total bolus verified.");
    }

    public static void main(String[] args) {
        JUnitFundamentalsExercises testRunner = new JUnitFundamentalsExercises();
        testRunner.testCarbBolusCalculation();
        testRunner.testCorrectionBolusHyperglycemia();
        testRunner.testTotalBolusCombined();
        System.out.println("All Unit 5.2 Exercises Passed Successfully!");
    }
}
