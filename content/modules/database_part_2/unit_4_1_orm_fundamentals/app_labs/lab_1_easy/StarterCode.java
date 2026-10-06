import java.util.*;

public class StarterCode {

    public record ColumnDef(String name, String typeAndConstraints) {}

    public record TableSchema(
        String tableName,
        String primaryKey,
        List<ColumnDef> columns,
        String foreignKeyConstraint
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

        public void assignBed(Bed bed) {
            // TODO: Validate capacity, add to beds, and set bed.setWard(this)
        }

        public void releaseBed(Bed bed) {
            // TODO: Remove from beds and clear bed.setWard(null)
        }
    }

    public static class Bed {
        private final Long id;
        private final String bedCode;
        private final String status;
        private Ward ward;

        public Bed(Long id, String bedCode, String status) {
            this.id = id;
            this.bedCode = bedCode;
            this.status = status;
        }

        public Long getId() { return id; }
        public String getBedCode() { return bedCode; }
        public String getStatus() { return status; }
        public Ward getWard() { return ward; }
        public void setWard(Ward ward) { this.ward = ward; }
    }

    public static class WardBedOrmEngine {

        public List<String> generateSchemaDdl(TableSchema wardSchema, TableSchema bedSchema) {
            // TODO: Generate DDL statements for wards and beds tables
            return Collections.emptyList();
        }

        public Ward hydrateWardGraph(Map<String, Object> wardRow, List<Map<String, Object>> bedRows) {
            // TODO: Hydrate Ward and associated Beds, establishing bidirectional links
            return null;
        }
    }
}
