package unit_2_7_choosing_the_right_concurrency_model.app_labs.lab_1_easy.solution;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Solution {

    public interface TelemetryCounter {
        void recordSample();
        long getCount();
    }

    public record BenchmarkResult(String strategyName, long totalCount, long elapsedMs) {}

    public static class SynchronizedCounter implements TelemetryCounter {
        private long count = 0;

        @Override
        public synchronized void recordSample() {
            count++;
        }

        @Override
        public synchronized long getCount() {
            return count;
        }
    }

    public static class ReentrantLockCounter implements TelemetryCounter {
        private final Lock lock = new ReentrantLock();
        private long count = 0;

        @Override
        public void recordSample() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public long getCount() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    public static class AtomicCounter implements TelemetryCounter {
        private final AtomicLong count = new AtomicLong(0);

        @Override
        public void recordSample() {
            count.incrementAndGet();
        }

        @Override
        public long getCount() {
            return count.get();
        }
    }

    public static class BenchmarkRunner {
        public static BenchmarkResult runBenchmark(
                TelemetryCounter counter, 
                String name, 
                int threads, 
                int incrementsPerThread) throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            CountDownLatch latch = new CountDownLatch(threads);

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    try {
                        for (int j = 0; j < incrementsPerThread; j++) {
                            counter.recordSample();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(10, TimeUnit.SECONDS);
            long elapsed = System.currentTimeMillis() - startTime;
            pool.shutdown();

            return new BenchmarkResult(name, counter.getCount(), elapsed);
        }
    }
}
