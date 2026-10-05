package unit_2_4_atomic_operations_and_memory.exercises.solutions;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicOperationsSolutions {

    // Exercise 1: Lock-Free Inventory Decrementer
    public static class LockFreeInventory {
        private final AtomicInteger stock;

        public LockFreeInventory(int initialStock) {
            this.stock = new AtomicInteger(initialStock);
        }

        public boolean reserveStock(int quantity) {
            while (true) {
                int current = stock.get();
                if (current < quantity) {
                    return false; // Insufficient stock
                }
                if (stock.compareAndSet(current, current - quantity)) {
                    return true; // Successfully reserved
                }
            }
        }

        public int getAvailableStock() {
            return stock.get();
        }
    }

    // Exercise 2: State Machine with AtomicReference
    public enum ServiceState { INITIALIZING, READY, FAILED }

    public static class ServiceManager {
        private final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.INITIALIZING);

        public boolean markReady() {
            return state.compareAndSet(ServiceState.INITIALIZING, ServiceState.READY);
        }

        public boolean markFailed() {
            ServiceState prev = state.getAndSet(ServiceState.FAILED);
            return prev != ServiceState.FAILED;
        }

        public ServiceState getState() {
            return state.get();
        }
    }

    // Exercise 3: Volatile Graceful Worker
    public static class GracefulWorker implements Runnable {
        private volatile boolean running = true;
        private int iterations = 0;

        @Override
        public void run() {
            while (running) {
                iterations++;
                Thread.yield();
            }
        }

        public void stop() {
            running = false;
        }

        public int getIterations() {
            return iterations;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running Unit 2.4 Solutions Tests...");

        // Test 1: Inventory CAS
        LockFreeInventory inventory = new LockFreeInventory(100);
        int threads = 10;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                try {
                    for (int j = 0; j < 10; j++) {
                        inventory.reserveStock(1);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(5, TimeUnit.SECONDS);
        exec.shutdown();

        assert inventory.getAvailableStock() == 0 : "Stock should be 0, got " + inventory.getAvailableStock();
        assert !inventory.reserveStock(1) : "Overdraw must be rejected";

        // Test 2: State Machine
        ServiceManager sm = new ServiceManager();
        assert sm.getState() == ServiceState.INITIALIZING;
        assert sm.markReady() : "Should transition to READY";
        assert !sm.markReady() : "Second markReady should fail CAS";
        assert sm.markFailed() : "Should transition to FAILED";
        assert !sm.markFailed() : "Second markFailed should return false";

        // Test 3: Volatile worker
        GracefulWorker worker = new GracefulWorker();
        Thread workerThread = new Thread(worker);
        workerThread.start();
        Thread.sleep(50);
        worker.stop();
        workerThread.join(2000);
        assert !workerThread.isAlive() : "Worker must terminate after flag is set to false";
        assert worker.getIterations() > 0 : "Worker must have run iterations";

        System.out.println("All Unit 2.4 Solutions Tests PASSED!");
    }
}
