public class StarterCode {

    public record PatientVitals(int heartRate, int respiratoryRate, int spo2, int systolicBp) {}

    public record TriageAssessment(int totalScore, int acuityTier, String acuityName, boolean resuscitationRequired) {}

    public static class ClinicalTriageEngine {

        public static TriageAssessment scorePatient(PatientVitals vitals) {
            // TODO: Validate vitals within physiological boundaries.
            // Calculate MEWS partial points for HR, RR, SpO2, and Systolic BP.
            // Assign Acuity Tier 1, 2, 3, or 4 and return TriageAssessment.
            throw new UnsupportedOperationException("TODO: Implement scorePatient");
        }
    }
}
