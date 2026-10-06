import java.util.*;

public class Solution {

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
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ").append(schema.tableName()).append(" (\n");

            List<String> definitions = new ArrayList<>();
            for (Map.Entry<String, String> col : schema.columns().entrySet()) {
                definitions.add("    " + col.getKey() + " " + col.getValue());
            }

            if (schema.primaryKey() != null && !schema.primaryKey().isBlank()) {
                definitions.add("    CONSTRAINT pk_" + schema.tableName() + " PRIMARY KEY (" + schema.primaryKey() + ")");
            }

            if (schema.foreignKeys() != null) {
                for (Map.Entry<String, String> fk : schema.foreignKeys().entrySet()) {
                    definitions.add("    CONSTRAINT fk_" + fk.getKey() + " FOREIGN KEY (" + fk.getKey() + ") REFERENCES " + fk.getValue());
                }
            }

            sb.append(String.join(",\n", definitions));
            sb.append("\n);");
            return sb.toString();
        }

        public List<EncounterRecord> validateEncounterBatch(Set<Long> registeredPatientIds, List<EncounterRecord> encounters) {
            List<EncounterRecord> violations = new ArrayList<>();
            for (EncounterRecord encounter : encounters) {
                if (encounter.patientId() == null || !registeredPatientIds.contains(encounter.patientId())) {
                    violations.add(encounter);
                }
            }
            return violations;
        }
    }
}
