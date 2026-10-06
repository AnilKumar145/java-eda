import java.util.*;

public class StarterCode {

    public record SchemaDefinition(
        String tableName,
        String primaryKey,
        LinkedHashMap<String, String> columns,
        LinkedHashMap<String, String> foreignKeys
    ) {}

    public record EncounterRecord(
        Long encounterId,
        Long patientId,
        String department,
        String physician
    ) {}

    public static class HospitalRegistryArchitect {

        public String generateDdl(SchemaDefinition schema) {
            // TODO: Generate ANSI DDL statement with primary key and foreign key constraints
            return null;
        }

        public List<EncounterRecord> validateEncounterBatch(Set<Long> registeredPatientIds, List<EncounterRecord> encounters) {
            // TODO: Return all encounter records whose patientId is not present in registeredPatientIds
            return Collections.emptyList();
        }
    }
}
