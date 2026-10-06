import java.util.*;

public class Solution {

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
            if (bed == null) return;
            if (this.beds.size() >= this.capacity) {
                throw new IllegalStateException("Ward is at full capacity: " + this.capacity);
            }
            if (!this.beds.contains(bed)) {
                this.beds.add(bed);
            }
            bed.setWard(this);
        }

        public void releaseBed(Bed bed) {
            if (bed == null) return;
            this.beds.remove(bed);
            bed.setWard(null);
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
            List<String> ddlList = new ArrayList<>();
            ddlList.add(formatTableDdl(wardSchema));
            ddlList.add(formatTableDdl(bedSchema));
            return ddlList;
        }

        private String formatTableDdl(TableSchema schema) {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ").append(schema.tableName()).append(" (");

            List<String> definitions = new ArrayList<>();
            for (ColumnDef col : schema.columns()) {
                definitions.add(col.name() + " " + col.typeAndConstraints());
            }
            definitions.add("PRIMARY KEY (" + schema.primaryKey() + ")");

            if (schema.foreignKeyConstraint() != null && !schema.foreignKeyConstraint().isBlank()) {
                definitions.add(schema.foreignKeyConstraint());
            }

            sb.append(String.join(", ", definitions));
            sb.append(");");
            return sb.toString();
        }

        public Ward hydrateWardGraph(Map<String, Object> wardRow, List<Map<String, Object>> bedRows) {
            if (wardRow == null) return null;

            Long wardId = ((Number) wardRow.get("id")).longValue();
            String name = (String) wardRow.get("name");
            int capacity = ((Number) wardRow.get("capacity")).intValue();

            Ward ward = new Ward(wardId, name, capacity);

            if (bedRows != null) {
                for (Map<String, Object> bRow : bedRows) {
                    Long bedId = ((Number) bRow.get("id")).longValue();
                    String bedCode = (String) bRow.get("bed_code");
                    String status = (String) bRow.get("status");

                    Bed bed = new Bed(bedId, bedCode, status);
                    ward.assignBed(bed);
                }
            }
            return ward;
        }
    }
}
