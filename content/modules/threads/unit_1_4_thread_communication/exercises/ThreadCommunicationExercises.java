import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit 1.4 Exercises: Java Thread Communication
 * Implement the exercise methods to pass all assertion tests.
 */
public class ThreadCommunicationExercises {

    // -------------------------------------------------------------------------
    // Exercise 1: Single-Item Drop Box (Wait / Notify)
    // -------------------------------------------------------------------------
    public static class DropBox<T> {
        private T message;
        private boolean empty = true;

        public synchronized void put(T message) throws InterruptedException {
            // TODO: While not empty, wait(). Then store message, empty = false, notifyAll().
        }

        public synchronized T take() throws InterruptedException {
            // TODO: While empty, wait(). Then take message, empty = true, notifyAll(), return item.
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Exercise 2: Custom Bounded Queue
    // -------------------------------------------------------------------------
    public static class CustomBoundedQueue<T> {
        private final Queue<T> queue = new LinkedList<>();
        private final int capacity;

        public CustomBoundedQueue(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void enqueue(T item) throws InterruptedException {
            // TODO: While queue.size() == capacity, wait(). Add item, notifyAll().
        }

        public synchronized T dequeue() throws InterruptedException {
            // TODO: While queue.isEmpty(), wait(). Poll item, notifyAll(), return item.
            return null;
        }

        public synchronized int size() {
            return queue.size();
        }
    }

    // -------------------------------------------------------------------------
    // Exercise 3: Start Gate (Barrier)
    // -------------------------------------------------------------------------
    public static class StartGate {
        private boolean open = false;

        public synchronized void awaitOpen() throws InterruptedException {
            // TODO: While !open, wait().
        }

        public synchronized void openGate() {
            // TODO: open = true, notifyAll().
        }
    }

    // -------------------------------------------------------------------------
    // Verification Tests
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        testExercise1();
        testExercise2();
        testExercise3();
        System.out.println("All Unit 1.4 Exercises Passed Successfully!");
    }

    private static void testExercise1() throws Exception {
        DropBox<String> box = new DropBox<>();
        Thread producer = new Thread(() -> {
            try {
                box.put("Msg-1");
                box.put("Msg-2");
            } catch (InterruptedException ignored) {}
        });

        Thread consumer = new Thread(() -> {
            try {
                assert "Msg-1".equals(box.take());
                assert "Msg-2".equals(box.take());
            } catch (InterruptedException ignored) {}
        });

        consumer.start();
        producer.start();
        producer.join();
        consumer.join();
        System.out.println("Exercise 1 passed!");
    }

    private static void testExercise2() throws Exception {
        CustomBoundedQueue<Integer> q = new CustomBoundedQueue<>(2);
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) q.enqueue(i);
            } catch (InterruptedException ignored) {}
        });

        AtomicInteger sum = new AtomicInteger(0);
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) sum.addAndGet(q.dequeue());
            } catch (InterruptedException ignored) {}
        });

        consumer.start();
        producer.start();
        producer.join();
        consumer.join();

        assert sum.get() == 15 : "Expected sum 15, got " + sum.get();
        System.out.println("Exercise 2 passed!");
    }

    private static void testExercise3() throws Exception {
        StartGate gate = new StartGate();
        AtomicInteger runnersPassed = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    gate.awaitOpen();
                    runnersPassed.incrementAndGet();
                } catch (InterruptedException ignored) {}
            }).start();
        }

        Thread.sleep(100);
        assert runnersPassed.get() == 0 : "No runners should pass before gate is open";

        gate.openGate();
        Thread.sleep(100);
        assert runnersPassed.get() == 5 : "All 5 runners should have passed";
        System.out.println("Exercise 3 passed!");
    }
}
