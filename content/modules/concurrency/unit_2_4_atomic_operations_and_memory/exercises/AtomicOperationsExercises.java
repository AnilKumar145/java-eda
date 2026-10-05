package unit_2_4_atomic_operations_and_memory.exercises;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicOperationsExercises {

    // Exercise 1: Lock-Free Inventory Decrementer
    public static class LockFreeInventory {
        private final AtomicInteger stock;

        public LockFreeInventory(int initialStock) {
            this.stock = new AtomicInteger(initialStock);
        }

        public boolean reserveStock(int quantity) {
            // TODO: Atomically deduct quantity only if stock >= quantity using CAS loop
            return false;
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
            // TODO: Transition from INITIALIZING to READY using compareAndSet
            return false;
        }

        public boolean markFailed() {
            // TODO: Transition from any current state to FAILED using getAndSet or CAS
            return false;
        }

        public ServiceState getState() {
            return state.get();
        }
    }

    // Exercise 3: Volatile Graceful Worker
    public static class GracefulWorker implements Runnable {
        // TODO: Declare volatile boolean running
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
            // TODO: Set running to false
            running = false;
        }

        public int getIterations() {
            return iterations;
        }
    }
}
