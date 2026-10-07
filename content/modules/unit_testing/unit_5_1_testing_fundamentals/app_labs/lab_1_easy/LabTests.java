public class LabTests {

    public static void main(String[] args) {
        testNormalVitalsScoresTier4NonUrgent();
        testModerateVitalsScoresTier2Emergent();
        testCriticalHypoxiaScoresTier1Resuscitation();
        testInvalidHeartRateRejection();
        testInvalidSpO2Rejection();
        System.out.println("All Unit 5.1 Lab 1 Tests Passed Successfully!");
    }

    public static void testNormalVitalsScoresTier4NonUrgent() {
        // Arrange
        Solution.PatientVitals vitals = new Solution.PatientVitals(72, 14, 99, 120);

        // Act
        Solution.TriageAssessment result = Solution.ClinicalTriageEngine.scorePatient(vitals);

        // Assert
        assert result.totalScore() == 0 : "Normal vitals score should be 0, got " + result.totalScore();
        assert result.acuityTier() == 4 : "Normal vitals should be Tier 4";
        assert "NON_URGENT".equals(result.acuityName()) : "Expected NON_URGENT";
        assert !result.resuscitationRequired() : "Resuscitation should not be required";
        System.out.println("Lab 1 Test 1 Passed: Normal vitals evaluated correctly.");
    }

    public static void testModerateVitalsScoresTier2Emergent() {
        // Arrange: HR 115 (2pts), RR 22 (2pts), SpO2 94 (0pts), BP 130 (0pts) -> Score = 4 (Tier 2)
        Solution.PatientVitals vitals = new Solution.PatientVitals(115, 22, 94, 130);

        // Act
        Solution.TriageAssessment result = Solution.ClinicalTriageEngine.scorePatient(vitals);

        // Assert
        assert result.totalScore() == 4 : "Expected score 4, got " + result.totalScore();
        assert result.acuityTier() == 2 : "Expected Tier 2";
        assert "EMERGENT".equals(result.acuityName()) : "Expected EMERGENT";
        assert !result.resuscitationRequired() : "Resuscitation should not be required for Tier 2";
        System.out.println("Lab 1 Test 2 Passed: Moderate vitals evaluated correctly.");
    }

    public static void testCriticalHypoxiaScoresTier1Resuscitation() {
        // Arrange: HR 135 (3pts), RR 32 (3pts), SpO2 82 (3pts), BP 65 (3pts) -> Score = 12 (Tier 1)
        Solution.PatientVitals vitals = new Solution.PatientVitals(135, 32, 82, 65);

        // Act
        Solution.TriageAssessment result = Solution.ClinicalTriageEngine.scorePatient(vitals);

        // Assert
        assert result.totalScore() == 12 : "Expected score 12, got " + result.totalScore();
        assert result.acuityTier() == 1 : "Expected Tier 1 Resuscitation";
        assert "RESUSCITATION".equals(result.acuityName()) : "Expected RESUSCITATION";
        assert result.resuscitationRequired() : "Resuscitation must be flagged true";
        System.out.println("Lab 1 Test 3 Passed: Critical vitals evaluated correctly.");
    }

    public static void testInvalidHeartRateRejection() {
        boolean caught = false;
        try {
            Solution.ClinicalTriageEngine.scorePatient(new Solution.PatientVitals(5, 14, 98, 120));
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assert caught : "Should reject heart rate < 10";
        System.out.println("Lab 1 Test 4 Passed: Low HR validation verified.");
    }

    public static void testInvalidSpO2Rejection() {
        boolean caught = false;
        try {
            Solution.ClinicalTriageEngine.scorePatient(new Solution.PatientVitals(75, 14, 105, 120));
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assert caught : "Should reject SpO2 > 100";
        System.out.println("Lab 1 Test 5 Passed: High SpO2 validation verified.");
    }
}
