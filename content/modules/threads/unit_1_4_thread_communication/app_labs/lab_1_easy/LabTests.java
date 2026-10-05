/**
 * Verification Tests for Lab 1 Easy: Hospital ER Patient Triage Dispatcher (Java)
 */
public class LabTests {

    public static void main(String[] args) throws Exception {
        testPriorityOrderSingleDoctor();
        testMultiDoctorPool();
        System.out.println("All Lab 1 Easy Tests Passed!");
    }

    public static void testPriorityOrderSingleDoctor() throws Exception {
        Solution.ERTriageDispatcher dispatcher = new Solution.ERTriageDispatcher();

        // Admit Level 4 first, then Level 1, then Level 2
        dispatcher.admitPatient(new Solution.TriageCase(4, "P-401", "Minor Abrasion"));
        dispatcher.admitPatient(new Solution.TriageCase(1, "P-101", "Massive Trauma"));
        dispatcher.admitPatient(new Solution.TriageCase(2, "P-201", "Severe Asthma"));

        // Start 1 doctor to process all 3 in strict priority order
        dispatcher.startDoctors(1);
        dispatcher.drainAndShutdown();

        assert dispatcher.treatedLog.size() == 3 : "Expected 3 treated patients, got " + dispatcher.treatedLog.size();
        assert "P-101-Acuity1".equals(dispatcher.treatedLog.get(0)) : "First treated must be Acuity 1, got " + dispatcher.treatedLog.get(0);
        assert "P-201-Acuity2".equals(dispatcher.treatedLog.get(1)) : "Second treated must be Acuity 2, got " + dispatcher.treatedLog.get(1);
        assert "P-401-Acuity4".equals(dispatcher.treatedLog.get(2)) : "Third treated must be Acuity 4, got " + dispatcher.treatedLog.get(2);
        System.out.println("Test 1 passed! Priority dispatching verified (Level 1 treated first).");
    }

    public static void testMultiDoctorPool() throws Exception {
        Solution.ERTriageDispatcher dispatcher = new Solution.ERTriageDispatcher();

        for (int i = 0; i < 9; i++) {
            dispatcher.admitPatient(new Solution.TriageCase((i % 5) + 1, "P-" + i, "Condition-" + i));
        }

        // Start 3 doctors concurrently
        dispatcher.startDoctors(3);
        dispatcher.drainAndShutdown();

        assert dispatcher.treatedLog.size() == 9 : "Expected 9 treated patients, got " + dispatcher.treatedLog.size();
        System.out.println("Test 2 passed! Multi-doctor pool processed all 9 patients and shut down cleanly.");
    }
}
