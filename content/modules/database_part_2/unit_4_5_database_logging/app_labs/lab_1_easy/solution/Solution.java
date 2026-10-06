import java.util.*;

public class Solution {

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
            count++;
            totalDurationMs += durationMs;
            if (durationMs < minDurationMs) {
                minDurationMs = durationMs;
            }
            if (durationMs > maxDurationMs) {
                maxDurationMs = durationMs;
            }
        }

        public int getCount() { return count; }
        public double getTotalDurationMs() { return totalDurationMs; }
        public double getMinDurationMs() { return count == 0 ? 0.0 : minDurationMs; }
        public double getMaxDurationMs() { return maxDurationMs; }
        public double getAverageDurationMs() {
            return count == 0 ? 0.0 : totalDurationMs / count;
        }
    }

    public static class IcuQueryProfiler {
        private final Map<String, StatementMetrics> metricsMap = new HashMap<>();
        private final List<ExecutionLogEntry> executionLog = new ArrayList<>();

        public ExecutionLogEntry profileExecution(String sqlFingerprint, long durationNanos) {
            double durationMs = durationNanos / 1_000_000.0;

            LatencyClassification classification;
            if (durationMs < 10.0) {
                classification = LatencyClassification.FAST;
            } else if (durationMs < 50.0) {
                classification = LatencyClassification.NORMAL;
            } else if (durationMs < 200.0) {
                classification = LatencyClassification.SLOW;
            } else {
                classification = LatencyClassification.CRITICAL;
            }

            StatementMetrics metrics = metricsMap.computeIfAbsent(sqlFingerprint, k -> new StatementMetrics());
            metrics.record(durationMs);

            ExecutionLogEntry entry = new ExecutionLogEntry(sqlFingerprint, durationMs, classification);
            executionLog.add(entry);
            return entry;
        }

        public StatementMetrics getMetrics(String sqlFingerprint) {
            return metricsMap.get(sqlFingerprint);
        }

        public List<ExecutionLogEntry> getSlowQueryAlerts(double thresholdMs) {
            List<ExecutionLogEntry> slowAlerts = new ArrayList<>();
            for (ExecutionLogEntry entry : executionLog) {
                if (entry.durationMs() > thresholdMs) {
                    slowAlerts.add(entry);
                }
            }
            slowAlerts.sort((e1, e2) -> Double.compare(e2.durationMs(), e1.durationMs()));
            return slowAlerts;
        }
    }
}
