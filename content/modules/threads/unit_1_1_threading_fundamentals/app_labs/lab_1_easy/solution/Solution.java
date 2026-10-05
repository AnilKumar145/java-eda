import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bedside Vitals Telemetry Poller - Solution
 */
public class Solution {

    public record MonitorConfig(String deviceId, long latencyMs, Object telemetryValue) {}

    public static class TelemetryIngestionEngine {
        public Map<String, Object> pollMonitors(List<MonitorConfig> configs) throws InterruptedException {
            Map<String, Object> results = new ConcurrentHashMap<>();
            List<Thread> threads = new ArrayList<>();

            for (MonitorConfig config : configs) {
                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(config.latencyMs());
                        results.put(config.deviceId(), config.telemetryValue());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, "poller-" + config.deviceId());

                threads.add(t);
                t.start();
            }

            for (Thread t : threads) {
                t.join();
            }

            return results;
        }
    }
}
