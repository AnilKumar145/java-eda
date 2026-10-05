import java.util.concurrent.atomic.AtomicLong;

/**
 * Verification Tests for Lab 1 Easy: ICU Watchdog Telemetry Supervisor (Java)
 */
public class LabTests {

    public static void main(String[] args) throws Exception {
        testSuperviseHealthyPoller();
        testSuperviseStalledPoller();
        testHeartbeatDaemon();
        System.out.println("All Lab 1 Easy Tests Passed!");
    }

    public static void testSuperviseHealthyPoller() throws Exception {
        Solution.ICUWatchdogSupervisor supervisor = new Solution.ICUWatchdogSupervisor();

        Thread healthy = new Thread(() -> {
            try { Thread.sleep(40); } catch (InterruptedException ignored) {}
        });
        healthy.start();

        String status = supervisor.supervisePoller(healthy, 200);
        assert Solution.ICUWatchdogSupervisor.STATUS_OK.equals(status) : "Expected STATUS_OK, got " + status;
        System.out.println("Test 1 passed! Healthy poller supervised successfully.");
    }

    public static void testSuperviseStalledPoller() throws Exception {
        Solution.ICUWatchdogSupervisor supervisor = new Solution.ICUWatchdogSupervisor();

        Thread stalled = new Thread(() -> {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        });
        stalled.start();

        String status = supervisor.supervisePoller(stalled, 80);
        assert Solution.ICUWatchdogSupervisor.STATUS_TIMED_OUT.equals(status) : "Expected STATUS_TIMED_OUT, got " + status;
        stalled.join(300);
        assert !stalled.isAlive() : "Stalled poller should have been interrupted";
        System.out.println("Test 2 passed! Stalled poller timed out and was interrupted.");
    }

    public static void testHeartbeatDaemon() throws Exception {
        Solution.ICUWatchdogSupervisor supervisor = new Solution.ICUWatchdogSupervisor();
        AtomicLong pings = new AtomicLong(0);

        Thread daemon = supervisor.startHeartbeatDaemon(pings, 30);
        assert daemon.isDaemon() : "Watchdog must be configured as a daemon";
        Thread.sleep(100);
        assert pings.get() >= 2 : "Daemon should have pulsed multiple pings, got " + pings.get();
        daemon.interrupt();
        System.out.println("Test 3 passed! Daemon heartbeat active.");
    }
}
