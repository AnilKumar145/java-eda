import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Unit 1.2 Exercises: Java Thread Management - Solutions
 */
public class ThreadManagementSolutions {

    public static Thread startInterruptibleCounter(AtomicInteger counter) {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                counter.incrementAndGet();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                    break;
                }
            }
        });
        thread.start();
        return thread;
    }

    public static Thread startDaemonWatchdog(String name, AtomicLong lastPingTimestamp) {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lastPingTimestamp.set(System.currentTimeMillis());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, name);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    public static boolean waitWithTimeout(Thread worker, long timeoutMs) throws InterruptedException {
        worker.join(timeoutMs);
        if (worker.isAlive()) {
            worker.interrupt();
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        // Exercise 1
        AtomicInteger counter = new AtomicInteger(0);
        Thread t = startInterruptibleCounter(counter);
        assert t != null : "Thread must not be null";
        Thread.sleep(100);
        t.interrupt();
        t.join(500);
        assert !t.isAlive() : "Thread must have terminated after interruption";
        assert counter.get() >= 3 : "Counter should have incremented multiple times, got " + counter.get();
        System.out.println("Exercise 1 passed!");

        // Exercise 2
        AtomicLong ping = new AtomicLong(0);
        Thread daemon = startDaemonWatchdog("AuditWatchdog", ping);
        assert daemon != null : "Daemon thread must not be null";
        assert daemon.isDaemon() : "Thread must be configured as a daemon";
        assert "AuditWatchdog".equals(daemon.getName()) : "Thread name mismatch";
        Thread.sleep(120);
        assert ping.get() > 0 : "Watchdog ping was not updated";
        daemon.interrupt();
        System.out.println("Exercise 2 passed!");

        // Exercise 3
        Thread fastWorker = new Thread(() -> {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        });
        fastWorker.start();
        assert waitWithTimeout(fastWorker, 300) : "Fast worker should have finished within deadline";

        Thread slowWorker = new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        });
        slowWorker.start();
        assert !waitWithTimeout(slowWorker, 100) : "Slow worker should have timed out";
        slowWorker.join(300);
        System.out.println("Exercise 3 passed!");

        System.out.println("All Unit 1.2 Solutions Verified Successfully!");
    }
}
