import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class LabTests {

    public static final String TEST_DB_URL = "jdbc:h2:mem:radiology_lab_db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";

    public static void main(String[] args) throws SQLException {
        testRadiologyStudyStoreAndDosimetry();
        System.out.println("All Unit 3.3 Lab 1 Tests Passed Successfully!");
    }

    public static void testRadiologyStudyStoreAndDosimetry() throws SQLException {
        Solution.RadiologyStudyRepository repo = new Solution.RadiologyStudyRepository(TEST_DB_URL);
        repo.initSchema();

        UUID ctStudyId = UUID.randomUUID();
        Solution.StudyRecord ctStudy = new Solution.StudyRecord(
            ctStudyId,
            "PATIENT-RAD-1",
            "CT",
            new BigDecimal("3.45"),
            "{\"kvp\": 120, \"slice_thickness_mm\": 1.25}",
            Instant.now()
        );

        UUID mriStudyId = UUID.randomUUID();
        Solution.StudyRecord mriStudy = new Solution.StudyRecord(
            mriStudyId,
            "PATIENT-RAD-1",
            "MRI",
            null, // MRI has no ionizing radiation dose!
            "{\"field_strength_tesla\": 3.0, \"sequence\": \"T2_FLAIR\"}",
            Instant.now()
        );

        UUID petCtStudyId = UUID.randomUUID();
        Solution.StudyRecord petCtStudy = new Solution.StudyRecord(
            petCtStudyId,
            "PATIENT-RAD-1",
            "PET-CT",
            new BigDecimal("5.10"),
            "{\"tracer\": \"18F-FDG\", \"injected_dose_mbq\": 250}",
            Instant.now()
        );

        UUID insertedCt = repo.recordStudy(ctStudy);
        assert ctStudyId.equals(insertedCt) : "Inserted UUID mismatch";

        UUID insertedMri = repo.recordStudy(mriStudy);
        assert mriStudyId.equals(insertedMri) : "MRI inserted UUID mismatch";

        UUID insertedPet = repo.recordStudy(petCtStudy);
        assert petCtStudyId.equals(insertedPet) : "PET inserted UUID mismatch";

        System.out.println("Lab Test 1 Passed: Ingested CT, MRI (with null radiation dose), and PET-CT studies.");

        // Verify cumulative dosage calculation: 3.45 + null + 5.10 = 8.55
        BigDecimal totalDose = repo.getCumulativeDose("PATIENT-RAD-1");
        assert new BigDecimal("8.55").compareTo(totalDose) == 0 : "Expected 8.55 mSv cumulative dose, got " + totalDose;
        System.out.println("Lab Test 2 Passed: Cumulative radiation dose correctly computed with NULL safety: " + totalDose + " mSv.");

        // Verify patient with zero scans returns 0.00
        BigDecimal zeroDose = repo.getCumulativeDose("PATIENT-UNKNOWN");
        assert new BigDecimal("0.00").compareTo(zeroDose) == 0 : "Expected 0.00 for unknown patient, got " + zeroDose;
        System.out.println("Lab Test 3 Passed: COALESCE properly returned 0.00 for patient with no scans.");
    }
}
