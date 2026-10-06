import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.UUID;

public class StarterCode {

    public record StudyRecord(
        UUID studyId,
        String patientMrn,
        String modality,
        BigDecimal radiationDoseMsv,
        String scannerMetadataJson,
        Instant createdAt
    ) {}

    public static class RadiologyStudyRepository {
        private final String jdbcUrl;

        public RadiologyStudyRepository(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public void initSchema() throws SQLException {
            // TODO: Create radiology_studies table
        }

        public UUID recordStudy(StudyRecord study) throws SQLException {
            // TODO: Insert study using PreparedStatement with typed bindings
            return null;
        }

        public BigDecimal getCumulativeDose(String patientMrn) throws SQLException {
            // TODO: Query sum of radiation_dose_msv using COALESCE
            return BigDecimal.ZERO;
        }
    }
}
