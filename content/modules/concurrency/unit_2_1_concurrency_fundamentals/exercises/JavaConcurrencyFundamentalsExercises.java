import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Unit 2.1 Exercises: Java Concurrency Fundamentals
 * Implement the exercise methods to pass all assertion tests.
 */
public class JavaConcurrencyFundamentalsExercises {

    // -------------------------------------------------------------------------
    // Exercise 1: Immutable Domain Record with Defensive Copy
    // -------------------------------------------------------------------------
    public record ImmutableUserProfile(String username, int age, List<String> roles) {
        public ImmutableUserProfile {
            // TODO: Ensure roles is defensively copied with List.copyOf()
        }
    }

    // -------------------------------------------------------------------------
    // Exercise 2: Atomic State Snapshot Transition
    // -------------------------------------------------------------------------
    public static class AppStateContainer {
        public record AppState(String status, int activeJobs) {}

        private final AtomicReference<AppState> state =
            new AtomicReference<>(new AppState("INIT", 0));

        public void transition(String newStatus, int newActiveJobs) {
            // TODO: Atomically update state with new AppState
        }

        public AppState getCurrentState() {
            // TODO: Return current state
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Verification Tests
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        testExercise1();
        testExercise2();
        System.out.println("All Unit 2.1 Exercises Passed Successfully!");
    }

    private static void testExercise1() {
        List<String> mutableRoles = new ArrayList<>();
        mutableRoles.add("ROLE_USER");

        ImmutableUserProfile profile = new ImmutableUserProfile("alice", 28, mutableRoles);
        // Modify external list
        mutableRoles.add("ROLE_ADMIN");

        // Profile must NOT have been modified!
        assert profile.roles().size() == 1 : "Defensive copy failed! Roles size was " + profile.roles().size();
        assert !profile.roles().contains("ROLE_ADMIN") : "Profile was modified from outside!";
        System.out.println("Exercise 1 passed!");
    }

    private static void testExercise2() throws Exception {
        AppStateContainer container = new AppStateContainer();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            final int id = i;
            threads.add(new Thread(() -> container.transition("RUNNING", id)));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        assert "RUNNING".equals(container.getCurrentState().status()) : "Status should be RUNNING";
        System.out.println("Exercise 2 passed!");
    }
}
