import java.time.Instant;
import java.util.*;

/**
 * Unit 4.5 Solutions: Database Logging and Diagnostics
 * Complete reference implementation for Unit 4.5 exercises.
 */
public class DatabaseLoggingSolutions {

    public static String formatSqlWithParams(String sqlTemplate, List<Object> params) {
        if (sqlTemplate == null) return null;
        if (params == null || params.isEmpty()) return sqlTemplate;

        StringBuilder sb = new StringBuilder();
        int paramIndex = 0;
        int len = sqlTemplate.length();

        for (int i = 0; i < len; i++) {
            char c = sqlTemplate.charAt(i);
            if (c == '?' && paramIndex < params.size()) {
                Object p = params.get(paramIndex++);
                if (p == null) {
                    sb.append("NULL");
                } else if (p instanceof String || p instanceof Enum<?>) {
                    sb.append("'").append(p).append("'");
                } else {
                    sb.append(p);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public record SlowQueryAlert(
        String sql,
        long durationMs,
        long thresholdMs,
        String severity
    ) {}

    public static SlowQueryAlert checkSlowQuery(String sql, long durationMs, long thresholdMs) {
        if (durationMs <= thresholdMs) {
            return null;
        }

        String severity;
        if (durationMs >= thresholdMs * 3) {
            severity = "CRITICAL";
        } else {
            severity = "WARNING";
        }

        return new SlowQueryAlert(sql, durationMs, thresholdMs, severity);
    }

    public enum PoolHealthStatus {
        HEALTHY,
        WARNING_EXHAUSTED,
        CRITICAL_STARVATION
    }

    public static PoolHealthStatus evaluatePoolHealth(int active, int idle, int awaitingThreads, int maxPoolSize) {
        if (awaitingThreads > 0) {
            return PoolHealthStatus.CRITICAL_STARVATION;
        }
        if (active >= maxPoolSize) {
            return PoolHealthStatus.WARNING_EXHAUSTED;
        }
        return PoolHealthStatus.HEALTHY;
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

    public static void stampAudit(AuditableRecord record, String username, Instant timestamp, boolean isUpdate) {
        if (record == null) return;

        if (!isUpdate) {
            record.setCreatedBy(username);
            record.setCreatedAt(timestamp);
            record.setUpdatedBy(username);
            record.setUpdatedAt(timestamp);
            record.setVersion(1L);
        } else {
            record.setUpdatedBy(username);
            record.setUpdatedAt(timestamp);
            record.setVersion(record.getVersion() + 1L);
        }
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
