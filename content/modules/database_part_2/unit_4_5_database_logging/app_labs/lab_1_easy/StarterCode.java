import java.util.*;

public class StarterCode {

    public enum LatencyClassification {
        FAST,
        NORMAL,
        SLOW,
        CRITICAL
    }

    public record ExecutionLogEntry(
        String sqlFingerprint,
        double durationMs,
        LatencyClassification classification
    ) {}

    public static class StatementMetrics {
        private int count = 0;
        private double totalDurationMs = 0.0;
        private double minDurationMs = Double.MAX_VALUE;
        private double maxDurationMs = 0.0;

        public void record(double durationMs) {
            // TODO: Update metrics
        }

        public int getCount() { return count; }
        public double getTotalDurationMs() { return totalDurationMs; }
        public double getMinDurationMs() { return minDurationMs; }
        public double getMaxDurationMs() { return maxDurationMs; }
        public double getAverageDurationMs() {
            return count == 0 ? 0.0 : totalDurationMs / count;
        }
    }

    public static class IcuQueryProfiler {
        private final Map<String, StatementMetrics> metricsMap = new HashMap<>();
        private final List<ExecutionLogEntry> executionLog = new ArrayList<>();

        public ExecutionLogEntry profileExecution(String sqlFingerprint, long durationNanos) {
            // TODO: Convert to ms, classify, record in metrics, and log
            return null;
        }

        public StatementMetrics getMetrics(String sqlFingerprint) {
            return metricsMap.get(sqlFingerprint);
        }

        public List<ExecutionLogEntry> getSlowQueryAlerts(double thresholdMs) {
            // TODO: Return log entries exceeding thresholdMs sorted slowest first
            return Collections.emptyList();
        }
    }
}
