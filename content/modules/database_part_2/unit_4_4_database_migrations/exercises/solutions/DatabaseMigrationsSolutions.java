import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * Unit 4.4 Solutions: Database Migrations
 * Complete reference implementation for Unit 4.4 exercises.
 */
public class DatabaseMigrationsSolutions {

    public record MigrationDescriptor(
        String prefix,
        String version,
        String description,
        String filename
    ) {}

    private static final Pattern FLYWAY_PATTERN = Pattern.compile("^(V|U|R)([0-9_]+)__(.+)\\.sql$");

    public static MigrationDescriptor parseMigrationFilename(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("filename cannot be null");
        }
        Matcher matcher = FLYWAY_PATTERN.matcher(filename);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid migration filename format: " + filename);
        }
        String prefix = matcher.group(1);
        String rawVersion = matcher.group(2);
        String rawDescription = matcher.group(3);

        String version = rawVersion.replace('_', '.');
        String description = rawDescription.replace('_', ' ');

        return new MigrationDescriptor(prefix, version, description, filename);
    }

    public static long computeChecksum(String sqlContent) {
        if (sqlContent == null) return 0L;
        String normalized = sqlContent.replace("\r\n", "\n").replace('\r', '\n').trim();
        CRC32 crc = new CRC32();
        crc.update(normalized.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return crc.getValue();
    }

    public static List<String> sortMigrationsByVersion(List<String> filenames) {
        if (filenames == null) return Collections.emptyList();

        List<String> sorted = new ArrayList<>(filenames);
        sorted.sort((f1, f2) -> {
            MigrationDescriptor d1 = parseMigrationFilename(f1);
            MigrationDescriptor d2 = parseMigrationFilename(f2);
            return compareVersions(d1.version(), d2.version());
        });
        return sorted;
    }

    private static int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int maxLen = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < maxLen; i++) {
            int num1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
            int num2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }

    public static List<String> detectTamperedMigrations(Map<String, Long> recordedChecksums,
                                                        Map<String, String> currentContents) {
        if (recordedChecksums == null || currentContents == null) {
            return Collections.emptyList();
        }

        List<String> tampered = new ArrayList<>();
        for (Map.Entry<String, Long> entry : recordedChecksums.entrySet()) {
            String version = entry.getKey();
            Long expectedChecksum = entry.getValue();

            String content = currentContents.get(version);
            if (content != null) {
                long actualChecksum = computeChecksum(content);
                if (actualChecksum != expectedChecksum) {
                    tampered.add(version);
                }
            }
        }
        Collections.sort(tampered);
        return tampered;
    }

    public static void main(String[] args) {
        // Test 1: Parse filename
        MigrationDescriptor desc = parseMigrationFilename("V1_2__create_drug_formulary.sql");
        assert desc != null : "Descriptor should not be null";
        assert "V".equals(desc.prefix()) : "Prefix should be V";
        assert "1.2".equals(desc.version()) : "Version should be 1.2";
        assert "create drug formulary".equals(desc.description()) : "Description mismatch";
        System.out.println("Exercise 1 passed: Migration filename parsed successfully.");

        // Test 2: Checksum computation
        String sql = "CREATE TABLE drugs (id BIGINT PRIMARY KEY);\r\n";
        long c1 = computeChecksum(sql);
        long c2 = computeChecksum("CREATE TABLE drugs (id BIGINT PRIMARY KEY);\n");
        assert c1 == c2 && c1 != 0 : "Checksums must match across normalized line breaks";
        System.out.println("Exercise 2 passed: Script CRC32 checksum computed.");

        // Test 3: Version sorting
        List<String> unsorted = List.of("V10_0__reports.sql", "V1_0__init.sql", "V2_0__users.sql", "V1_1__add_col.sql");
        List<String> sorted = sortMigrationsByVersion(unsorted);
        assert "V1_0__init.sql".equals(sorted.get(0)) : "First should be V1_0";
        assert "V1_1__add_col.sql".equals(sorted.get(1)) : "Second should be V1_1";
        assert "V2_0__users.sql".equals(sorted.get(2)) : "Third should be V2_0";
        assert "V10_0__reports.sql".equals(sorted.get(3)) : "Fourth should be V10_0";
        System.out.println("Exercise 3 passed: Migrations sorted by numerical version.");

        // Test 4: Tamper detection
        Map<String, Long> recorded = Map.of(
            "1.0", computeChecksum("CREATE TABLE a (id INT);"),
            "1.1", computeChecksum("CREATE TABLE b (id INT);")
        );
        Map<String, String> current = Map.of(
            "1.0", "CREATE TABLE a (id INT);",
            "1.1", "CREATE TABLE b (id INT, tampered BOOLEAN);"
        );
        List<String> tampered = detectTamperedMigrations(recorded, current);
        assert tampered.size() == 1 && tampered.contains("1.1") : "Failed to detect tampered version 1.1";
        System.out.println("Exercise 4 passed: Tampered migration detected.");

        System.out.println("All Unit 4.4 Exercise Tests Passed!");
    }
}
