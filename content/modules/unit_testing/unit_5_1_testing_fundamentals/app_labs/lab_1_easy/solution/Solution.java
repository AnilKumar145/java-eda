public class Solution {

    public record PatientVitals(int heartRate, int respiratoryRate, int spo2, int systolicBp) {}

    public record TriageAssessment(int totalScore, int acuityTier, String acuityName, boolean resuscitationRequired) {}

    public static class ClinicalTriageEngine {

        public static TriageAssessment scorePatient(PatientVitals vitals) {
            validateVitals(vitals);

            int score = 0;

            // Heart Rate
            int hr = vitals.heartRate();
            if (hr <= 40) score += 2;
            else if (hr <= 50) score += 1;
            else if (hr <= 100) score += 0;
            else if (hr <= 110) score += 1;
            else if (hr <= 129) score += 2;
            else score += 3;

            // Respiratory Rate
            int rr = vitals.respiratoryRate();
            if (rr < 9) score += 2;
            else if (rr <= 14) score += 0;
            else if (rr <= 20) score += 1;
            else if (rr <= 29) score += 2;
            else score += 3;

            // SpO2
            int o2 = vitals.spo2();
            if (o2 < 85) score += 3;
            else if (o2 <= 89) score += 2;
            else if (o2 <= 92) score += 1;
            else score += 0;

            // Systolic BP
            int sbp = vitals.systolicBp();
            if (sbp <= 70) score += 3;
            else if (sbp <= 80) score += 2;
            else if (sbp <= 100) score += 1;
            else if (sbp <= 199) score += 0;
            else score += 2;

            if (score >= 7) {
                return new TriageAssessment(score, 1, "RESUSCITATION", true);
            } else if (score >= 4) {
                return new TriageAssessment(score, 2, "EMERGENT", false);
            } else if (score >= 1) {
                return new TriageAssessment(score, 3, "URGENT", false);
            } else {
                return new TriageAssessment(score, 4, "NON_URGENT", false);
            }
        }

        private static void validateVitals(PatientVitals vitals) {
            if (vitals.heartRate() < 10 || vitals.heartRate() > 300) {
                throw new IllegalArgumentException("Invalid heart rate: " + vitals.heartRate());
            }
            if (vitals.respiratoryRate() < 4 || vitals.respiratoryRate() > 80) {
                throw new IllegalArgumentException("Invalid respiratory rate: " + vitals.respiratoryRate());
            }
            if (vitals.spo2() < 40 || vitals.spo2() > 100) {
                throw new IllegalArgumentException("Invalid SpO2: " + vitals.spo2());
            }
            if (vitals.systolicBp() < 30 || vitals.systolicBp() > 300) {
                throw new IllegalArgumentException("Invalid systolic BP: " + vitals.systolicBp());
            }
        }
    }
}
