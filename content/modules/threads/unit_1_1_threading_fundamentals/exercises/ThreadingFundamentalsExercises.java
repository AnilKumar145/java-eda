import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit 1.1 Exercises: Java Threading Fundamentals
 * Implement the exercise methods to pass all assertion tests.
 */
public class ThreadingFundamentalsExercises {

    // -------------------------------------------------------------------------
    // Exercise 1: Spawn and Join Named Worker Thread
    // -------------------------------------------------------------------------
    /**
     * Spawns a new Thread with the given name that executes the task.
     * Starts the thread, waits for it to finish using join(), and returns the thread.
     */
    public static Thread spawnAndJoin(String threadName, Runnable task) throws InterruptedException {
        // TODO: Create thread with threadName, start it, join it, and return it
        return null;
    }

    // -------------------------------------------------------------------------
    // Exercise 2: Parallel Batch Computation
    // -------------------------------------------------------------------------
    /**
     * Given a list of numbers, create one worker thread per number that multiplies
     * the number by 2 and stores the result in a thread-safe map under key (number).
     * Start all threads, wait for all threads to finish with join(), and return the map.
     */
    public static ConcurrentHashMap<Integer, Integer> parallelDouble(List<Integer> numbers) throws InterruptedException {
        ConcurrentHashMap<Integer, Integer> results = new ConcurrentHashMap<>();
        // TODO: Launch concurrent threads for each number, join all, and return results
        return results;
    }

    // -------------------------------------------------------------------------
    // Exercise 3: Inspect Thread Lifecycle States
    // -------------------------------------------------------------------------
    /**
     * Create a thread that sleeps for 200ms.
     * Verify its state is NEW before start().
     * Start the thread, verify its state is RUNNABLE or TIMED_WAITING.
     * Join the thread, verify its state is TERMINATED.
     * Return true if all state checks succeed.
     */
    public static boolean verifyLifecycle() throws InterruptedException {
        // TODO: Create, inspect NEW, start, inspect RUNNABLE/TIMED_WAITING, join, inspect TERMINATED
        return false;
    }

    // -------------------------------------------------------------------------
    // Verification Tests
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        testExercise1();
        testExercise2();
        testExercise3();
        System.out.println("All Unit 1.1 Exercises Passed Successfully!");
    }

    private static void testExercise1() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        Thread t = spawnAndJoin("CustomWorker-99", () -> counter.addAndGet(42));
        assert t != null : "Thread must not be null";
        assert "CustomWorker-99".equals(t.getName()) : "Thread name mismatch";
        assert counter.get() == 42 : "Task was not executed";
        assert t.getState() == Thread.State.TERMINATED : "Thread must be terminated after join";
        System.out.println("Exercise 1 passed!");
    }

    private static void testExercise2() throws Exception {
        List<Integer> inputs = List.of(2, 4, 6, 8, 10);
        ConcurrentHashMap<Integer, Integer> res = parallelDouble(inputs);
        assert res.size() == 5 : "Expected 5 results, got " + res.size();
        assert res.get(2) == 4 : "2 * 2 should be 4";
        assert res.get(10) == 20 : "10 * 2 should be 20";
        System.out.println("Exercise 2 passed!");
    }

    private static void testExercise3() throws Exception {
        boolean ok = verifyLifecycle();
        assert ok : "Lifecycle verification failed";
        System.out.println("Exercise 3 passed!");
    }
}
