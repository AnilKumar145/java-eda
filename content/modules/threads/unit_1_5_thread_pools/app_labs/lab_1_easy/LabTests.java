import java.util.List;

/**
 * Verification Tests for Lab 1 Easy: Multi-Service Clinical Diagnostics Aggregator (Java)
 */
public class LabTests {

    public static void main(String[] args) throws Exception {
        testConcurrentSpeedupAllHealthy();
        testTimeoutDegradation();
        System.out.println("All Lab 1 Easy Tests Passed!");
    }

    public static void testConcurrentSpeedupAllHealthy() throws Exception {
        Solution.DiagnosticAggregator aggregator = new Solution.DiagnosticAggregator();

        List<Solution.ServiceQuery> queries = List.of(
            new Solution.ServiceQuery("EHR", () -> { Thread.sleep(40); return "Allergies: Penicillin"; }),
            new Solution.ServiceQuery("Pathology", () -> { Thread.sleep(60); return "Glucose: 95 mg/dL"; }),
            new Solution.ServiceQuery("Telemetry", () -> { Thread.sleep(50); return "HR: 72 bpm"; })
        );

        long t0 = System.currentTimeMillis();
        List<Solution.ServiceResult> results = aggregator.aggregateProfile(queries, 200);
        long elapsed = System.currentTimeMillis() - t0;

        assert results.size() == 3 : "Expected 3 service results";
        for (Solution.ServiceResult r : results) {
            assert "OK".equals(r.status()) : "Expected OK status for " + r.serviceName();
            assert r.data() != null : "Expected non-null data for " + r.serviceName();
        }

        // Sequential = 40+60+50 = 150ms. Concurrent should be ~60-110ms.
        assert elapsed < 140 : "Execution was not concurrent; took " + elapsed + "ms";
        System.out.println("Test 1 passed! Aggregated 3 services concurrently in " + elapsed + "ms.");
    }

    public static void testTimeoutDegradation() throws Exception {
        Solution.DiagnosticAggregator aggregator = new Solution.DiagnosticAggregator();

        List<Solution.ServiceQuery> queries = List.of(
            new Solution.ServiceQuery("FastService", () -> { Thread.sleep(20); return "FastData"; }),
            new Solution.ServiceQuery("StalledService", () -> { Thread.sleep(500); return "StalledData"; })
        );

        List<Solution.ServiceResult> results = aggregator.aggregateProfile(queries, 80);
        assert results.size() == 2;

        Solution.ServiceResult fast = results.stream().filter(r -> "FastService".equals(r.serviceName())).findFirst().orElseThrow();
        Solution.ServiceResult stalled = results.stream().filter(r -> "StalledService".equals(r.serviceName())).findFirst().orElseThrow();

        assert "OK".equals(fast.status()) && "FastData".equals(fast.data());
        assert "TIMEOUT".equals(stalled.status()) && stalled.data() == null;
        System.out.println("Test 2 passed! Stalled service timed out cleanly while healthy service succeeded.");
    }
}
