import java.util.*;

public class TestDataAndParameterizedSolutions {

    public record ReferenceRange(double lowerLimit, double upperLimit) {}

    public static class ClinicalReferenceRangeClassifier {

        private static final Map<String, ReferenceRange> RANGES = Map.of(
            "GLUCOSE", new ReferenceRange(70.0, 99.0),
            "POTASSIUM", new ReferenceRange(3.5, 5.0),
            "SODIUM", new ReferenceRange(135.0, 145.0)
        );

        public String classify(String analyte, double value) {
            String key = analyte.trim().toUpperCase();
            if (!RANGES.containsKey(key)) {
                throw new IllegalArgumentException("Unknown clinical analyte: " + analyte);
            }
            ReferenceRange range = RANGES.get(key);
            if (value < range.lowerLimit()) {
                return "LOW";
            } else if (value > range.upperLimit()) {
                return "HIGH";
            } else {
                return "NORMAL";
            }
        }
    }

    public static class LabOrder {
        private final String orderId;
        private final String patientId;
        private final String priority; // STAT, ROUTINE
        private final List<String> tests;

        public LabOrder(String orderId, String patientId, String priority, List<String> tests) {
            this.orderId = orderId;
            this.patientId = patientId;
            this.priority = priority;
            this.tests = tests;
        }

        public String getOrderId() { return orderId; }
        public String getPatientId() { return patientId; }
        public String getPriority() { return priority; }
        public List<String> getTests() { return tests; }
    }

    public static class LabOrderBuilder {
        private String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        private String patientId = "PAT-DEFAULT";
        private String priority = "ROUTINE";
        private List<String> tests = new ArrayList<>(List.of("GLUCOSE", "CBC"));

        public LabOrderBuilder withPatientId(String patientId) {
            this.patientId = patientId;
            return this;
        }

        public LabOrderBuilder asStatPriority() {
            this.priority = "STAT";
            return this;
        }

        public LabOrderBuilder withTests(List<String> tests) {
            this.tests = new ArrayList<>(tests);
            return this;
        }

        public LabOrder build() {
            return new LabOrder(orderId, patientId, priority, Collections.unmodifiableList(tests));
        }
    }

    // ==========================================
    // Parameterized Simulation Test Suites
    // ==========================================

    public static void testReferenceRangeParameterizedMatrix() {
        ClinicalReferenceRangeClassifier classifier = new ClinicalReferenceRangeClassifier();

        // [analyte, value, expectedStatus]
        Object[][] testData = new Object[][] {
            {"GLUCOSE", 65.0, "LOW"},
            {"GLUCOSE", 70.0, "NORMAL"},
            {"GLUCOSE", 85.0, "NORMAL"},
            {"GLUCOSE", 99.0, "NORMAL"},
            {"GLUCOSE", 140.0, "HIGH"},
            {"POTASSIUM", 3.1, "LOW"},
            {"POTASSIUM", 4.2, "NORMAL"},
            {"POTASSIUM", 5.8, "HIGH"},
            {"SODIUM", 130.0, "LOW"},
            {"SODIUM", 140.0, "NORMAL"},
            {"SODIUM", 152.0, "HIGH"}
        };

        for (Object[] row : testData) {
            String analyte = (String) row[0];
            double val = (Double) row[1];
            String expected = (String) row[2];

            String actual = classifier.classify(analyte, val);
            assert expected.equals(actual) : "Failed for " + analyte + " value " + val + ": expected " + expected + " but got " + actual;
        }
        System.out.println("Test 1 Passed: 11 parameterized reference range test cases passed.");
    }

    public static void testLabOrderBuilderDefaults() {
        LabOrder order = new LabOrderBuilder().build();
        assert order.getPatientId().equals("PAT-DEFAULT") : "Default patient id mismatch";
        assert order.getPriority().equals("ROUTINE") : "Default priority must be ROUTINE";
        assert order.getTests().size() == 2 : "Default tests should contain 2 panels";
        System.out.println("Test 2 Passed: LabOrderBuilder defaults verified.");
    }

    public static void testLabOrderBuilderCustomStat() {
        LabOrder order = new LabOrderBuilder()
            .withPatientId("PAT-999")
            .asStatPriority()
            .withTests(List.of("TROPONIN", "D-DIMER", "LACTATE"))
            .build();

        assert order.getPatientId().equals("PAT-999") : "Patient ID override mismatch";
        assert order.getPriority().equals("STAT") : "Priority must be STAT";
        assert order.getTests().contains("TROPONIN") : "Must contain TROPONIN";
        assert order.getTests().size() == 3 : "Must contain 3 tests";
        System.out.println("Test 3 Passed: Custom STAT order builder verified.");
    }

    public static void main(String[] args) {
        testReferenceRangeParameterizedMatrix();
        testLabOrderBuilderDefaults();
        testLabOrderBuilderCustomStat();
        System.out.println("All Unit 5.3 Exercise Solutions Passed Successfully!");
    }
}
