import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LabTests {

    public static final String TEST_DB_URL = "jdbc:h2:mem:telemetry_lab_db;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws SQLException {
        testTelemetryPipelineBatchAndQuery();
        System.out.println("All Unit 3.2 Lab 1 Tests Passed Successfully!");
    }

    public static void testTelemetryPipelineBatchAndQuery() throws SQLException {
        Solution.ClinicTelemetryRepository repo = new Solution.ClinicTelemetryRepository(TEST_DB_URL);
        repo.initSchema();

        long now = System.currentTimeMillis();
        List<Solution.TelemetryRecord> batch = List.of(
            new Solution.TelemetryRecord("PATIENT-A", 72, 98.5, 120, 80, now - 5000),
            new Solution.TelemetryRecord("PATIENT-A", 75, 99.0, 122, 81, now - 3000),
            new Solution.TelemetryRecord("PATIENT-B", 85, 95.0, 135, 88, now - 2000),
            new Solution.TelemetryRecord("PATIENT-A", 71, 98.0, 118, 79, now - 1000)
        );

        int inserted = repo.ingestTelemetryBatch(batch);
        assert inserted == 4 : "Expected 4 records inserted, got " + inserted;
        System.out.println("Lab Test 1 Passed: Successfully ingested batch of " + inserted + " records.");

        // Query Patient A in time window
        List<Solution.TelemetryRecord> patientARecords = repo.queryVitalsByTimeRange("PATIENT-A", now - 6000, now);
        assert patientARecords.size() == 3 : "Expected 3 records for PATIENT-A, got " + patientARecords.size();
        assert patientARecords.get(0).recordedEpochMs() < patientARecords.get(1).recordedEpochMs() : "Records must be sorted chronologically";
        assert patientARecords.get(0).heartRate() == 72 : "Heart rate mismatch";
        System.out.println("Lab Test 2 Passed: Time window query returned chronological vitals series.");

        // Query non-existent window
        List<Solution.TelemetryRecord> emptyRecords = repo.queryVitalsByTimeRange("PATIENT-A", 0, 1000);
        assert emptyRecords.isEmpty() : "Expected empty list for out-of-range window";
        System.out.println("Lab Test 3 Passed: Out-of-window query returned empty result.");
    }
}
