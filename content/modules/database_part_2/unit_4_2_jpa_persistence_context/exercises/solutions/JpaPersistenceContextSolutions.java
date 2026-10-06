import java.util.*;

/**
 * Unit 4.2 Solutions: JPA/Hibernate Persistence Context
 * Complete reference implementation for Unit 4.2 exercises.
 */
public class JpaPersistenceContextSolutions {

    public enum EntityLifecycleState {
        TRANSIENT,
        MANAGED,
        DETACHED,
        REMOVED
    }

    public static EntityLifecycleState transitionState(EntityLifecycleState current, String operation) {
        if (current == null || operation == null) {
            throw new IllegalArgumentException("State and operation cannot be null");
        }
        String op = operation.trim().toLowerCase();

        return switch (current) {
            case TRANSIENT -> {
                if ("persist".equals(op)) yield EntityLifecycleState.MANAGED;
                throw new IllegalStateException("Cannot perform " + op + " on TRANSIENT entity");
            }
            case MANAGED -> {
                if ("detach".equals(op) || "clear".equals(op)) yield EntityLifecycleState.DETACHED;
                if ("remove".equals(op)) yield EntityLifecycleState.REMOVED;
                if ("persist".equals(op)) yield EntityLifecycleState.MANAGED; // idempotent
                throw new IllegalStateException("Invalid operation " + op + " on MANAGED entity");
            }
            case DETACHED -> {
                if ("merge".equals(op)) yield EntityLifecycleState.MANAGED;
                throw new IllegalStateException("Cannot perform " + op + " on DETACHED entity without merge");
            }
            case REMOVED -> {
                if ("persist".equals(op)) yield EntityLifecycleState.MANAGED;
                throw new IllegalStateException("Cannot perform " + op + " on REMOVED entity");
            }
        };
    }

    public static class IdentityMap<K, V> {
        private final Map<K, V> cache = new HashMap<>();
        private int hitCount = 0;
        private int missCount = 0;

        public V get(K key) {
            if (cache.containsKey(key)) {
                hitCount++;
                return cache.get(key);
            } else {
                missCount++;
                return null;
            }
        }

        public void put(K key, V entity) {
            cache.put(key, entity);
        }

        public int getHitCount() { return hitCount; }
        public int getMissCount() { return missCount; }
    }

    public static String detectDirtyAndFlush(String tableName, String pkCol, Object pkVal,
                                            Map<String, Object> initialSnapshot,
                                            Map<String, Object> currentValues) {
        if (tableName == null || pkCol == null || initialSnapshot == null || currentValues == null) {
            return null;
        }

        List<String> assignments = new ArrayList<>();
        for (Map.Entry<String, Object> entry : currentValues.entrySet()) {
            String col = entry.getKey();
            Object currentVal = entry.getValue();
            Object initialVal = initialSnapshot.get(col);

            if (!Objects.equals(initialVal, currentVal)) {
                if (currentVal instanceof String || currentVal instanceof Enum<?>) {
                    assignments.add(col + " = '" + currentVal + "'");
                } else {
                    assignments.add(col + " = " + currentVal);
                }
            }
        }

        if (assignments.isEmpty()) {
            return null;
        }

        return "UPDATE " + tableName + " SET " + String.join(", ", assignments) +
               " WHERE " + pkCol + " = " + pkVal + ";";
    }

    public static String bindJpqlParameters(String jpql, Map<String, Object> params) {
        if (jpql == null) return null;
        if (params == null || params.isEmpty()) return jpql;

        String result = jpql;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = ":" + entry.getKey();
            Object val = entry.getValue();
            String formatted;
            if (val instanceof String || val instanceof Enum<?>) {
                formatted = "'" + val + "'";
            } else {
                formatted = String.valueOf(val);
            }
            result = result.replace(placeholder, formatted);
        }
        return result;
    }

    public static void main(String[] args) {
        // Test 1: State transitions
        assert transitionState(EntityLifecycleState.TRANSIENT, "persist") == EntityLifecycleState.MANAGED : "Transient persist failed";
        assert transitionState(EntityLifecycleState.MANAGED, "detach") == EntityLifecycleState.DETACHED : "Managed detach failed";
        assert transitionState(EntityLifecycleState.DETACHED, "merge") == EntityLifecycleState.MANAGED : "Detached merge failed";
        assert transitionState(EntityLifecycleState.MANAGED, "remove") == EntityLifecycleState.REMOVED : "Managed remove failed";
        System.out.println("Exercise 1 passed: Lifecycle state transitions validated.");

        // Test 2: First-Level Cache
        IdentityMap<Long, String> cache = new IdentityMap<>();
        assert cache.get(101L) == null : "Should be cache miss";
        assert cache.getMissCount() == 1 : "Miss count should be 1";
        cache.put(101L, "Patient#101");
        assert "Patient#101".equals(cache.get(101L)) : "Cache hit returned incorrect value";
        assert cache.getHitCount() == 1 : "Hit count should be 1";
        System.out.println("Exercise 2 passed: First-Level Cache identity map validated.");

        // Test 3: Dirty Checking
        Map<String, Object> snapshot = Map.of("status", "SCHEDULED", "room", "OR-1");
        Map<String, Object> updated = Map.of("status", "IN_PROGRESS", "room", "OR-1");
        String sql = detectDirtyAndFlush("surgeries", "id", 55L, snapshot, updated);
        assert sql != null && sql.contains("UPDATE surgeries SET") : "Update statement not generated";
        assert sql.contains("status = 'IN_PROGRESS'") : "Dirty column not updated";
        assert sql.contains("WHERE id = 55;") : "Where clause missing";

        String cleanSql = detectDirtyAndFlush("surgeries", "id", 55L, snapshot, snapshot);
        assert cleanSql == null : "Clean entity should not generate SQL";
        System.out.println("Exercise 3 passed: Dirty checking and SQL generation validated.");

        // Test 4: Parameter Binding
        String query = "SELECT p FROM Procedure p WHERE p.status = :status AND p.room = :roomNumber";
        Map<String, Object> params = Map.of("status", "ACTIVE", "roomNumber", "OR-3");
        String bound = bindJpqlParameters(query, params);
        assert bound.contains("p.status = 'ACTIVE'") : "Status parameter not bound correctly";
        assert bound.contains("p.room = 'OR-3'") : "Room parameter not bound correctly";
        System.out.println("Exercise 4 passed: JPQL parameter binding validated.");

        System.out.println("All Unit 4.2 Exercise Tests Passed!");
    }
}
