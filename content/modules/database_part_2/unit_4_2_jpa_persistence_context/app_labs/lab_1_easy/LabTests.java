import java.util.*;

public class LabTests {

    public static void main(String[] args) {
        testPersistenceAndIdentityMap();
        testAutomaticDirtyChecking();
        testQueryProceduresByStatus();
        System.out.println("All Unit 4.2 Lab 1 Tests Passed Successfully!");
    }

    public static void testPersistenceAndIdentityMap() {
        Solution.SurgicalPersistenceContext context = new Solution.SurgicalPersistenceContext();

        Solution.SurgicalProcedure p1 = new Solution.SurgicalProcedure(101L, "OR-A", "Dr. Meredith Grey", "SCHEDULED");
        assert p1.getLifecycleState() == Solution.ProcedureState.TRANSIENT : "Initial state should be TRANSIENT";

        context.persist(p1);
        assert p1.getLifecycleState() == Solution.ProcedureState.MANAGED : "State after persist should be MANAGED";

        // Lookup from identity map
        Solution.SurgicalProcedure p1Cached = context.find(101L);
        assert p1Cached == p1 : "Identity Map should return identical object reference";
        System.out.println("Lab 1 Test 1 Passed: Persistence and First-Level Cache identity map validated.");
    }

    public static void testAutomaticDirtyChecking() {
        Solution.SurgicalPersistenceContext context = new Solution.SurgicalPersistenceContext();

        Solution.SurgicalProcedure p1 = new Solution.SurgicalProcedure(201L, "OR-B", "Dr. Derek Shepherd", "SCHEDULED");
        context.persist(p1);

        // No changes made yet
        List<String> noUpdates = context.flush();
        assert noUpdates.isEmpty() : "Unmodified entity should generate zero updates";

        // Mutate fields directly on managed entity
        p1.setStatus("IN_PROGRESS");
        p1.setRoomCode("OR-C");

        List<String> updates = context.flush();
        assert updates.size() == 1 : "Should generate exactly 1 UPDATE statement";
        String sql = updates.get(0);
        assert sql.contains("UPDATE surgical_procedures SET") : "Malformed update statement";
        assert sql.contains("status = 'IN_PROGRESS'") : "Missing status update";
        assert sql.contains("room_code = 'OR-C'") : "Missing room update";
        assert sql.contains("WHERE id = 201;") : "Missing WHERE clause";

        // Second flush should be clean because snapshot was updated
        List<String> secondFlush = context.flush();
        assert secondFlush.isEmpty() : "Second flush without changes should generate zero updates";
        System.out.println("Lab 1 Test 2 Passed: Automatic dirty checking and flush emission validated.");
    }

    public static void testQueryProceduresByStatus() {
        Solution.SurgicalPersistenceContext context = new Solution.SurgicalPersistenceContext();

        context.persist(new Solution.SurgicalProcedure(301L, "OR-1", "Dr. House", "COMPLETED"));
        context.persist(new Solution.SurgicalProcedure(302L, "OR-2", "Dr. Watson", "SCHEDULED"));
        context.persist(new Solution.SurgicalProcedure(303L, "OR-3", "Dr. Strange", "SCHEDULED"));
        context.persist(new Solution.SurgicalProcedure(304L, "OR-4", "Dr. Cuddy", "IN_PROGRESS"));

        List<Solution.SurgicalProcedure> scheduled = context.queryProceduresByStatus("SCHEDULED");
        assert scheduled.size() == 2 : "Should find 2 SCHEDULED procedures";
        assert scheduled.get(0).getId().equals(302L) : "Procedures should be ordered by id ascending";
        assert scheduled.get(1).getId().equals(303L) : "Procedures should be ordered by id ascending";

        List<Solution.SurgicalProcedure> completed = context.queryProceduresByStatus("COMPLETED");
        assert completed.size() == 1 && completed.get(0).getId().equals(301L) : "Should find procedure 301";
        System.out.println("Lab 1 Test 3 Passed: Procedure query filtering by status validated.");
    }
}
