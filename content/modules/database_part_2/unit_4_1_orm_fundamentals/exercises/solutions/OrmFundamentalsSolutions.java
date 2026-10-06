import java.util.*;

/**
 * Unit 4.1 Solutions: ORM Fundamentals
 * Complete reference implementation for Unit 4.1 exercises.
 */
public class OrmFundamentalsSolutions {

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

    public static EntityDescriptor buildEntitySchema(String tableName, LinkedHashMap<String, String> columns, String primaryKey) {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalArgumentException("tableName cannot be null or blank");
        }
        if (primaryKey == null || primaryKey.isBlank()) {
            throw new IllegalArgumentException("primaryKey cannot be null or blank");
        }
        if (columns == null || !columns.containsKey(primaryKey)) {
            throw new IllegalArgumentException("columns must contain the primaryKey: " + primaryKey);
        }
        return new EntityDescriptor(tableName, primaryKey, new LinkedHashMap<>(columns));
    }

    public static String generateEntityTableDdl(String tableName, LinkedHashMap<String, String> columns,
                                               String primaryKey, String foreignKeyCol,
                                               String refTable, String refCol) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

        List<String> defs = new ArrayList<>();
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            defs.add(entry.getKey() + " " + entry.getValue());
        }
        defs.add("PRIMARY KEY (" + primaryKey + ")");

        if (foreignKeyCol != null && refTable != null && refCol != null) {
            defs.add("CONSTRAINT fk_" + foreignKeyCol + " FOREIGN KEY (" + foreignKeyCol + ") REFERENCES " + refTable + "(" + refCol + ")");
        }

        sb.append(String.join(", ", defs));
        sb.append(");");
        return sb.toString();
    }

    public static Ward hydrateEntity(Map<String, Object> rowData) {
        if (rowData == null) {
            return null;
        }
        Long id = ((Number) rowData.get("id")).longValue();
        String name = (String) rowData.get("name");
        int capacity = ((Number) rowData.get("capacity")).intValue();
        return new Ward(id, name, capacity);
    }

    public static void synchronizeBidirectional(Ward ward, Bed bed) {
        if (ward == null || bed == null) {
            return;
        }
        if (!ward.getBeds().contains(bed)) {
            ward.getBeds().add(bed);
        }
        bed.setWard(ward);
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
