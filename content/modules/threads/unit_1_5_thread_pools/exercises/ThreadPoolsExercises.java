import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Unit 1.5 Exercises: Java Thread Pools & ExecutorService
 * Implement the exercise methods to pass all assertion tests.
 */
public class ThreadPoolsExercises {

    // -------------------------------------------------------------------------
    // Exercise 1: Parallel Batch Square Calculator (Callable & Future)
    // -------------------------------------------------------------------------
    /**
     * Submit each number in inputs as a Callable to executor that returns number * number.
     * Gather all Future results and return the list of calculated integers.
     */
    public static List<Integer> computeSquaresConcurrently(List<Integer> inputs, ExecutorService executor)
            throws Exception {
        // TODO: Submit tasks, collect futures, call get() on each, return results
        return new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Exercise 2: Timed Multi-Service Scatter-Gather
    // -------------------------------------------------------------------------
    /**
     * Submit a task that sleeps for taskDelayMs and returns "DONE".
     * Await its result using future.get(timeoutMs, TimeUnit.MILLISECONDS).
     * If TimeoutException occurs, cancel the future and return "TIMEOUT".
     * If succeeds, return "DONE".
     */
    public static String executeWithTimeout(ExecutorService executor, long taskDelayMs, long timeoutMs) {
        // TODO: Submit task, call get(timeoutMs, TimeUnit.MILLISECONDS), handle TimeoutException
        return null;
    }

    // -------------------------------------------------------------------------
    // Exercise 3: Two-Phase Graceful Shutdown
    // -------------------------------------------------------------------------
    /**
     * Shut down pool cleanly:
     * 1. Call pool.shutdown().
     * 2. Wait up to timeoutMs for termination via awaitTermination.
     * 3. If not terminated, call pool.shutdownNow().
     */
    public static void shutdownGracefully(ExecutorService pool, long timeoutMs) {
        // TODO: Implement two-phase shutdown
    }

    // -------------------------------------------------------------------------
    // Verification Tests
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        testExercise1();
        testExercise2();
        testExercise3();
        System.out.println("All Unit 1.5 Exercises Passed Successfully!");
    }

    private static void testExercise1() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            List<Integer> inputs = List.of(2, 4, 6, 8);
            List<Integer> squares = computeSquaresConcurrently(inputs, pool);
            assert squares.equals(List.of(4, 16, 36, 64)) : "Expected [4, 16, 36, 64], got " + squares;
            System.out.println("Exercise 1 passed!");
        } finally {
            pool.shutdown();
        }
    }

    private static void testExercise2() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            String fastResult = executeWithTimeout(pool, 20, 200);
            assert "DONE".equals(fastResult) : "Expected DONE for fast task, got " + fastResult;

            String slowResult = executeWithTimeout(pool, 500, 50);
            assert "TIMEOUT".equals(slowResult) : "Expected TIMEOUT for slow task, got " + slowResult;
            System.out.println("Exercise 2 passed!");
        } finally {
            pool.shutdown();
        }
    }

    private static void testExercise3() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.execute(() -> {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        });

        shutdownGracefully(pool, 200);
        assert pool.isShutdown() : "Pool must be shutdown";
        assert pool.isTerminated() : "Pool must be terminated";
        System.out.println("Exercise 3 passed!");
    }
}
