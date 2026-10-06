import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StarterCode {

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
            // TODO: Create patient_vitals table
        }

        public int ingestTelemetryBatch(List<TelemetryRecord> records) throws SQLException {
            // TODO: Use PreparedStatement batch execution inside transaction to insert records
            return 0;
        }

        public List<TelemetryRecord> queryVitalsByTimeRange(String mrn, long startMs, long endMs) throws SQLException {
            // TODO: Query records for mrn between startMs and endMs ordered by recorded_epoch_ms ASC
            return Collections.emptyList();
        }
    }
}
