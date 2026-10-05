import java.util.List;
import java.util.Map;

/**
 * Verification Tests for Lab 1 Easy: Bedside Vitals Telemetry Poller (Java)
 */
public class LabTests {

    public static void main(String[] args) throws Exception {
        testConcurrentSpeedupAndAccuracy();
        System.out.println("All Lab 1 Easy Tests Passed!");
    }

    public static void testConcurrentSpeedupAndAccuracy() throws Exception {
        // We test against Solution's engine
        Solution.TelemetryIngestionEngine engine = new Solution.TelemetryIngestionEngine();

        List<Solution.MonitorConfig> configs = List.of(
            new Solution.MonitorConfig("ECG-Lead-II", 50, 75),
            new Solution.MonitorConfig("SPO2-Sensor", 80, 98.5),
            new Solution.MonitorConfig("NIBP-Cuff", 60, "120/80")
        );

        long t0 = System.currentTimeMillis();
        Map<String, Object> results = engine.pollMonitors(configs);
        long elapsed = System.currentTimeMillis() - t0;

        assert results.size() == 3 : "Expected 3 device records, got " + results.size();
        assert Integer.valueOf(75).equals(results.get("ECG-Lead-II")) : "ECG reading mismatch";
        assert Double.valueOf(98.5).equals(results.get("SPO2-Sensor")) : "SPO2 reading mismatch";
        assert "120/80".equals(results.get("NIBP-Cuff")) : "Blood pressure mismatch";

        // Sequential sum is 50+80+60 = 190ms. Concurrent should be ~80-140ms.
        assert elapsed < 160 : "Polling was not concurrent; took " + elapsed + "ms";

        System.out.println("Test 1 passed! Polled 3 bedside devices concurrently in " + elapsed + "ms.");
    }
}
