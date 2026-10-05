import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Unit 1.2 Exercises: Java Thread Management
 * Implement the exercise methods to pass all assertion tests.
 */
public class ThreadManagementExercises {

    // -------------------------------------------------------------------------
    // Exercise 1: Cooperative Interruptible Counter
    // -------------------------------------------------------------------------
    /**
     * Start a thread that loops, incrementing counter by 1 every 20ms.
     * When interrupted (either via isInterrupted() or InterruptedException),
     * it must exit the loop, restore interrupt status, and terminate.
     * Return the running thread.
     */
    public static Thread startInterruptibleCounter(AtomicInteger counter) {
        // TODO: Create, start, and return a thread that increments counter every 20ms until interrupted
        return null;
    }

    // -------------------------------------------------------------------------
    // Exercise 2: Daemon Watchdog Ping Engine
    // -------------------------------------------------------------------------
    /**
     * Create and return a daemon thread (setDaemon(true)) with the given name
     * that continuously updates lastPingTimestamp with System.currentTimeMillis()
     * every 50ms while running.
     * Start the thread before returning.
     */
    public static Thread startDaemonWatchdog(String name, AtomicLong lastPingTimestamp) {
        // TODO: Create daemon thread, setDaemon(true), start it, and return it
        return null;
    }

    // -------------------------------------------------------------------------
    // Exercise 3: Timed Join with Deadline Watchdog
    // -------------------------------------------------------------------------
    /**
     * Wait for the given worker thread to complete using join(timeoutMs).
     * If worker is still alive after timeoutMs, interrupt the worker and return false.
     * If worker finished within timeoutMs, return true.
     */
    public static boolean waitWithTimeout(Thread worker, long timeoutMs) throws InterruptedException {
        // TODO: Call worker.join(timeoutMs); if still alive, interrupt and return false, else true
        return false;
    }

    // -------------------------------------------------------------------------
    // Verification Tests
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        testExercise1();
        testExercise2();
        testExercise3();
        System.out.println("All Unit 1.2 Exercises Passed Successfully!");
    }

    private static void testExercise1() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        Thread t = startInterruptibleCounter(counter);
        assert t != null : "Thread must not be null";
        Thread.sleep(100);
        t.interrupt();
        t.join(500);
        assert !t.isAlive() : "Thread must have terminated after interruption";
        assert counter.get() >= 3 : "Counter should have incremented multiple times, got " + counter.get();
        System.out.println("Exercise 1 passed!");
    }

    private static void testExercise2() throws Exception {
        AtomicLong ping = new AtomicLong(0);
        Thread daemon = startDaemonWatchdog("AuditWatchdog", ping);
        assert daemon != null : "Daemon thread must not be null";
        assert daemon.isDaemon() : "Thread must be configured as a daemon";
        assert "AuditWatchdog".equals(daemon.getName()) : "Thread name mismatch";
        Thread.sleep(120);
        assert ping.get() > 0 : "Watchdog ping was not updated";
        daemon.interrupt();
        System.out.println("Exercise 2 passed!");
    }

    private static void testExercise3() throws Exception {
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
    }
}
