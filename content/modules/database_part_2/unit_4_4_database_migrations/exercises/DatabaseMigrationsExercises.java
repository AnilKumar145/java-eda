import java.util.*;
import java.util.zip.CRC32;

/**
 * Unit 4.4 Exercises: Database Migrations
 * Implement each method according to its documentation to pass all assertion tests.
 */
public class DatabaseMigrationsExercises {

    public record MigrationDescriptor(
        String prefix,
        String version,
        String description,
        String filename
    ) {}

    /**
     * Exercise 1: Parse Flyway Migration Filename
     * Parse filenames of the pattern: V{version}__{description}.sql
     * Example: "V2_1__create_drug_formulary.sql"
     * -> prefix="V", version="2.1", description="create drug formulary", filename="V2_1__create_drug_formulary.sql"
     * Throws IllegalArgumentException if pattern does not match.
     */
    public static MigrationDescriptor parseMigrationFilename(String filename) {
        // TODO: Parse prefix, version, and description from filename.
        return null;
    }

    /**
     * Exercise 2: Compute Script Checksum
     * Compute and return a 32-bit CRC checksum (using java.util.zip.CRC32)
     * of the SQL script content after normalizing line endings (\r\n -> \n) and trimming whitespace.
     */
    public static long computeChecksum(String sqlContent) {
        // TODO: Normalize content and compute CRC32 checksum.
        return 0L;
    }

    /**
     * Exercise 3: Sort Migration Filenames in Numerical Version Order
     * Given a list of Flyway filenames like ["V10_0__reports.sql", "V1_0__init.sql", "V2_0__users.sql"],
     * sort them in ascending numerical order: ["V1_0__init.sql", "V2_0__users.sql", "V10_0__reports.sql"].
     */
    public static List<String> sortMigrationsByVersion(List<String> filenames) {
        // TODO: Sort filenames according to semantic numerical version.
        return Collections.emptyList();
    }

    /**
     * Exercise 4: Detect Altered Historical Migrations
     * Given recorded checksums in the schema history table (key: version, val: checksum)
     * and current script contents (key: version, val: sqlContent),
     * return a list of versions whose computed checksum differs from the recorded checksum.
     */
    public static List<String> detectTamperedMigrations(Map<String, Long> recordedChecksums,
                                                        Map<String, String> currentContents) {
        // TODO: Compare checksums and return list of tampered versions.
        return Collections.emptyList();
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
