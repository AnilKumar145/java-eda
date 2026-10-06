import java.sql.*;
import java.util.*;
import java.util.zip.CRC32;

public class StarterCode {

    public record MigrationScript(
        String version,
        String description,
        String scriptName,
        String sqlContent
    ) {
        public long computeChecksum() {
            String norm = sqlContent.replace("\r\n", "\n").replace('\r', '\n').trim();
            CRC32 crc = new CRC32();
            crc.update(norm.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return crc.getValue();
        }
    }

    public static class FormularyMigrationEngine {

        public void initHistoryTable(Connection conn) throws SQLException {
            // TODO: Execute CREATE TABLE IF NOT EXISTS for pharmacy_schema_history
        }

        public void validateHistoricalScripts(Connection conn, List<MigrationScript> scripts) throws SQLException {
            // TODO: Query pharmacy_schema_history and throw IllegalStateException if any applied script has altered checksum
        }

        public List<String> migrate(Connection conn, List<MigrationScript> scripts) throws SQLException {
            // TODO: Initialize table, validate history, and execute pending scripts transactionally
            return Collections.emptyList();
        }
    }
}
