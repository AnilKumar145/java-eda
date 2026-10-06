import java.util.*;

/**
 * Unit 3.1 Exercises: Database Fundamentals & Relational Concepts
 * Implement each method according to its documentation to pass all assertion tests.
 */
public class DatabaseFundamentalsExercises {

    /**
     * Exercise 1: Generate ANSI SQL CREATE TABLE DDL
     * Builds a CREATE TABLE IF NOT EXISTS statement with given table name,
     * column definitions, and primary key.
     * Example output:
     * "CREATE TABLE IF NOT EXISTS patients (id BIGINT, name VARCHAR(100), PRIMARY KEY (id));"
     */
    public static String generateCreateTableDdl(String tableName, LinkedHashMap<String, String> columns, String primaryKey) {
        // TODO: Construct and return the ANSI SQL CREATE TABLE statement.
        return null;
    }

    /**
     * Exercise 2: Simulate B-Tree Index Logarithmic Page Lookup
     * In a B-Tree, keys are stored sorted across pages. Given a sorted list of keys
     * and a target key, perform binary search to determine if key exists and count
     * the number of comparison hops (representing page reads).
     * Returns an int array [foundFlag (1 or 0), hopCount].
     */
    public static int[] simulateBTreeLookup(List<Integer> sortedKeys, int target) {
        // TODO: Implement binary search tracking comparison hops. Return [found (1/0), hops].
        return new int[]{0, 0};
    }

    /**
     * Exercise 3: Validate Referential Integrity (Foreign Key Validation)
     * Given a set of valid parent primary keys and a list of foreign key references from a child table,
     * return a list of invalid (orphan) foreign key IDs.
     */
    public static List<Long> findOrphanForeignKeys(Set<Long> parentPrimaryKeys, List<Long> childForeignKeys) {
        // TODO: Filter childForeignKeys and return all IDs that do NOT exist in parentPrimaryKeys.
        return Collections.emptyList();
    }

    /**
     * Exercise 4: Database Engine Architecture Profile
     * Returns a Map of architectural properties for the given engine name ("POSTGRESQL", "SQLITE", "H2").
     * Keys required: "type" ("CLIENT_SERVER" or "EMBEDDED"), "concurrency" ("MVCC" or "LOCKING").
     */
    public static Map<String, String> getEngineProfile(String engineName) {
        // TODO: Return map containing "type" and "concurrency" based on uppercase engineName.
        return Collections.emptyMap();
    }

    public static void main(String[] args) {
        // Test 1: DDL Generator
        LinkedHashMap<String, String> cols = new LinkedHashMap<>();
        cols.put("patient_id", "BIGINT");
        cols.put("full_name", "VARCHAR(150)");
        cols.put("dob", "DATE");
        String ddl = generateCreateTableDdl("patients", cols, "patient_id");
        assert ddl != null && ddl.contains("CREATE TABLE IF NOT EXISTS patients") : "DDL generation failed";
        assert ddl.contains("PRIMARY KEY (patient_id)") : "Primary key missing from DDL";
        System.out.println("Exercise 1 passed: " + ddl);

        // Test 2: B-Tree Lookup
        List<Integer> keys = new ArrayList<>();
        for (int i = 0; i < 1024; i += 2) {
            keys.add(i);
        }
        int[] result = simulateBTreeLookup(keys, 500);
        assert result[0] == 1 : "Expected key 500 to be found";
        assert result[1] <= 11 : "B-Tree lookup exceeded logarithmic hops: " + result[1];

        int[] notFound = simulateBTreeLookup(keys, 501);
        assert notFound[0] == 0 : "Expected key 501 to not be found";
        System.out.println("Exercise 2 passed: found in " + result[1] + " hops.");

        // Test 3: Referential Integrity
        Set<Long> parents = Set.of(101L, 102L, 103L);
        List<Long> children = List.of(101L, 102L, 999L, 103L, 888L);
        List<Long> orphans = findOrphanForeignKeys(parents, children);
        assert orphans.size() == 2 && orphans.contains(999L) && orphans.contains(888L) : "Orphan detection failed";
        System.out.println("Exercise 3 passed: found orphans " + orphans);

        // Test 4: Engine Profile
        Map<String, String> pg = getEngineProfile("POSTGRESQL");
        assert "CLIENT_SERVER".equals(pg.get("type")) : "Postgres should be CLIENT_SERVER";
        assert "MVCC".equals(pg.get("concurrency")) : "Postgres should use MVCC";

        Map<String, String> h2 = getEngineProfile("H2");
        assert "EMBEDDED".equals(h2.get("type")) : "H2 should be EMBEDDED";
        System.out.println("Exercise 4 passed: " + pg + ", " + h2);

        System.out.println("All Unit 3.1 Exercises Passed Successfully!");
    }
}
