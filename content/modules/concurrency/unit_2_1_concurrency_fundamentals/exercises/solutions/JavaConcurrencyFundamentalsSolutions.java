import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Unit 2.1 Exercises: Java Concurrency Fundamentals - Solutions
 */
public class JavaConcurrencyFundamentalsSolutions {

    public record ImmutableUserProfile(String username, int age, List<String> roles) {
        public ImmutableUserProfile {
            roles = List.copyOf(roles);
        }
    }

    public static class AppStateContainer {
        public record AppState(String status, int activeJobs) {}

        private final AtomicReference<AppState> state =
            new AtomicReference<>(new AppState("INIT", 0));

        public void transition(String newStatus, int newActiveJobs) {
            state.set(new AppState(newStatus, newActiveJobs));
        }

        public AppState getCurrentState() {
            return state.get();
        }
    }

    public static void main(String[] args) throws Exception {
        // Exercise 1
        List<String> mutableRoles = new ArrayList<>();
        mutableRoles.add("ROLE_USER");

        ImmutableUserProfile profile = new ImmutableUserProfile("alice", 28, mutableRoles);
        mutableRoles.add("ROLE_ADMIN");

        assert profile.roles().size() == 1 : "Defensive copy failed!";
        assert !profile.roles().contains("ROLE_ADMIN") : "Profile was modified from outside!";
        System.out.println("Exercise 1 passed!");

        // Exercise 2
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

        System.out.println("All Unit 2.1 Solutions Verified Successfully!");
    }
}
