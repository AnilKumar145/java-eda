import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.Instant;
import java.util.UUID;

public class Solution {

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
            String sql = """
                CREATE TABLE IF NOT EXISTS radiology_studies (
                    study_id UUID PRIMARY KEY,
                    patient_mrn VARCHAR(64) NOT NULL,
                    modality VARCHAR(16) NOT NULL,
                    radiation_dose_msv NUMERIC(6, 2),
                    scanner_metadata_json TEXT NOT NULL,
                    created_at TIMESTAMP NOT NULL
                );
                CREATE INDEX IF NOT EXISTS idx_radiology_mrn ON radiology_studies(patient_mrn);
                """;

            try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        }

        public UUID recordStudy(StudyRecord study) throws SQLException {
            String sql = """
                INSERT INTO radiology_studies (study_id, patient_mrn, modality, radiation_dose_msv, scanner_metadata_json, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

            try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setObject(1, study.studyId());
                pstmt.setString(2, study.patientMrn());
                pstmt.setString(3, study.modality());

                if (study.radiationDoseMsv() != null) {
                    pstmt.setBigDecimal(4, study.radiationDoseMsv());
                } else {
                    pstmt.setNull(4, Types.NUMERIC);
                }

                pstmt.setString(5, study.scannerMetadataJson());
                pstmt.setTimestamp(6, Timestamp.from(study.createdAt()));

                pstmt.executeUpdate();
                return study.studyId();
            }
        }

        public BigDecimal getCumulativeDose(String patientMrn) throws SQLException {
            String sql = """
                SELECT COALESCE(SUM(radiation_dose_msv), 0.00) AS total_dose
                FROM radiology_studies
                WHERE patient_mrn = ?
                """;

            try (Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, patientMrn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal total = rs.getBigDecimal("total_dose");
                        return total != null ? total.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                    }
                }
            }
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
