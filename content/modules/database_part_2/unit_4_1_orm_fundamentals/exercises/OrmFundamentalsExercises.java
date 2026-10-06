import java.util.*;

/**
 * Unit 4.1 Exercises: ORM Fundamentals
 * Implement each method according to its documentation to pass all assertion tests.
 */
public class OrmFundamentalsExercises {

    public record EntityDescriptor(
        String tableName,
        String primaryKey,
        LinkedHashMap<String, String> columns
    ) {}

    public static class Ward {
        private final Long id;
        private final String name;
        private final int capacity;
        private final List<Bed> beds = new ArrayList<>();

        public Ward(Long id, String name, int capacity) {
            this.id = id;
            this.name = name;
            this.capacity = capacity;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public int getCapacity() { return capacity; }
        public List<Bed> getBeds() { return beds; }
    }

    public static class Bed {
        private final Long id;
        private final String bedNumber;
        private Ward ward;

        public Bed(Long id, String bedNumber) {
            this.id = id;
            this.bedNumber = bedNumber;
        }

        public Long getId() { return id; }
        public String getBedNumber() { return bedNumber; }
        public Ward getWard() { return ward; }
        public void setWard(Ward ward) { this.ward = ward; }
    }

    /**
     * Exercise 1: Entity Metadata Construction
     * Build and return an EntityDescriptor record from the provided arguments.
     * Validate that tableName and primaryKey are not null/blank and columns contains primaryKey.
     * Throw IllegalArgumentException if validation fails.
     */
    public static EntityDescriptor buildEntitySchema(String tableName, LinkedHashMap<String, String> columns, String primaryKey) {
        // TODO: Validate arguments and return a new EntityDescriptor instance.
        return null;
    }

    /**
     * Exercise 2: DDL Generation from Entity Metadata
     * Generate an ANSI SQL CREATE TABLE IF NOT EXISTS statement.
     * Format: "CREATE TABLE IF NOT EXISTS {tableName} ({colDefs}, PRIMARY KEY ({primaryKey})[, CONSTRAINT fk_{col} FOREIGN KEY ({foreignKeyCol}) REFERENCES {refTable}({refCol})]);"
     */
    public static String generateEntityTableDdl(String tableName, LinkedHashMap<String, String> columns,
                                               String primaryKey, String foreignKeyCol,
                                               String refTable, String refCol) {
        // TODO: Assemble and return the DDL string.
        return null;
    }

    /**
     * Exercise 3: Relational Tuple Hydration
     * Given a map representing a database row with keys "id", "name", and "capacity",
     * instantiate and return a new Ward domain object.
     */
    public static Ward hydrateEntity(Map<String, Object> rowData) {
        // TODO: Extract values and return a populated Ward instance.
        return null;
    }

    /**
     * Exercise 4: Bidirectional Association Synchronization
     * Defensively synchronize the parent Ward and child Bed in memory:
     * 1. If ward.getBeds() does not contain bed, add bed to ward.getBeds().
     * 2. Set bed.setWard(ward).
     */
    public static void synchronizeBidirectional(Ward ward, Bed bed) {
        // TODO: Link both sides of the association.
    }

    public static void main(String[] args) {
        // Test 1: Build Entity Schema
        LinkedHashMap<String, String> wardCols = new LinkedHashMap<>();
        wardCols.put("id", "BIGINT");
        wardCols.put("name", "VARCHAR(100)");
        wardCols.put("capacity", "INT");
        EntityDescriptor schema = buildEntitySchema("wards", wardCols, "id");
        assert schema != null : "Schema should not be null";
        assert "wards".equals(schema.tableName()) : "Table name mismatch";
        System.out.println("Exercise 1 passed: Entity schema built successfully.");

        // Test 2: DDL Generation
        LinkedHashMap<String, String> bedCols = new LinkedHashMap<>();
        bedCols.put("id", "BIGINT");
        bedCols.put("bed_number", "VARCHAR(20)");
        bedCols.put("ward_id", "BIGINT");
        String ddl = generateEntityTableDdl("beds", bedCols, "id", "ward_id", "wards", "id");
        assert ddl.contains("CREATE TABLE IF NOT EXISTS beds") : "DDL missing table statement";
        assert ddl.contains("PRIMARY KEY (id)") : "DDL missing primary key";
        assert ddl.contains("FOREIGN KEY (ward_id) REFERENCES wards(id)") : "DDL missing foreign key constraint";
        System.out.println("Exercise 2 passed: DDL generated correctly.");

        // Test 3: Hydration
        Map<String, Object> row = Map.of("id", 101L, "name", "Cardiology Ward", "capacity", 30);
        Ward ward = hydrateEntity(row);
        assert ward != null && ward.getId().equals(101L) : "Ward id mismatch";
        assert "Cardiology Ward".equals(ward.getName()) : "Ward name mismatch";
        assert ward.getCapacity() == 30 : "Ward capacity mismatch";
        System.out.println("Exercise 3 passed: Entity hydrated from row data.");

        // Test 4: Bidirectional Synchronization
        Bed bed = new Bed(501L, "CARD-BED-01");
        synchronizeBidirectional(ward, bed);
        assert ward.getBeds().contains(bed) : "Ward does not contain bed";
        assert bed.getWard() == ward : "Bed does not reference ward";
        System.out.println("Exercise 4 passed: Bidirectional association synchronized.");

        System.out.println("All Unit 4.1 Exercise Tests Passed!");
    }
}
