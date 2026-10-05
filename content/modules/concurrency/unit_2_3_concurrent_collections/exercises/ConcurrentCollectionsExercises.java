import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit 2.3 Exercises: Java Concurrent Collections
 * Implement the exercise methods to pass all assertion tests.
 */
public class ConcurrentCollectionsExercises {

    // -------------------------------------------------------------------------
    // Exercise 1: Atomic Frequency Counter (ConcurrentHashMap)
    // -------------------------------------------------------------------------
    public static void recordFrequency(ConcurrentHashMap<String, Integer> map, String word) {
        // TODO: Atomically increment count for word using merge() or compute()
    }

    // -------------------------------------------------------------------------
    // Exercise 2: Safe Listener Dispatch (CopyOnWriteArrayList)
    // -------------------------------------------------------------------------
    public static class EventDispatcher {
        private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

        public void addListener(Runnable listener) {
            // TODO: Add listener
        }

        public void dispatchAll() {
            // TODO: Iterate and run each listener
        }
    }

    // -------------------------------------------------------------------------
    // Exercise 3: Blocking Pipeline
    // -------------------------------------------------------------------------
    public static int runPipeline(List<Integer> inputs) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
        AtomicInteger totalSum = new AtomicInteger(0);

        // TODO: Start producer enqueuing inputs via put()
        // Start consumer taking inputs via take(), summing into totalSum
        // Join both, return totalSum.get()
        return 0;
    }

    // -------------------------------------------------------------------------
    // Verification Tests
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        testExercise1();
        testExercise2();
        testExercise3();
        System.out.println("All Unit 2.3 Exercises Passed Successfully!");
    }

    private static void testExercise1() throws Exception {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        List<Thread> threads = new java.util.ArrayList<>();

        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    recordFrequency(map, "heart_rate");
                }
            }));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        assert map.get("heart_rate") == 1000 : "Expected 1000, got " + map.get("heart_rate");
        System.out.println("Exercise 1 passed!");
    }

    private static void testExercise2() {
        EventDispatcher dispatcher = new EventDispatcher();
        AtomicInteger count = new AtomicInteger(0);

        dispatcher.addListener(() -> {
            count.incrementAndGet();
            // Mutate list DURING iteration!
            dispatcher.addListener(() -> count.incrementAndGet());
        });

        dispatcher.dispatchAll();
        assert count.get() == 1 : "Expected 1 invocation on initial dispatch";
        System.out.println("Exercise 2 passed!");
    }

    private static void testExercise3() throws Exception {
        int sum = runPipeline(List.of(1, 2, 3, 4, 5));
        assert sum == 15 : "Expected 15, got " + sum;
        System.out.println("Exercise 3 passed!");
    }
}
