import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bedside Vitals Telemetry Poller - Starter Code
 */
public class StarterCode {

    public record MonitorConfig(String deviceId, long latencyMs, Object telemetryValue) {}

    public static class TelemetryIngestionEngine {
        /**
         * Poll all configured bedside monitors concurrently.
         *
         * @param configs List of MonitorConfig objects
         * @return Map of deviceId to telemetry value
         */
        public Map<String, Object> pollMonitors(List<MonitorConfig> configs) throws InterruptedException {
            // TODO: Implement concurrent polling across worker threads
            return new ConcurrentHashMap<>();
        }
    }
}
