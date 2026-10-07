public class LabTests {

    public static void main(String[] args) {
        testStandardPediatricDoseUncapped();
        testHeavyPediatricDoseHitsMaxCeiling();
        testNegativeWeightThrowsIllegalArgumentException();
        testZeroConcentrationThrowsIllegalArgumentException();
        System.out.println("All Unit 5.2 Lab 1 Tests Passed Successfully!");
    }

    public static void testStandardPediatricDoseUncapped() {
        // 12 kg toddler, 15 mg/kg paracetamol, 24 mg/mL syrup, 1000 mg adult max
        // Dose = 180 mg, Volume = 180 / 24 = 7.5 mL, capped = false
        Solution.DoseVolumeResult result = Solution.PediatricDosageCalculator.calculateLiquidVolume(
            12.0, 15.0, 24.0, 1000.0
        );

        assert Math.abs(result.doseMg() - 180.0) < 0.001 : "Expected 180.0 mg, got " + result.doseMg();
        assert Math.abs(result.volumeMl() - 7.50) < 0.001 : "Expected 7.50 mL, got " + result.volumeMl();
        assert !result.cappedAtAdultMax() : "180 mg is well below 1000 mg cap";
        System.out.println("Lab 1 Test 1 Passed: Standard uncapped dose verified.");
    }

    public static void testHeavyPediatricDoseHitsMaxCeiling() {
        // 45 kg adolescent, 15 mg/kg amoxicillin = 675 mg, capped at 500 mg adult max
        // Volume = 500 / 50 mg/mL = 10.0 mL, capped = true
        Solution.DoseVolumeResult result = Solution.PediatricDosageCalculator.calculateLiquidVolume(
            45.0, 15.0, 50.0, 500.0
        );

        assert Math.abs(result.doseMg() - 500.0) < 0.001 : "Expected capped dose 500.0 mg, got " + result.doseMg();
        assert Math.abs(result.volumeMl() - 10.00) < 0.001 : "Expected 10.00 mL, got " + result.volumeMl();
        assert result.cappedAtAdultMax() : "Dose must be flagged as capped at adult ceiling";
        System.out.println("Lab 1 Test 2 Passed: Adult ceiling clamping verified.");
    }

    public static void testNegativeWeightThrowsIllegalArgumentException() {
        boolean caught = false;
        try {
            Solution.PediatricDosageCalculator.calculateLiquidVolume(-5.0, 15.0, 24.0, 1000.0);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assert caught : "Negative weight must throw IllegalArgumentException";
        System.out.println("Lab 1 Test 3 Passed: Negative weight validation verified.");
    }

    public static void testZeroConcentrationThrowsIllegalArgumentException() {
        boolean caught = false;
        try {
            Solution.PediatricDosageCalculator.calculateLiquidVolume(12.0, 15.0, 0.0, 1000.0);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assert caught : "Zero concentration must throw IllegalArgumentException";
        System.out.println("Lab 1 Test 4 Passed: Zero concentration validation verified.");
    }
}
