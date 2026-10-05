import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit 1.4 Exercises: Java Thread Communication - Solutions
 */
public class ThreadCommunicationSolutions {

    public static class DropBox<T> {
        private T message;
        private boolean empty = true;

        public synchronized void put(T message) throws InterruptedException {
            while (!empty) {
                wait();
            }
            this.message = message;
            this.empty = false;
            notifyAll();
        }

        public synchronized T take() throws InterruptedException {
            while (empty) {
                wait();
            }
            T item = this.message;
            this.empty = true;
            notifyAll();
            return item;
        }
    }

    public static class CustomBoundedQueue<T> {
        private final Queue<T> queue = new LinkedList<>();
        private final int capacity;

        public CustomBoundedQueue(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void enqueue(T item) throws InterruptedException {
            while (queue.size() == capacity) {
                wait();
            }
            queue.add(item);
            notifyAll();
        }

        public synchronized T dequeue() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            T item = queue.poll();
            notifyAll();
            return item;
        }

        public synchronized int size() {
            return queue.size();
        }
    }

    public static class StartGate {
        private boolean open = false;

        public synchronized void awaitOpen() throws InterruptedException {
            while (!open) {
                wait();
            }
        }

        public synchronized void openGate() {
            this.open = true;
            notifyAll();
        }
    }

    public static void main(String[] args) throws Exception {
        // Exercise 1
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

        // Exercise 2
        CustomBoundedQueue<Integer> q = new CustomBoundedQueue<>(2);
        Thread qProducer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) q.enqueue(i);
            } catch (InterruptedException ignored) {}
        });

        AtomicInteger sum = new AtomicInteger(0);
        Thread qConsumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) sum.addAndGet(q.dequeue());
            } catch (InterruptedException ignored) {}
        });

        qConsumer.start();
        qProducer.start();
        qProducer.join();
        qConsumer.join();

        assert sum.get() == 15 : "Expected sum 15, got " + sum.get();
        System.out.println("Exercise 2 passed!");

        // Exercise 3
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

        System.out.println("All Unit 1.4 Solutions Verified Successfully!");
    }
}
