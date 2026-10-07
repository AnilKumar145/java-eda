public class JUnitFundamentalsSolutions {

    public static class InsulinBolusCalculator {

        public double calculateCarbBolus(double carbGrams, double icrRatio) {
            if (carbGrams < 0) {
                throw new IllegalArgumentException("Carbohydrate grams cannot be negative: " + carbGrams);
            }
            if (icrRatio <= 0) {
                throw new IllegalArgumentException("Insulin-to-carb ratio must be positive: " + icrRatio);
            }
            return Math.round((carbGrams / icrRatio) * 10.0) / 10.0;
        }

        public double calculateCorrectionBolus(double currentGlucose, double targetGlucose, double isf) {
            if (currentGlucose <= 0 || targetGlucose <= 0) {
                throw new IllegalArgumentException("Blood glucose values must be strictly positive");
            }
            if (isf <= 0) {
                throw new IllegalArgumentException("Insulin sensitivity factor must be positive: " + isf);
            }
            if (currentGlucose <= targetGlucose) {
                return 0.0;
            }
            double correction = (currentGlucose - targetGlucose) / isf;
            return Math.round(correction * 10.0) / 10.0;
        }

        public double calculateTotalBolus(
            double carbGrams,
            double icrRatio,
            double currentGlucose,
            double targetGlucose,
            double isf
        ) {
            double carbUnits = calculateCarbBolus(carbGrams, icrRatio);
            double corrUnits = calculateCorrectionBolus(currentGlucose, targetGlucose, isf);
            return Math.round((carbUnits + corrUnits) * 10.0) / 10.0;
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
        // 60g carbs with 1:15 ratio -> 4.0 units
        double units = calculator.calculateCarbBolus(60.0, 15.0);
        assert Math.abs(units - 4.0) < 0.001 : "Expected 4.0 units, got " + units;
        tearDown();
        System.out.println("Test 1 Passed: Carb bolus calculation verified.");
    }

    public void testCorrectionBolusHyperglycemia() {
        setUp();
        // Current 220 mg/dL, Target 100 mg/dL, ISF 40 mg/dL per unit -> (220-100)/40 = 3.0 units
        double corr = calculator.calculateCorrectionBolus(220.0, 100.0, 40.0);
        assert Math.abs(corr - 3.0) < 0.001 : "Expected 3.0 correction units, got " + corr;
        tearDown();
        System.out.println("Test 2 Passed: Correction bolus for hyperglycemia verified.");
    }

    public void testCorrectionBolusZeroWhenAtOrBelowTarget() {
        setUp();
        // Current 95 mg/dL, Target 100 mg/dL -> 0 units correction
        double corr = calculator.calculateCorrectionBolus(95.0, 100.0, 40.0);
        assert Math.abs(corr - 0.0) < 0.001 : "Expected 0.0 correction units when glucose <= target";
        tearDown();
        System.out.println("Test 3 Passed: Zero correction below target verified.");
    }

    public void testTotalBolusCombined() {
        setUp();
        // 75g carbs at 1:10 ratio (7.5 units) + (180 - 100)/50 (1.6 units) = 9.1 units
        double total = calculator.calculateTotalBolus(75.0, 10.0, 180.0, 100.0, 50.0);
        assert Math.abs(total - 9.1) < 0.001 : "Expected 9.1 total units, got " + total;
        tearDown();
        System.out.println("Test 4 Passed: Total bolus summation verified.");
    }

    public void testNegativeCarbsThrowsIllegalArgumentException() {
        setUp();
        boolean caught = false;
        try {
            calculator.calculateCarbBolus(-20.0, 15.0);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assert caught : "Negative carbs must throw IllegalArgumentException";
        tearDown();
        System.out.println("Test 5 Passed: Negative carb input rejected.");
    }

    public static void main(String[] args) {
        JUnitFundamentalsSolutions testRunner = new JUnitFundamentalsSolutions();
        testRunner.testCarbBolusCalculation();
        testRunner.testCorrectionBolusHyperglycemia();
        testRunner.testCorrectionBolusZeroWhenAtOrBelowTarget();
        testRunner.testTotalBolusCombined();
        testRunner.testNegativeCarbsThrowsIllegalArgumentException();
        System.out.println("All Unit 5.2 Exercise Solutions Passed Successfully!");
    }
}
