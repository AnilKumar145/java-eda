import java.util.*;

public class StarterCode {

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
            // TODO: Manage entity, record snapshot, add to identity map
        }

        public SurgicalProcedure find(Long id) {
            // TODO: Check identity map first; if missing, fetch from databaseStore, attach, snapshot, cache
            return null;
        }

        public List<String> flush() {
            // TODO: Compare managed entities against snapshots and return list of UPDATE SQL statements
            return Collections.emptyList();
        }

        public List<SurgicalProcedure> queryProceduresByStatus(String status) {
            // TODO: Return managed procedures matching status, sorted by id
            return Collections.emptyList();
        }
    }
}
