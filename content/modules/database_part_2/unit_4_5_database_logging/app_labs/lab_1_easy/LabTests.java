import java.util.*;

public class LabTests {

    public static void main(String[] args) {
        testDurationProfilingAndClassification();
        testStatementMetricsAggregation();
        testSlowQueryAlertWatchdog();
        System.out.println("All Unit 4.5 Lab 1 Tests Passed Successfully!");
    }

    public static void testDurationProfilingAndClassification() {
        Solution.IcuQueryProfiler profiler = new Solution.IcuQueryProfiler();

        Solution.ExecutionLogEntry e1 = profiler.profileExecution("SELECT * FROM beds WHERE id = ?", 5_000_000L); // 5 ms
        assert e1.classification() == Solution.LatencyClassification.FAST : "5 ms should be FAST";

        Solution.ExecutionLogEntry e2 = profiler.profileExecution("SELECT * FROM telemetry WHERE ward_id = ?", 25_000_000L); // 25 ms
        assert e2.classification() == Solution.LatencyClassification.NORMAL : "25 ms should be NORMAL";

        Solution.ExecutionLogEntry e3 = profiler.profileExecution("SELECT * FROM historical_vitals", 120_000_000L); // 120 ms
        assert e3.classification() == Solution.LatencyClassification.SLOW : "120 ms should be SLOW";

        Solution.ExecutionLogEntry e4 = profiler.profileExecution("SELECT * FROM full_audit_log", 350_000_000L); // 350 ms
        assert e4.classification() == Solution.LatencyClassification.CRITICAL : "350 ms should be CRITICAL";

        System.out.println("Lab 1 Test 1 Passed: Execution duration and latency classification validated.");
    }

    public static void testStatementMetricsAggregation() {
        Solution.IcuQueryProfiler profiler = new Solution.IcuQueryProfiler();
        String fp = "SELECT cardiac_rate FROM vitals WHERE patient_id = ?";

        profiler.profileExecution(fp, 10_000_000L); // 10 ms
        profiler.profileExecution(fp, 20_000_000L); // 20 ms
        profiler.profileExecution(fp, 30_000_000L); // 30 ms

        Solution.StatementMetrics metrics = profiler.getMetrics(fp);
        assert metrics != null : "Metrics should exist for fingerprint";
        assert metrics.getCount() == 3 : "Count should be 3";
        assert Math.abs(metrics.getMinDurationMs() - 10.0) < 0.001 : "Min duration should be 10 ms";
        assert Math.abs(metrics.getMaxDurationMs() - 30.0) < 0.001 : "Max duration should be 30 ms";
        assert Math.abs(metrics.getAverageDurationMs() - 20.0) < 0.001 : "Average duration should be 20 ms";

        System.out.println("Lab 1 Test 2 Passed: Statement metrics aggregation validated.");
    }

    public static void testSlowQueryAlertWatchdog() {
        Solution.IcuQueryProfiler profiler = new Solution.IcuQueryProfiler();

        profiler.profileExecution("QUERY_A", 15_000_000L);  // 15 ms
        profiler.profileExecution("QUERY_B", 180_000_000L); // 180 ms
        profiler.profileExecution("QUERY_C", 40_000_000L);  // 40 ms
        profiler.profileExecution("QUERY_D", 500_000_000L); // 500 ms

        List<Solution.ExecutionLogEntry> alerts = profiler.getSlowQueryAlerts(50.0);
        assert alerts.size() == 2 : "Should trigger 2 slow query alerts (> 50ms)";
        assert "QUERY_D".equals(alerts.get(0).sqlFingerprint()) : "Slowest query (QUERY_D) should be first";
        assert "QUERY_B".equals(alerts.get(1).sqlFingerprint()) : "Second slowest (QUERY_B) should be second";

        System.out.println("Lab 1 Test 3 Passed: Slow query alert watchdog filtering and ranking validated.");
    }
}
