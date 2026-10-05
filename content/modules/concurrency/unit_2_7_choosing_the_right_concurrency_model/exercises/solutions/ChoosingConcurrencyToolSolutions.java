package unit_2_7_choosing_the_right_concurrency_model.exercises.solutions;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class ChoosingConcurrencyToolSolutions {

    // Exercise 1: Concurrency Tool Advisor
    public enum ConcurrencyGoal {
        SIMPLE_FLAG_VISIBILITY,
        HIGH_FREQUENCY_COUNTER,
        CONCURRENT_KEY_VALUE_CACHE,
        CPU_BOUND_RECURSIVE_CALCULATION,
        MASS_CONCURRENT_BLOCKING_IO
    }

    public static class ToolAdvisor {
        public static String recommend(ConcurrencyGoal goal) {
            return switch (goal) {
                case SIMPLE_FLAG_VISIBILITY -> "volatile";
                case HIGH_FREQUENCY_COUNTER -> "AtomicLong";
                case CONCURRENT_KEY_VALUE_CACHE -> "ConcurrentHashMap";
                case CPU_BOUND_RECURSIVE_CALCULATION -> "ForkJoinPool";
                case MASS_CONCURRENT_BLOCKING_IO -> "VirtualThreads";
            };
        }
    }

    // Exercise 2: Lock-based vs Atomic Counter
    public static class LockCounter {
        private final ReentrantLock lock = new ReentrantLock();
        private long count = 0;

        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        public long get() {
            return count;
        }
    }

    public static class AtomicCounter {
        private final AtomicLong count = new AtomicLong(0);

        public void increment() {
            count.incrementAndGet();
        }

        public long get() {
            return count.get();
        }
    }

    // Exercise 3: Dispatcher using Virtual Threads
    public static class VirtualThreadDispatcher {
        public static void runTasks(int count, Runnable task) throws Exception {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < count; i++) {
                    executor.submit(task);
                }
            } // Auto-closes and blocks until all virtual threads finish
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running Unit 2.7 Solutions Tests...");

        // Test 1: ToolAdvisor
        assert ToolAdvisor.recommend(ConcurrencyGoal.SIMPLE_FLAG_VISIBILITY).equals("volatile");
        assert ToolAdvisor.recommend(ConcurrencyGoal.HIGH_FREQUENCY_COUNTER).equals("AtomicLong");
        assert ToolAdvisor.recommend(ConcurrencyGoal.CONCURRENT_KEY_VALUE_CACHE).equals("ConcurrentHashMap");
        assert ToolAdvisor.recommend(ConcurrencyGoal.CPU_BOUND_RECURSIVE_CALCULATION).equals("ForkJoinPool");
        assert ToolAdvisor.recommend(ConcurrencyGoal.MASS_CONCURRENT_BLOCKING_IO).equals("VirtualThreads");

        // Test 2: Counters
        LockCounter lc = new LockCounter();
        AtomicCounter ac = new AtomicCounter();
        int runs = 1000;
        for (int i = 0; i < runs; i++) {
            lc.increment();
            ac.increment();
        }
        assert lc.get() == runs;
        assert ac.get() == runs;

        // Test 3: VirtualThreadDispatcher
        AtomicInteger vCounter = new AtomicInteger(0);
        VirtualThreadDispatcher.runTasks(500, () -> {
            try {
                Thread.sleep(10);
                vCounter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        assert vCounter.get() == 500 : "Expected 500 virtual threads completed, got: " + vCounter.get();

        System.out.println("All Unit 2.7 Solutions Tests PASSED!");
    }
}
