import java.util.concurrent.atomic.AtomicLong;

/**
 * ICU Watchdog Telemetry Supervisor - Starter Code
 */
public class StarterCode {

    public static class ICUWatchdogSupervisor {
        public static final String STATUS_OK = "STATUS_OK";
        public static final String STATUS_TIMED_OUT = "STATUS_TIMED_OUT";

        public String supervisePoller(Thread pollerThread, long deadlineMs) throws InterruptedException {
            // TODO: Join with deadline, interrupt if alive, return status
            return null;
        }

        public Thread startHeartbeatDaemon(AtomicLong pingCounter, long intervalMs) {
            // TODO: Start and return a daemon thread updating pingCounter
            return null;
        }
    }
}
