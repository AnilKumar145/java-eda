import java.util.*;

/**
 * Unit 3.1 Exercises: Database Fundamentals & Relational Concepts - Solutions
 */
public class DatabaseFundamentalsSolutions {

    public static String generateCreateTableDdl(String tableName, LinkedHashMap<String, String> columns, String primaryKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        boolean first = true;
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append(" ").append(entry.getValue());
            first = false;
        }
        if (primaryKey != null && !primaryKey.isBlank()) {
            sb.append(", PRIMARY KEY (").append(primaryKey).append(")");
        }
        sb.append(");");
        return sb.toString();
    }

    public static int[] simulateBTreeLookup(List<Integer> sortedKeys, int target) {
        int low = 0;
        int high = sortedKeys.size() - 1;
        int hops = 0;

        while (low <= high) {
            hops++;
            int mid = (low + high) >>> 1;
            int midVal = sortedKeys.get(mid);

            if (midVal < target) {
                low = mid + 1;
            } else if (midVal > target) {
                high = mid - 1;
            } else {
                return new int[]{1, hops};
            }
        }
        return new int[]{0, hops};
    }

    public static List<Long> findOrphanForeignKeys(Set<Long> parentPrimaryKeys, List<Long> childForeignKeys) {
        List<Long> orphans = new ArrayList<>();
        for (Long fk : childForeignKeys) {
            if (!parentPrimaryKeys.contains(fk)) {
                orphans.add(fk);
            }
        }
        return orphans;
    }

    public static Map<String, String> getEngineProfile(String engineName) {
        Map<String, String> profile = new HashMap<>();
        String upper = engineName == null ? "" : engineName.toUpperCase();
        switch (upper) {
            case "POSTGRESQL" -> {
                profile.put("type", "CLIENT_SERVER");
                profile.put("concurrency", "MVCC");
            }
            case "SQLITE" -> {
                profile.put("type", "EMBEDDED");
                profile.put("concurrency", "LOCKING");
            }
            case "H2" -> {
                profile.put("type", "EMBEDDED");
                profile.put("concurrency", "MVCC");
            }
            default -> {
                profile.put("type", "UNKNOWN");
                profile.put("concurrency", "UNKNOWN");
            }
        }
        return profile;
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

        System.out.println("All Unit 3.1 Solution Exercises Passed Successfully!");
    }
}
