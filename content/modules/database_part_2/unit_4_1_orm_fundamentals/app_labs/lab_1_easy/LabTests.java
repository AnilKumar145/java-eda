import java.util.*;

public class LabTests {

    public static void main(String[] args) {
        testSchemaDdlGeneration();
        testBidirectionalInvariantEnforcement();
        testWardGraphHydration();
        System.out.println("All Unit 4.1 Lab 1 Tests Passed Successfully!");
    }

    public static void testSchemaDdlGeneration() {
        Solution.WardBedOrmEngine engine = new Solution.WardBedOrmEngine();

        List<Solution.ColumnDef> wardCols = List.of(
            new Solution.ColumnDef("id", "BIGINT"),
            new Solution.ColumnDef("name", "VARCHAR(100) NOT NULL"),
            new Solution.ColumnDef("capacity", "INT NOT NULL")
        );
        Solution.TableSchema wardSchema = new Solution.TableSchema("wards", "id", wardCols, null);

        List<Solution.ColumnDef> bedCols = List.of(
            new Solution.ColumnDef("id", "BIGINT"),
            new Solution.ColumnDef("bed_code", "VARCHAR(30) NOT NULL UNIQUE"),
            new Solution.ColumnDef("ward_id", "BIGINT NOT NULL")
        );
        String fk = "CONSTRAINT fk_beds_ward FOREIGN KEY (ward_id) REFERENCES wards(id)";
        Solution.TableSchema bedSchema = new Solution.TableSchema("beds", "id", bedCols, fk);

        List<String> ddl = engine.generateSchemaDdl(wardSchema, bedSchema);
        assert ddl.size() == 2 : "Should generate 2 DDL statements";
        assert ddl.get(0).contains("CREATE TABLE IF NOT EXISTS wards") : "Wards DDL missing";
        assert ddl.get(0).contains("PRIMARY KEY (id)") : "Wards PK missing";
        assert ddl.get(1).contains("CREATE TABLE IF NOT EXISTS beds") : "Beds DDL missing";
        assert ddl.get(1).contains("CONSTRAINT fk_beds_ward FOREIGN KEY (ward_id) REFERENCES wards(id)") : "FK constraint missing";
        System.out.println("Lab 1 Test 1 Passed: DDL generation validated.");
    }

    public static void testBidirectionalInvariantEnforcement() {
        Solution.Ward ward = new Solution.Ward(10L, "Intensive Care Unit", 2);
        Solution.Bed bed1 = new Solution.Bed(101L, "ICU-01", "AVAILABLE");
        Solution.Bed bed2 = new Solution.Bed(102L, "ICU-02", "OCCUPIED");
        Solution.Bed bed3 = new Solution.Bed(103L, "ICU-03", "AVAILABLE");

        ward.assignBed(bed1);
        ward.assignBed(bed2);

        assert ward.getBeds().size() == 2 : "Ward should contain 2 beds";
        assert bed1.getWard() == ward : "Bed 1 should point to ward";
        assert bed2.getWard() == ward : "Bed 2 should point to ward";

        // Capacity overflow check
        boolean caughtCapacityError = false;
        try {
            ward.assignBed(bed3);
        } catch (IllegalStateException e) {
            caughtCapacityError = true;
        }
        assert caughtCapacityError : "Should throw IllegalStateException when exceeding capacity";

        // Release check
        ward.releaseBed(bed1);
        assert ward.getBeds().size() == 1 : "Ward should contain 1 bed after release";
        assert bed1.getWard() == null : "Bed 1 ward reference should be null after release";
        System.out.println("Lab 1 Test 2 Passed: Bidirectional invariants and capacity enforcement validated.");
    }

    public static void testWardGraphHydration() {
        Solution.WardBedOrmEngine engine = new Solution.WardBedOrmEngine();

        Map<String, Object> wardRow = Map.of(
            "id", 201L,
            "name", "Pediatric Oncology",
            "capacity", 15
        );

        List<Map<String, Object>> bedRows = List.of(
            Map.of("id", 1001L, "bed_code", "PED-ONC-01", "status", "OCCUPIED"),
            Map.of("id", 1002L, "bed_code", "PED-ONC-02", "status", "AVAILABLE")
        );

        Solution.Ward hydratedWard = engine.hydrateWardGraph(wardRow, bedRows);
        assert hydratedWard != null : "Hydrated ward should not be null";
        assert hydratedWard.getId().equals(201L) : "Ward id mismatch";
        assert "Pediatric Oncology".equals(hydratedWard.getName()) : "Ward name mismatch";
        assert hydratedWard.getBeds().size() == 2 : "Hydrated ward should have 2 beds";
        assert hydratedWard.getBeds().get(0).getWard() == hydratedWard : "Bed 0 inverse link missing";
        assert hydratedWard.getBeds().get(1).getWard() == hydratedWard : "Bed 1 inverse link missing";
        System.out.println("Lab 1 Test 3 Passed: Nested entity graph hydration validated.");
    }
}
