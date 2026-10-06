import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Solution {

    public record TelemetryRecord(
        String mrn,
        int heartRate,
        double spo2,
        int systolic,
        int diastolic,
        long recordedEpochMs
    ) {}

    public static class ClinicTelemetryRepository {
        private final String jdbcUrl;

        public ClinicTelemetryRepository(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public void initSchema() throws SQLException {
            String sql = """
                CREATE TABLE IF NOT EXISTS patient_vitals (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    mrn VARCHAR(64) NOT NULL,
                    heart_rate INT NOT NULL,
                    spo2 DOUBLE NOT NULL,
                    systolic INT NOT NULL,
                    diastolic INT NOT NULL,
                    recorded_epoch_ms BIGINT NOT NULL
                );
                CREATE INDEX IF NOT EXISTS idx_vitals_mrn_time ON patient_vitals(mrn, recorded_epoch_ms);
                """;
            try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        }

        public int ingestTelemetryBatch(List<TelemetryRecord> records) throws SQLException {
            String sql = """
                INSERT INTO patient_vitals (mrn, heart_rate, spo2, systolic, diastolic, recorded_epoch_ms)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

            try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "")) {
                boolean originalAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    for (TelemetryRecord r : records) {
                        pstmt.setString(1, r.mrn());
                        pstmt.setInt(2, r.heartRate());
                        pstmt.setDouble(3, r.spo2());
                        pstmt.setInt(4, r.systolic());
                        pstmt.setInt(5, r.diastolic());
                        pstmt.setLong(6, r.recordedEpochMs());
                        pstmt.addBatch();
                    }
                    int[] batchResults = pstmt.executeBatch();
                    conn.commit();
                    int totalInserted = 0;
                    for (int count : batchResults) {
                        totalInserted += (count >= 0 ? count : 1);
                    }
                    return totalInserted;
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(originalAutoCommit);
                }
            }
        }

        public List<TelemetryRecord> queryVitalsByTimeRange(String mrn, long startMs, long endMs) throws SQLException {
            String sql = """
                SELECT mrn, heart_rate, spo2, systolic, diastolic, recorded_epoch_ms
                FROM patient_vitals
                WHERE mrn = ? AND recorded_epoch_ms BETWEEN ? AND ?
                ORDER BY recorded_epoch_ms ASC
                """;

            List<TelemetryRecord> results = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, mrn);
                pstmt.setLong(2, startMs);
                pstmt.setLong(3, endMs);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(new TelemetryRecord(
                            rs.getString("mrn"),
                            rs.getInt("heart_rate"),
                            rs.getDouble("spo2"),
                            rs.getInt("systolic"),
                            rs.getInt("diastolic"),
                            rs.getLong("recorded_epoch_ms")
                        ));
                    }
                }
            }
            return results;
        }
    }
}
