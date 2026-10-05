import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit 2.3 Exercises: Java Concurrent Collections - Solutions
 */
public class ConcurrentCollectionsSolutions {

    public static void recordFrequency(ConcurrentHashMap<String, Integer> map, String word) {
        map.merge(word, 1, Integer::sum);
    }

    public static class EventDispatcher {
        private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

        public void addListener(Runnable listener) {
            listeners.add(listener);
        }

        public void dispatchAll() {
            for (Runnable listener : listeners) {
                listener.run();
            }
        }
    }

    public static int runPipeline(List<Integer> inputs) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
        AtomicInteger totalSum = new AtomicInteger(0);

        Thread producer = new Thread(() -> {
            try {
                for (Integer n : inputs) {
                    queue.put(n);
                }
            } catch (InterruptedException ignored) {}
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < inputs.size(); i++) {
                    totalSum.addAndGet(queue.take());
                }
            } catch (InterruptedException ignored) {}
        });

        consumer.start();
        producer.start();

        producer.join();
        consumer.join();

        return totalSum.get();
    }

    public static void main(String[] args) throws Exception {
        // Exercise 1
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

        // Exercise 2
        EventDispatcher dispatcher = new EventDispatcher();
        AtomicInteger count = new AtomicInteger(0);

        dispatcher.addListener(() -> {
            count.incrementAndGet();
            dispatcher.addListener(() -> count.incrementAndGet());
        });

        dispatcher.dispatchAll();
        assert count.get() == 1 : "Expected 1 invocation on initial dispatch";
        System.out.println("Exercise 2 passed!");

        // Exercise 3
        int sum = runPipeline(List.of(1, 2, 3, 4, 5));
        assert sum == 15 : "Expected 15, got " + sum;
        System.out.println("Exercise 3 passed!");

        System.out.println("All Unit 2.3 Solutions Verified Successfully!");
    }
}
