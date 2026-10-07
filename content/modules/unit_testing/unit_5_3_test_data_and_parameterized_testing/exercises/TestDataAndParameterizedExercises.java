import java.util.*;

public class TestDataAndParameterizedExercises {

    public record ReferenceRange(double lowerLimit, double upperLimit) {}

    public static class ClinicalReferenceRangeClassifier {

        private static final Map<String, ReferenceRange> RANGES = Map.of(
            "GLUCOSE", new ReferenceRange(70.0, 99.0),
            "POTASSIUM", new ReferenceRange(3.5, 5.0),
            "SODIUM", new ReferenceRange(135.0, 145.0)
        );

        public String classify(String analyte, double value) {
            // TODO: Look up analyte in RANGES (case-insensitive). If missing, throw IllegalArgumentException.
            // If value < lowerLimit -> "LOW"
            // If value > upperLimit -> "HIGH"
            // Otherwise -> "NORMAL"
            throw new UnsupportedOperationException("TODO: Implement classify");
        }
    }

    public static class LabOrder {
        private final String orderId;
        private final String patientId;
        private final String priority;
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
        // TODO: Implement Builder pattern fields and methods:
        // - default orderId, patientId="PAT-DEFAULT", priority="ROUTINE", tests=["GLUCOSE", "CBC"]
        // - withPatientId(String patientId)
        // - asStatPriority() -> sets priority="STAT"
        // - withTests(List<String> tests)
        // - build() -> returns LabOrder
        public LabOrder build() {
            throw new UnsupportedOperationException("TODO: Implement LabOrderBuilder");
        }
    }

    public static void main(String[] args) {
        System.out.println("Unit 5.3 Exercises Starter ready for implementation.");
    }
}
