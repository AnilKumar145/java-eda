import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit 1.3 Exercises: Java Thread Synchronization - Solutions
 */
public class ThreadSynchronizationSolutions {

    public static class BankVault {
        private int balance;
        private final Object lock = new Object();

        public BankVault(int initialBalance) {
            this.balance = initialBalance;
        }

        public void deposit(int amount) {
            synchronized (lock) {
                this.balance += amount;
            }
        }

        public boolean withdraw(int amount) {
            synchronized (lock) {
                if (this.balance >= amount) {
                    this.balance -= amount;
                    return true;
                }
                return false;
            }
        }

        public int getBalance() {
            synchronized (lock) {
                return this.balance;
            }
        }
    }

    public static class RequestMetrics {
        private final AtomicInteger totalRequests = new AtomicInteger(0);

        public void recordRequest() {
            totalRequests.incrementAndGet();
        }

        public int getTotalRequests() {
            return totalRequests.get();
        }
    }

    public static class StoppableWorker implements Runnable {
        private volatile boolean running = true;
        private int processedItems = 0;

        public void stopWorker() {
            this.running = false;
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

    public static void main(String[] args) throws Exception {
        // Test 1: BankVault
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

        // Test 2: RequestMetrics
        RequestMetrics metrics = new RequestMetrics();
        List<Thread> metricThreads = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            metricThreads.add(new Thread(() -> {
                for (int j = 0; j < 500; j++) metrics.recordRequest();
            }));
        }

        for (Thread t : metricThreads) t.start();
        for (Thread t : metricThreads) t.join();

        assert metrics.getTotalRequests() == 10000 : "Expected 10000, got " + metrics.getTotalRequests();
        System.out.println("Exercise 2 passed!");

        // Test 3: StoppableWorker
        StoppableWorker worker = new StoppableWorker();
        Thread t = new Thread(worker);
        t.start();

        Thread.sleep(50);
        worker.stopWorker();
        t.join(500);

        assert !t.isAlive() : "Worker should have stopped when flag set to false";
        assert worker.getProcessedItems() > 0 : "Worker should have processed items";
        System.out.println("Exercise 3 passed!");

        System.out.println("All Unit 1.3 Solutions Verified Successfully!");
    }
}
