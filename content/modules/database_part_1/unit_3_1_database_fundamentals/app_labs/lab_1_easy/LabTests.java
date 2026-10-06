import java.util.*;

public class LabTests {

    public static void main(String[] args) {
        testPatientSchemaDdlGeneration();
        testReferentialIntegrityValidation();
        System.out.println("All Unit 3.1 Lab 1 Tests Passed Successfully!");
    }

    public static void testPatientSchemaDdlGeneration() {
        Solution.HospitalRegistryArchitect architect = new Solution.HospitalRegistryArchitect();

        LinkedHashMap<String, String> patientCols = new LinkedHashMap<>();
        patientCols.put("patient_id", "BIGINT GENERATED ALWAYS AS IDENTITY");
        patientCols.put("mrn", "VARCHAR(64) NOT NULL UNIQUE");
        patientCols.put("full_name", "VARCHAR(150) NOT NULL");

        Solution.SchemaDefinition patientSchema = new Solution.SchemaDefinition(
            "patients",
            "patient_id",
            patientCols,
            new LinkedHashMap<>()
        );

        String ddl = architect.generateDdl(patientSchema);
        assert ddl.contains("CREATE TABLE IF NOT EXISTS patients") : "DDL must have CREATE TABLE clause";
        assert ddl.contains("CONSTRAINT pk_patients PRIMARY KEY (patient_id)") : "Primary key constraint missing";
        System.out.println("Lab 1 Test 1 Passed: Patient schema DDL validated.");

        LinkedHashMap<String, String> encounterCols = new LinkedHashMap<>();
        encounterCols.put("encounter_id", "BIGINT GENERATED ALWAYS AS IDENTITY");
        encounterCols.put("patient_id", "BIGINT NOT NULL");
        encounterCols.put("department", "VARCHAR(80) NOT NULL");

        LinkedHashMap<String, String> encounterFks = new LinkedHashMap<>();
        encounterFks.put("patient_id", "patients(patient_id)");

        Solution.SchemaDefinition encounterSchema = new Solution.SchemaDefinition(
            "medical_encounters",
            "encounter_id",
            encounterCols,
            encounterFks
        );

        String encounterDdl = architect.generateDdl(encounterSchema);
        assert encounterDdl.contains("CONSTRAINT fk_patient_id FOREIGN KEY (patient_id) REFERENCES patients(patient_id)") : "FK missing";
        System.out.println("Lab 1 Test 2 Passed: Encounter schema DDL with FK constraint validated.");
    }

    public static void testReferentialIntegrityValidation() {
        Solution.HospitalRegistryArchitect architect = new Solution.HospitalRegistryArchitect();

        Set<Long> registeredPatients = Set.of(1001L, 1002L, 1003L);

        List<Solution.EncounterRecord> batch = List.of(
            new Solution.EncounterRecord(1L, 1001L, "Emergency", "Dr. House"),
            new Solution.EncounterRecord(2L, 9999L, "Cardiology", "Dr. Watson"), // Invalid FK
            new Solution.EncounterRecord(3L, 1003L, "Radiology", "Dr. Strange"),
            new Solution.EncounterRecord(4L, 7777L, "Neurology", "Dr. Grey")    // Invalid FK
        );

        List<Solution.EncounterRecord> violations = architect.validateEncounterBatch(registeredPatients, batch);
        assert violations.size() == 2 : "Expected 2 referential integrity violations, got " + violations.size();
        assert violations.get(0).encounterId() == 2L : "First violation mismatch";
        assert violations.get(1).encounterId() == 4L : "Second violation mismatch";
        System.out.println("Lab 1 Test 3 Passed: In-memory referential integrity violations correctly intercepted.");
    }
}
