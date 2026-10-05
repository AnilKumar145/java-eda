import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Unit 2.2 Exercises: Java Concurrency Utilities - Solutions
 */
public class JavaConcurrencyUtilitiesSolutions {

    public static boolean safeDualLock(ReentrantLock first, ReentrantLock second, long timeoutMs)
            throws InterruptedException {
        if (first.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
            try {
                if (second.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                    try {
                        return true;
                    } finally {
                        second.unlock();
                    }
                }
            } finally {
                first.unlock();
            }
        }
        return false;
    }

    public static class BoundedThrottler {
        private final Semaphore semaphore;

        public BoundedThrottler(int maxConcurrency) {
            this.semaphore = new Semaphore(maxConcurrency);
        }

        public boolean executeThrottled(Runnable task, long timeoutMs) throws InterruptedException {
            if (semaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
                try {
                    task.run();
                    return true;
                } finally {
                    semaphore.release();
                }
            }
            return false;
        }
    }

    public static void runAndAwaitWorkers(int workerCount, AtomicInteger counter) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(workerCount);
        for (int i = 0; i < workerCount; i++) {
            new Thread(() -> {
                counter.incrementAndGet();
                latch.countDown();
            }).start();
        }
        latch.await();
    }

    public static void main(String[] args) throws Exception {
        // Exercise 1
        ReentrantLock l1 = new ReentrantLock();
        ReentrantLock l2 = new ReentrantLock();
        assert safeDualLock(l1, l2, 100) : "Should acquire both uncontested locks";
        assert !l1.isLocked() && !l2.isLocked() : "Locks should be released after execution";
        System.out.println("Exercise 1 passed!");

        // Exercise 2
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

        // Exercise 3
        AtomicInteger counter = new AtomicInteger(0);
        runAndAwaitWorkers(5, counter);
        assert counter.get() == 5 : "Expected 5, got " + counter.get();
        System.out.println("Exercise 3 passed!");

        System.out.println("All Unit 2.2 Solutions Verified Successfully!");
    }
}
