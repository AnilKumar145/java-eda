import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit 1.3 Exercises: Java Thread Synchronization
 * Implement the exercise methods to pass all assertion tests.
 */
public class ThreadSynchronizationExercises {

    // -------------------------------------------------------------------------
    // Exercise 1: Thread-Safe Bank Vault (Synchronized)
    // -------------------------------------------------------------------------
    public static class BankVault {
        private int balance;
        private final Object lock = new Object();

        public BankVault(int initialBalance) {
            this.balance = initialBalance;
        }

        public void deposit(int amount) {
            // TODO: Synchronize and increment balance
        }

        public boolean withdraw(int amount) {
            // TODO: Synchronize; if balance >= amount, deduct and return true; else false
            return false;
        }

        public int getBalance() {
            // TODO: Synchronize and return balance
            return 0;
        }
    }

    // -------------------------------------------------------------------------
    // Exercise 2: Lock-Free Request Metrics (AtomicInteger)
    // -------------------------------------------------------------------------
    public static class RequestMetrics {
        private final AtomicInteger totalRequests = new AtomicInteger(0);

        public void recordRequest() {
            // TODO: Lock-free atomic increment
        }

        public int getTotalRequests() {
            // TODO: Return current atomic value
            return 0;
        }
    }

    // -------------------------------------------------------------------------
    // Exercise 3: Volatile Graceful Stopper
    // -------------------------------------------------------------------------
    public static class StoppableWorker implements Runnable {
        // TODO: Declare running flag with volatile
        private boolean running = true;
        private int processedItems = 0;

        public void stopWorker() {
            // TODO: Set running to false
        }

        public int getProcessedItems() {
            return processedItems;
        }

        @Override
        public void run() {
            while (running) {
                processedItems++;
                try { Thread.sleep(5); } catch (InterruptedException ignored) {}
            }
        }
    }

    // -------------------------------------------------------------------------
    // Verification Tests
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        testExercise1();
        testExercise2();
        testExercise3();
        System.out.println("All Unit 1.3 Exercises Passed Successfully!");
    }

    private static void testExercise1() throws Exception {
        BankVault vault = new BankVault(50000);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < 100; j++) vault.deposit(10);
            }));
            threads.add(new Thread(() -> {
                for (int j = 0; j < 100; j++) vault.withdraw(10);
            }));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        assert vault.getBalance() == 50000 : "Expected 50000 balance, got " + vault.getBalance();
        System.out.println("Exercise 1 passed!");
    }

    private static void testExercise2() throws Exception {
        RequestMetrics metrics = new RequestMetrics();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < 500; j++) metrics.recordRequest();
            }));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        assert metrics.getTotalRequests() == 10000 : "Expected 10000, got " + metrics.getTotalRequests();
        System.out.println("Exercise 2 passed!");
    }

    private static void testExercise3() throws Exception {
        StoppableWorker worker = new StoppableWorker();
        Thread t = new Thread(worker);
        t.start();

        Thread.sleep(50);
        worker.stopWorker();
        t.join(500);

        assert !t.isAlive() : "Worker should have stopped when flag set to false";
        assert worker.getProcessedItems() > 0 : "Worker should have processed items";
        System.out.println("Exercise 3 passed!");
    }
}
