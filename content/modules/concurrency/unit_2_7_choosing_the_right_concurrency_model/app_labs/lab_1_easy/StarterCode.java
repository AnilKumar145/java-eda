package unit_2_7_choosing_the_right_concurrency_model.app_labs.lab_1_easy;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StarterCode {

    public interface TelemetryCounter {
        void recordSample();
        long getCount();
    }

    public record BenchmarkResult(String strategyName, long totalCount, long elapsedMs) {}

    public static class SynchronizedCounter implements TelemetryCounter {
        private long count = 0;

        @Override
        public synchronized void recordSample() {
            // TODO: Increment count synchronously
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
            // TODO: Lock and increment count
        }

        @Override
        public long getCount() {
            // TODO: Lock and read count
            return 0;
        }
    }

    public static class AtomicCounter implements TelemetryCounter {
        private final AtomicLong count = new AtomicLong(0);

        @Override
        public void recordSample() {
            // TODO: Increment AtomicLong
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
            // TODO: Execute concurrent benchmark using ExecutorService and CountDownLatch
            return null;
        }
    }
}
