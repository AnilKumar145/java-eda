package unit_2_7_choosing_the_right_concurrency_model.exercises;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class ChoosingConcurrencyToolExercises {

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
            // TODO: Return exact tool name recommendation:
            // SIMPLE_FLAG_VISIBILITY -> "volatile"
            // HIGH_FREQUENCY_COUNTER -> "AtomicLong"
            // CONCURRENT_KEY_VALUE_CACHE -> "ConcurrentHashMap"
            // CPU_BOUND_RECURSIVE_CALCULATION -> "ForkJoinPool"
            // MASS_CONCURRENT_BLOCKING_IO -> "VirtualThreads"
            return null;
        }
    }

    // Exercise 2: Lock-based vs Atomic Counter
    public static class LockCounter {
        private final ReentrantLock lock = new ReentrantLock();
        private long count = 0;

        public void increment() {
            // TODO: Use ReentrantLock
        }

        public long get() {
            return count;
        }
    }

    public static class AtomicCounter {
        private final AtomicLong count = new AtomicLong(0);

        public void increment() {
            // TODO: Use AtomicLong
        }

        public long get() {
            return count.get();
        }
    }

    // Exercise 3: Dispatcher using Virtual Threads
    public static class VirtualThreadDispatcher {
        public static void runTasks(int count, Runnable task) throws Exception {
            // TODO: In Java 21, use Executors.newVirtualThreadPerTaskExecutor()
            // to submit 'count' tasks and await completion via try-with-resources
        }
    }
}
