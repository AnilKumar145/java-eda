import java.sql.*;
import java.util.*;
import java.util.zip.CRC32;

public class Solution {

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
            String ddl = """
                CREATE TABLE IF NOT EXISTS pharmacy_schema_history (
                    installed_rank INT AUTO_INCREMENT PRIMARY KEY,
                    version VARCHAR(50) NOT NULL UNIQUE,
                    description VARCHAR(200) NOT NULL,
                    script_name VARCHAR(150) NOT NULL,
                    checksum BIGINT NOT NULL,
                    installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    success BOOLEAN NOT NULL
                );
            """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(ddl);
            }
        }

        public void validateHistoricalScripts(Connection conn, List<MigrationScript> scripts) throws SQLException {
            Map<String, Long> history = new HashMap<>();
            String sql = "SELECT version, checksum FROM pharmacy_schema_history WHERE success = TRUE";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    history.put(rs.getString("version"), rs.getLong("checksum"));
                }
            }

            for (MigrationScript script : scripts) {
                if (history.containsKey(script.version())) {
                    long expected = history.get(script.version());
                    long actual = script.computeChecksum();
                    if (expected != actual) {
                        throw new IllegalStateException("Migration checksum mismatch for version " + script.version());
                    }
                }
            }
        }

        public List<String> migrate(Connection conn, List<MigrationScript> scripts) throws SQLException {
            initHistoryTable(conn);
            validateHistoricalScripts(conn, scripts);

            // Fetch already applied versions
            Set<String> appliedVersions = new HashSet<>();
            String checkSql = "SELECT version FROM pharmacy_schema_history WHERE success = TRUE";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkSql)) {
                while (rs.next()) {
                    appliedVersions.add(rs.getString("version"));
                }
            }

            // Sort scripts by version
            List<MigrationScript> sorted = new ArrayList<>(scripts);
            sorted.sort((s1, s2) -> compareVersions(s1.version(), s2.version()));

            List<String> newlyApplied = new ArrayList<>();
            String insertRecordSql = """
                INSERT INTO pharmacy_schema_history (version, description, script_name, checksum, success)
                VALUES (?, ?, ?, ?, TRUE);
            """;

            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                for (MigrationScript script : sorted) {
                    if (appliedVersions.contains(script.version())) {
                        continue; // Already executed
                    }

                    // Execute migration DDL/DML
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(script.sqlContent());
                    }

                    // Record history
                    try (PreparedStatement pstmt = conn.prepareStatement(insertRecordSql)) {
                        pstmt.setString(1, script.version());
                        pstmt.setString(2, script.description());
                        pstmt.setString(3, script.scriptName());
                        pstmt.setLong(4, script.computeChecksum());
                        pstmt.executeUpdate();
                    }

                    newlyApplied.add(script.version());
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }

            return newlyApplied;
        }

        private int compareVersions(String v1, String v2) {
            String[] p1 = v1.split("\\.");
            String[] p2 = v2.split("\\.");
            int max = Math.max(p1.length, p2.length);
            for (int i = 0; i < max; i++) {
                int n1 = (i < p1.length) ? Integer.parseInt(p1[i]) : 0;
                int n2 = (i < p2.length) ? Integer.parseInt(p2[i]) : 0;
                if (n1 != n2) return Integer.compare(n1, n2);
            }
            return 0;
        }
    }
}
