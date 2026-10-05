import java.util.concurrent.atomic.AtomicLong;

/**
 * ICU Watchdog Telemetry Supervisor - Solution
 */
public class Solution {

    public static class ICUWatchdogSupervisor {
        public static final String STATUS_OK = "STATUS_OK";
        public static final String STATUS_TIMED_OUT = "STATUS_TIMED_OUT";

        public String supervisePoller(Thread pollerThread, long deadlineMs) throws InterruptedException {
            pollerThread.join(deadlineMs);
            if (pollerThread.isAlive()) {
                pollerThread.interrupt();
                return STATUS_TIMED_OUT;
            }
            return STATUS_OK;
        }

        public Thread startHeartbeatDaemon(AtomicLong pingCounter, long intervalMs) {
            Thread daemon = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    pingCounter.incrementAndGet();
                    try {
                        Thread.sleep(intervalMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "ICU-Heartbeat-Daemon");

            daemon.setDaemon(true);
            daemon.start();
            return daemon;
        }
    }
}
