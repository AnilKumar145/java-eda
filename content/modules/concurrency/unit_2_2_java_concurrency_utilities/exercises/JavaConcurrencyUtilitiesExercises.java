import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Unit 2.2 Exercises: Java Concurrency Utilities
 * Implement the exercise methods to pass all assertion tests.
 */
public class JavaConcurrencyUtilitiesExercises {

    // -------------------------------------------------------------------------
    // Exercise 1: Deadlock-Free Timed Lock (ReentrantLock)
    // -------------------------------------------------------------------------
    public static boolean safeDualLock(ReentrantLock first, ReentrantLock second, long timeoutMs)
            throws InterruptedException {
        // TODO: Acquire first lock with tryLock(timeoutMs).
        // If acquired, try second lock with tryLock(timeoutMs).
        // If both acquired, unlock second in finally, unlock first in finally, return true.
        // If second fails, unlock first, return false.
        return false;
    }

    // -------------------------------------------------------------------------
    // Exercise 2: Bounded Resource Throttler (Semaphore)
    // -------------------------------------------------------------------------
    public static class BoundedThrottler {
        private final Semaphore semaphore;

        public BoundedThrottler(int maxConcurrency) {
            this.semaphore = new Semaphore(maxConcurrency);
        }

        public boolean executeThrottled(Runnable task, long timeoutMs) throws InterruptedException {
            // TODO: Acquire permit within timeoutMs, execute task in try/finally, release permit, return true.
            // If timed out, return false.
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Exercise 3: Parallel Rendezvous (CountDownLatch)
    // -------------------------------------------------------------------------
    public static void runAndAwaitWorkers(int workerCount, AtomicInteger counter) throws InterruptedException {
        // TODO: Create CountDownLatch(workerCount), launch workerCount threads that increment counter and countDown(),
        // await latch in main thread.
    }

    // -------------------------------------------------------------------------
    // Verification Tests
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        testExercise1();
        testExercise2();
        testExercise3();
        System.out.println("All Unit 2.2 Exercises Passed Successfully!");
    }

    private static void testExercise1() throws Exception {
        ReentrantLock l1 = new ReentrantLock();
        ReentrantLock l2 = new ReentrantLock();

        assert safeDualLock(l1, l2, 100) : "Should acquire both uncontested locks";
        assert !l1.isLocked() && !l2.isLocked() : "Locks should be released after execution";
        System.out.println("Exercise 1 passed!");
    }

    private static void testExercise2() throws Exception {
        BoundedThrottler throttler = new BoundedThrottler(2);
        AtomicInteger activeCount = new AtomicInteger(0);
        AtomicInteger maxObserved = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    throttler.executeThrottled(() -> {
                        int cur = activeCount.incrementAndGet();
                        maxObserved.updateAndGet(m -> Math.max(m, cur));
                        try { Thread.sleep(30); } catch (InterruptedException ignored) {}
                        activeCount.decrementAndGet();
                    }, 200);
                } catch (InterruptedException ignored) {}
            }).start();
        }

        Thread.sleep(250);
        assert maxObserved.get() <= 2 : "Throttle violated! Max concurrent was " + maxObserved.get();
        System.out.println("Exercise 2 passed!");
    }

    private static void testExercise3() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        runAndAwaitWorkers(5, counter);
        assert counter.get() == 5 : "Expected 5, got " + counter.get();
        System.out.println("Exercise 3 passed!");
    }
}
