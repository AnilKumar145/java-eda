import java.util.*;

public class TestCoverageAndBestPracticesSolutions {

    public static class TriageClassifier {
        public static String classifyTriageAcuity(int systolicBp, int heartRate, int oxygenSat) {
            if (systolicBp < 20 || systolicBp > 300) {
                throw new IllegalArgumentException("Invalid systolic blood pressure: " + systolicBp);
            }
            if (heartRate < 20 || heartRate > 300) {
                throw new IllegalArgumentException("Invalid heart rate: " + heartRate);
            }
            if (oxygenSat < 50 || oxygenSat > 100) {
                throw new IllegalArgumentException("Invalid oxygen saturation: " + oxygenSat);
            }

            if (systolicBp < 90 || heartRate > 130 || oxygenSat < 90) {
                return "RED_RESUSCITATION";
            } else if (systolicBp < 100 || heartRate > 110 || oxygenSat < 94) {
                return "ORANGE_EMERGENT";
            } else if (systolicBp > 180 || heartRate > 100) {
                return "YELLOW_URGENT";
            } else {
                return "GREEN_NON_URGENT";
            }
        }
    }

    public record AuditEntry(String userId, String action, String patientId) {}

    public static class AuditLogTrail {
        private final List<AuditEntry> entries = new ArrayList<>();

        public AuditEntry recordAction(String userId, String action, String patientId) {
            if (userId == null || userId.isBlank() || action == null || action.isBlank() || patientId == null || patientId.isBlank()) {
                throw new IllegalArgumentException("All fields are required and must be non-empty.");
            }
            AuditEntry entry = new AuditEntry(userId.trim(), action.trim().toUpperCase(), patientId.trim());
            entries.add(entry);
            return entry;
        }

        public List<AuditEntry> getActionsForPatient(String patientId) {
            String target = patientId.trim();
            List<AuditEntry> matched = new ArrayList<>();
            for (AuditEntry e : entries) {
                if (e.patientId().equals(target)) {
                    matched.add(e);
                }
            }
            return matched;
        }

        public boolean hasUnauthorizedAccess(String patientId, Set<String> authorizedUsers) {
            List<AuditEntry> patientEntries = getActionsForPatient(patientId);
            for (AuditEntry e : patientEntries) {
                if (!authorizedUsers.contains(e.userId())) {
                    return true;
                }
            }
            return false;
        }
    }

    // ==========================================
    // 100% Branch Coverage Test Suite
    // ==========================================

    public static void testTriageInvalidVitals() {
        int[][] invalid = new int[][] {
            {10, 80, 98}, {320, 80, 98}, // BP boundaries
            {120, 15, 98}, {120, 310, 98}, // HR boundaries
            {120, 80, 45}, {120, 80, 105}  // SpO2 boundaries
        };
        for (int[] v : invalid) {
            boolean caught = false;
            try {
                TriageClassifier.classifyTriageAcuity(v[0], v[1], v[2]);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assert caught : "Should throw IllegalArgumentException for vitals: " + Arrays.toString(v);
        }
        System.out.println("Test 1 Passed: 6 physiological boundary violations rejected.");
    }

    public static void testTriageRedBranches() {
        assert "RED_RESUSCITATION".equals(TriageClassifier.classifyTriageAcuity(85, 80, 98)) : "Low BP branch";
        assert "RED_RESUSCITATION".equals(TriageClassifier.classifyTriageAcuity(120, 135, 98)) : "High HR branch";
        assert "RED_RESUSCITATION".equals(TriageClassifier.classifyTriageAcuity(120, 80, 88)) : "Low SpO2 branch";
        System.out.println("Test 2 Passed: 3 RED compound branches evaluated.");
    }

    public static void testTriageOrangeBranches() {
        assert "ORANGE_EMERGENT".equals(TriageClassifier.classifyTriageAcuity(95, 80, 98)) : "Borderline BP branch";
        assert "ORANGE_EMERGENT".equals(TriageClassifier.classifyTriageAcuity(120, 115, 98)) : "Borderline HR branch";
        assert "ORANGE_EMERGENT".equals(TriageClassifier.classifyTriageAcuity(120, 80, 92)) : "Borderline SpO2 branch";
        System.out.println("Test 3 Passed: 3 ORANGE compound branches evaluated.");
    }

    public static void testTriageYellowAndGreenBranches() {
        assert "YELLOW_URGENT".equals(TriageClassifier.classifyTriageAcuity(190, 80, 98)) : "Hypertensive BP branch";
        assert "YELLOW_URGENT".equals(TriageClassifier.classifyTriageAcuity(120, 105, 98)) : "Elevated HR branch";
        assert "GREEN_NON_URGENT".equals(TriageClassifier.classifyTriageAcuity(120, 75, 98)) : "Green normal vitals branch";
        System.out.println("Test 4 Passed: YELLOW and GREEN branches evaluated.");
    }

    public static void testAuditLogTrailOperations() {
        AuditLogTrail audit = new AuditLogTrail();
        audit.recordAction("dr_smith", "view_chart", "P-100");
        audit.recordAction("nurse_joy", "administer_med", "P-100");
        audit.recordAction("dr_smith", "view_chart", "P-200");

        List<AuditEntry> p100 = audit.getActionsForPatient("P-100");
        assert p100.size() == 2 : "Should have 2 entries for P-100";
        assert "VIEW_CHART".equals(p100.get(0).action()) : "Action normalized uppercase";

        // Authorized access check
        Set<String> auth = Set.of("dr_smith", "nurse_joy");
        assert !audit.hasUnauthorizedAccess("P-100", auth) : "All users authorized";

        // Unauthorized access check
        audit.recordAction("intruder_x", "export_records", "P-100");
        assert audit.hasUnauthorizedAccess("P-100", auth) : "Intruder must be flagged";

        // Empty patient check
        assert !audit.hasUnauthorizedAccess("P-999", auth) : "Nonexistent patient has no breaches";
        System.out.println("Test 5 Passed: AuditLogTrail complete branch operations verified.");
    }

    public static void testAuditLogInputValidation() {
        AuditLogTrail audit = new AuditLogTrail();
        String[][] badInputs = new String[][] {
            {"", "ACTION", "P-1"},
            {"USER", "", "P-1"},
            {"USER", "ACTION", ""}
        };
        for (String[] input : badInputs) {
            boolean caught = false;
            try {
                audit.recordAction(input[0], input[1], input[2]);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assert caught : "Should reject empty fields: " + Arrays.toString(input);
        }
        System.out.println("Test 6 Passed: Audit log empty field rejections verified.");
    }

    public static void main(String[] args) {
        testTriageInvalidVitals();
        testTriageRedBranches();
        testTriageOrangeBranches();
        testTriageYellowAndGreenBranches();
        testAuditLogTrailOperations();
        testAuditLogInputValidation();
        System.out.println("All Unit 5.7 Exercise Solutions Passed Successfully!");
    }
}
