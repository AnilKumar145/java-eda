import java.util.*;

public class MockingAndIsolationExercises {

    public interface EhrApiClient {
        List<String> getPatientAllergies(String patientId);
    }

    public static class PharmacyPrescriptionService {
        private final EhrApiClient ehrClient;

        public PharmacyPrescriptionService(EhrApiClient ehrClient) {
            this.ehrClient = ehrClient;
        }

        public String checkPrescriptionSafety(String patientId, String drugCode) {
            // TODO: Validate patientId and drugCode are non-empty strings.
            // Query ehrClient.getPatientAllergies(patientId).
            // If any allergy matches drugCode (case-insensitive), return "BLOCKED_ALLERGIC_REACTION".
            // Otherwise return "CLEARED_FOR_DISPENSING".
            throw new UnsupportedOperationException("TODO: Implement checkPrescriptionSafety");
        }
    }

    public static class MockEhrApiClient implements EhrApiClient {
        // TODO: Implement mock storage, stubbing methods (whenGetPatientAllergies),
        // invocation count tracking, and recording called patientId arguments.
        @Override
        public List<String> getPatientAllergies(String patientId) {
            throw new UnsupportedOperationException("TODO: Implement mock getPatientAllergies");
        }
    }

    public static void main(String[] args) {
        System.out.println("Unit 5.4 Exercises Starter ready for implementation.");
    }
}
