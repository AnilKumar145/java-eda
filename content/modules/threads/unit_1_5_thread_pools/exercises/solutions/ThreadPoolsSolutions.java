import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Unit 1.5 Exercises: Java Thread Pools & ExecutorService - Solutions
 */
public class ThreadPoolsSolutions {

    public static List<Integer> computeSquaresConcurrently(List<Integer> inputs, ExecutorService executor)
            throws Exception {
        List<Future<Integer>> futures = new ArrayList<>();
        for (Integer n : inputs) {
            futures.add(executor.submit(() -> n * n));
        }

        List<Integer> results = new ArrayList<>();
        for (Future<Integer> f : futures) {
            results.add(f.get());
        }
        return results;
    }

    public static String executeWithTimeout(ExecutorService executor, long taskDelayMs, long timeoutMs) {
        Future<String> future = executor.submit(() -> {
            Thread.sleep(taskDelayMs);
            return "DONE";
        });

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return "TIMEOUT";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    public static void shutdownGracefully(ExecutorService pool, long timeoutMs) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        // Exercise 1
        ExecutorService pool1 = Executors.newFixedThreadPool(3);
        try {
            List<Integer> inputs = List.of(2, 4, 6, 8);
            List<Integer> squares = computeSquaresConcurrently(inputs, pool1);
            assert squares.equals(List.of(4, 16, 36, 64)) : "Expected [4, 16, 36, 64], got " + squares;
            System.out.println("Exercise 1 passed!");
        } finally {
            pool1.shutdown();
        }

        // Exercise 2
        ExecutorService pool2 = Executors.newFixedThreadPool(2);
        try {
            String fastResult = executeWithTimeout(pool2, 20, 200);
            assert "DONE".equals(fastResult) : "Expected DONE for fast task, got " + fastResult;

            String slowResult = executeWithTimeout(pool2, 500, 50);
            assert "TIMEOUT".equals(slowResult) : "Expected TIMEOUT for slow task, got " + slowResult;
            System.out.println("Exercise 2 passed!");
        } finally {
            pool2.shutdown();
        }

        // Exercise 3
        ExecutorService pool3 = Executors.newFixedThreadPool(2);
        pool3.execute(() -> {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        });

        shutdownGracefully(pool3, 200);
        assert pool3.isShutdown() : "Pool must be shutdown";
        assert pool3.isTerminated() : "Pool must be terminated";
        System.out.println("Exercise 3 passed!");

        System.out.println("All Unit 1.5 Solutions Verified Successfully!");
    }
}
