import java.util.*;

public class MockingAndIsolationSolutions {

    public interface EhrApiClient {
        List<String> getPatientAllergies(String patientId);
    }

    public static class PharmacyPrescriptionService {
        private final EhrApiClient ehrClient;

        public PharmacyPrescriptionService(EhrApiClient ehrClient) {
            this.ehrClient = ehrClient;
        }

        public String checkPrescriptionSafety(String patientId, String drugCode) {
            if (patientId == null || patientId.isBlank()) {
                throw new IllegalArgumentException("Patient ID cannot be empty");
            }
            if (drugCode == null || drugCode.isBlank()) {
                throw new IllegalArgumentException("Drug code cannot be empty");
            }

            List<String> allergies = ehrClient.getPatientAllergies(patientId);
            String normDrug = drugCode.trim().toUpperCase();

            for (String allergy : allergies) {
                if (allergy.equalsIgnoreCase(normDrug)) {
                    return "BLOCKED_ALLERGIC_REACTION";
                }
            }
            return "CLEARED_FOR_DISPENSING";
        }
    }

    // ==========================================
    // Test Double (Mock / Spy Pattern)
    // ==========================================

    public static class MockEhrApiClient implements EhrApiClient {
        private final Map<String, List<String>> stubbedResponses = new HashMap<>();
        private final List<String> recordedCalls = new ArrayList<>();
        private boolean shouldThrow = false;
        private RuntimeException exceptionToThrow;

        public void whenGetPatientAllergies(String patientId, List<String> returnAllergies) {
            stubbedResponses.put(patientId, returnAllergies);
        }

        public void whenGetPatientAllergiesThenThrow(RuntimeException ex) {
            this.shouldThrow = true;
            this.exceptionToThrow = ex;
        }

        @Override
        public List<String> getPatientAllergies(String patientId) {
            recordedCalls.add(patientId);
            if (shouldThrow) {
                throw exceptionToThrow;
            }
            return stubbedResponses.getOrDefault(patientId, Collections.emptyList());
        }

        public int getInvocationCount() {
            return recordedCalls.size();
        }

        public String getLastCalledPatientId() {
            return recordedCalls.isEmpty() ? null : recordedCalls.get(recordedCalls.size() - 1);
        }
    }

    // ==========================================
    // Test Suites
    // ==========================================

    public static void testClearedPrescriptionWhenNoAllergies() {
        MockEhrApiClient mockClient = new MockEhrApiClient();
        mockClient.whenGetPatientAllergies("PAT-101", List.of("PENICILLIN", "SULFA"));

        PharmacyPrescriptionService service = new PharmacyPrescriptionService(mockClient);
        String status = service.checkPrescriptionSafety("PAT-101", "AMOXICILLIN_CLAVULANATE");

        assert "CLEARED_FOR_DISPENSING".equals(status) : "Expected CLEARED, got " + status;
        assert mockClient.getInvocationCount() == 1 : "Expected exactly 1 call to EHR API";
        assert "PAT-101".equals(mockClient.getLastCalledPatientId()) : "Expected patient PAT-101 called";
        System.out.println("Test 1 Passed: Cleared prescription with interaction verification passed.");
    }

    public static void testBlockedPrescriptionWhenDrugInAllergyList() {
        MockEhrApiClient mockClient = new MockEhrApiClient();
        mockClient.whenGetPatientAllergies("PAT-202", List.of("PENICILLIN", "IBUPROFEN"));

        PharmacyPrescriptionService service = new PharmacyPrescriptionService(mockClient);
        String status = service.checkPrescriptionSafety("PAT-202", "PENICILLIN");

        assert "BLOCKED_ALLERGIC_REACTION".equals(status) : "Expected BLOCKED, got " + status;
        assert mockClient.getInvocationCount() == 1 : "Expected 1 call to EHR API";
        System.out.println("Test 2 Passed: Blocked allergy reaction verified.");
    }

    public static void testServicePropagatesEhrException() {
        MockEhrApiClient mockClient = new MockEhrApiClient();
        mockClient.whenGetPatientAllergiesThenThrow(new RuntimeException("EHR API Gateway Offline"));

        PharmacyPrescriptionService service = new PharmacyPrescriptionService(mockClient);

        boolean caught = false;
        try {
            service.checkPrescriptionSafety("PAT-303", "ASPIRIN");
        } catch (RuntimeException e) {
            caught = true;
            assert e.getMessage().contains("EHR API Gateway Offline") : "Unexpected exception message";
        }
        assert caught : "Should propagate EHR API failure";
        assert mockClient.getInvocationCount() == 1 : "Should have attempted call before failing";
        System.out.println("Test 3 Passed: Exception propagation verified.");
    }

    public static void main(String[] args) {
        testClearedPrescriptionWhenNoAllergies();
        testBlockedPrescriptionWhenDrugInAllergyList();
        testServicePropagatesEhrException();
        System.out.println("All Unit 5.4 Exercise Solutions Passed Successfully!");
    }
}
