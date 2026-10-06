import java.util.*;

public class Solution {

    public enum ProcedureState {
        TRANSIENT,
        MANAGED,
        DETACHED,
        REMOVED
    }

    public static class SurgicalProcedure {
        private final Long id;
        private String roomCode;
        private String leadSurgeon;
        private String status;
        private ProcedureState lifecycleState = ProcedureState.TRANSIENT;

        public SurgicalProcedure(Long id, String roomCode, String leadSurgeon, String status) {
            this.id = id;
            this.roomCode = roomCode;
            this.leadSurgeon = leadSurgeon;
            this.status = status;
        }

        public Long getId() { return id; }
        public String getRoomCode() { return roomCode; }
        public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
        public String getLeadSurgeon() { return leadSurgeon; }
        public void setLeadSurgeon(String leadSurgeon) { this.leadSurgeon = leadSurgeon; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public ProcedureState getLifecycleState() { return lifecycleState; }
        public void setLifecycleState(ProcedureState state) { this.lifecycleState = state; }
    }

    public static class SurgicalPersistenceContext {
        private final Map<Long, SurgicalProcedure> identityMap = new HashMap<>();
        private final Map<Long, Map<String, Object>> snapshots = new HashMap<>();
        private final Map<Long, SurgicalProcedure> databaseStore = new HashMap<>();

        public void seedDatabase(SurgicalProcedure proc) {
            databaseStore.put(proc.getId(), proc);
        }

        public void persist(SurgicalProcedure proc) {
            if (proc == null) return;
            proc.setLifecycleState(ProcedureState.MANAGED);
            identityMap.put(proc.getId(), proc);
            snapshots.put(proc.getId(), createSnapshot(proc));
        }

        public SurgicalProcedure find(Long id) {
            if (id == null) return null;
            if (identityMap.containsKey(id)) {
                return identityMap.get(id);
            }
            SurgicalProcedure fromDb = databaseStore.get(id);
            if (fromDb != null) {
                fromDb.setLifecycleState(ProcedureState.MANAGED);
                identityMap.put(id, fromDb);
                snapshots.put(id, createSnapshot(fromDb));
            }
            return fromDb;
        }

        private Map<String, Object> createSnapshot(SurgicalProcedure proc) {
            Map<String, Object> snap = new HashMap<>();
            snap.put("roomCode", proc.getRoomCode());
            snap.put("leadSurgeon", proc.getLeadSurgeon());
            snap.put("status", proc.getStatus());
            return snap;
        }

        public List<String> flush() {
            List<String> updates = new ArrayList<>();

            for (Map.Entry<Long, SurgicalProcedure> entry : identityMap.entrySet()) {
                Long id = entry.getKey();
                SurgicalProcedure proc = entry.getValue();

                if (proc.getLifecycleState() != ProcedureState.MANAGED) {
                    continue;
                }

                Map<String, Object> snap = snapshots.get(id);
                if (snap == null) continue;

                List<String> setClauses = new ArrayList<>();
                if (!Objects.equals(snap.get("roomCode"), proc.getRoomCode())) {
                    setClauses.add("room_code = '" + proc.getRoomCode() + "'");
                }
                if (!Objects.equals(snap.get("leadSurgeon"), proc.getLeadSurgeon())) {
                    setClauses.add("lead_surgeon = '" + proc.getLeadSurgeon() + "'");
                }
                if (!Objects.equals(snap.get("status"), proc.getStatus())) {
                    setClauses.add("status = '" + proc.getStatus() + "'");
                }

                if (!setClauses.isEmpty()) {
                    String sql = "UPDATE surgical_procedures SET " + String.join(", ", setClauses) +
                                 " WHERE id = " + id + ";";
                    updates.add(sql);
                    // refresh snapshot after flush
                    snapshots.put(id, createSnapshot(proc));
                }
            }

            return updates;
        }

        public List<SurgicalProcedure> queryProceduresByStatus(String status) {
            List<SurgicalProcedure> results = new ArrayList<>();
            for (SurgicalProcedure proc : identityMap.values()) {
                if (proc.getLifecycleState() == ProcedureState.MANAGED && Objects.equals(proc.getStatus(), status)) {
                    results.add(proc);
                }
            }
            results.sort(Comparator.comparing(SurgicalProcedure::getId));
            return results;
        }
    }
}
