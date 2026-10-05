import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit 1.1 Exercises: Java Threading Fundamentals - Solutions
 */
public class ThreadingFundamentalsSolutions {

    public static Thread spawnAndJoin(String threadName, Runnable task) throws InterruptedException {
        Thread thread = new Thread(task, threadName);
        thread.start();
        thread.join();
        return thread;
    }

    public static ConcurrentHashMap<Integer, Integer> parallelDouble(List<Integer> numbers) throws InterruptedException {
        ConcurrentHashMap<Integer, Integer> results = new ConcurrentHashMap<>();
        List<Thread> threads = new ArrayList<>();

        for (Integer num : numbers) {
            Thread t = new Thread(() -> results.put(num, num * 2));
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        return results;
    }

    public static boolean verifyLifecycle() throws InterruptedException {
        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {}
        });

        if (worker.getState() != Thread.State.NEW) {
            return false;
        }

        worker.start();
        Thread.State stateAfterStart = worker.getState();
        if (stateAfterStart != Thread.State.RUNNABLE && stateAfterStart != Thread.State.TIMED_WAITING) {
            return false;
        }

        worker.join();
        return worker.getState() == Thread.State.TERMINATED;
    }

    public static void main(String[] args) throws Exception {
        // Exercise 1
        AtomicInteger counter = new AtomicInteger(0);
        Thread t = spawnAndJoin("CustomWorker-99", () -> counter.addAndGet(42));
        assert t != null : "Thread must not be null";
        assert "CustomWorker-99".equals(t.getName()) : "Thread name mismatch";
        assert counter.get() == 42 : "Task was not executed";
        assert t.getState() == Thread.State.TERMINATED : "Thread must be terminated after join";
        System.out.println("Exercise 1 passed!");

        // Exercise 2
        List<Integer> inputs = List.of(2, 4, 6, 8, 10);
        ConcurrentHashMap<Integer, Integer> res = parallelDouble(inputs);
        assert res.size() == 5 : "Expected 5 results, got " + res.size();
        assert res.get(2) == 4 : "2 * 2 should be 4";
        assert res.get(10) == 20 : "10 * 2 should be 20";
        System.out.println("Exercise 2 passed!");

        // Exercise 3
        boolean ok = verifyLifecycle();
        assert ok : "Lifecycle verification failed";
        System.out.println("Exercise 3 passed!");

        System.out.println("All Unit 1.1 Solutions Verified Successfully!");
    }
}
