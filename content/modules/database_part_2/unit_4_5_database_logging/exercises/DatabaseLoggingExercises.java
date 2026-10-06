import java.time.Instant;
import java.util.*;

/**
 * Unit 4.5 Exercises: Database Logging and Diagnostics
 * Implement each method according to its documentation to pass all assertion tests.
 */
public class DatabaseLoggingExercises {

    /**
     * Exercise 1: Interpolate SQL Bound Parameters for Logging
     * Replace '?' placeholders in sqlTemplate with parameters from params in sequential order.
     * Strings should be enclosed in single quotes, null should be printed as 'NULL', numbers as raw.
     */
    public static String formatSqlWithParams(String sqlTemplate, List<Object> params) {
        // TODO: Substitute '?' placeholders with formatted parameter values.
        return null;
    }

    public record SlowQueryAlert(
        String sql,
        long durationMs,
        long thresholdMs,
        String severity
    ) {}

    /**
     * Exercise 2: Slow Query Watchdog & Threshold Alert
     * If durationMs exceeds thresholdMs, create and return a SlowQueryAlert:
     * - If durationMs >= thresholdMs * 3: severity is "CRITICAL"
     * - Else: severity is "WARNING"
     * If durationMs does not exceed thresholdMs, return null.
     */
    public static SlowQueryAlert checkSlowQuery(String sql, long durationMs, long thresholdMs) {
        // TODO: Check duration against threshold and return alert or null.
        return null;
    }

    public enum PoolHealthStatus {
        HEALTHY,
        WARNING_EXHAUSTED,
        CRITICAL_STARVATION
    }

    /**
     * Exercise 3: Connection Pool Telemetry Evaluator
     * Given active connections, idle connections, waiting threads, and maximum pool size:
     * - If awaitingThreads > 0: return CRITICAL_STARVATION
     * - Else if active >= maxPoolSize: return WARNING_EXHAUSTED
     * - Else: return HEALTHY
     */
    public static PoolHealthStatus evaluatePoolHealth(int active, int idle, int awaitingThreads, int maxPoolSize) {
        // TODO: Evaluate and return pool health status.
        return null;
    }

    public static class AuditableRecord {
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private long version = 0L;

        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
        public Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
        public long getVersion() { return version; }
        public void setVersion(long version) { this.version = version; }
    }

    /**
     * Exercise 4: Entity Audit Stamper
     * When isUpdate is false:
     * - Set createdBy and updatedBy to username.
     * - Set createdAt and updatedAt to timestamp.
     * - Set version to 1L.
     * When isUpdate is true:
     * - Retain createdBy and createdAt.
     * - Update updatedBy to username and updatedAt to timestamp.
     * - Increment version by 1.
     */
    public static void stampAudit(AuditableRecord record, String username, Instant timestamp, boolean isUpdate) {
        // TODO: Apply audit fields according to whether this is an insert or update.
    }

    public static void main(String[] args) {
        // Test 1: SQL formatting
        String template = "SELECT * FROM vitals WHERE patient_id = ? AND status = ? AND score >= ?";
        List<Object> params = List.of(101L, "STABLE", 95);
        String formatted = formatSqlWithParams(template, params);
        assert formatted.contains("patient_id = 101") : "Patient ID not formatted correctly";
        assert formatted.contains("status = 'STABLE'") : "Status string not quoted";
        assert formatted.contains("score >= 95") : "Score not formatted correctly";
        System.out.println("Exercise 1 passed: SQL parameters interpolated for logging.");

        // Test 2: Slow query watchdog
        SlowQueryAlert ok = checkSlowQuery("SELECT 1", 20, 100);
        assert ok == null : "Query under threshold should not alert";

        SlowQueryAlert warn = checkSlowQuery("SELECT * FROM large_log", 150, 100);
        assert warn != null && "WARNING".equals(warn.severity()) : "Expected WARNING alert";

        SlowQueryAlert crit = checkSlowQuery("SELECT * FROM unindexed_table", 400, 100);
        assert crit != null && "CRITICAL".equals(crit.severity()) : "Expected CRITICAL alert";
        System.out.println("Exercise 2 passed: Slow query threshold watchdog validated.");

        // Test 3: Pool health evaluation
        assert evaluatePoolHealth(5, 5, 0, 10) == PoolHealthStatus.HEALTHY : "Expected HEALTHY";
        assert evaluatePoolHealth(10, 0, 0, 10) == PoolHealthStatus.WARNING_EXHAUSTED : "Expected WARNING_EXHAUSTED";
        assert evaluatePoolHealth(10, 0, 3, 10) == PoolHealthStatus.CRITICAL_STARVATION : "Expected CRITICAL_STARVATION";
        System.out.println("Exercise 3 passed: Pool health evaluation validated.");

        // Test 4: Entity auditing
        AuditableRecord rec = new AuditableRecord();
        Instant t1 = Instant.parse("2026-10-06T10:00:00Z");
        stampAudit(rec, "dr_watson", t1, false);
        assert "dr_watson".equals(rec.getCreatedBy()) && rec.getVersion() == 1L : "Creation stamp failed";

        Instant t2 = Instant.parse("2026-10-06T11:00:00Z");
        stampAudit(rec, "dr_house", t2, true);
        assert "dr_watson".equals(rec.getCreatedBy()) : "CreatedBy should not change on update";
        assert "dr_house".equals(rec.getUpdatedBy()) : "UpdatedBy not updated";
        assert rec.getVersion() == 2L : "Version should be incremented to 2";
        System.out.println("Exercise 4 passed: Entity audit stamping validated.");

        System.out.println("All Unit 4.5 Exercise Tests Passed!");
    }
}
