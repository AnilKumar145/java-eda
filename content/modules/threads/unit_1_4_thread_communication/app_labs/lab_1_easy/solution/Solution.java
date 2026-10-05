import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Hospital ER Patient Triage Dispatcher - Solution
 */
public class Solution {

    public record TriageCase(int acuityLevel, String patientId, String condition)
            implements Comparable<TriageCase> {
        @Override
        public int compareTo(TriageCase o) {
            return Integer.compare(this.acuityLevel, o.acuityLevel);
        }
    }

    public static class ERTriageDispatcher {
        private final PriorityQueue<TriageCase> queue = new PriorityQueue<>();
        private final Object lock = new Object();
        private boolean shutdown = false;
        private final List<Thread> doctorThreads = new ArrayList<>();
        public final List<String> treatedLog = new ArrayList<>();

        public void admitPatient(TriageCase triageCase) {
            synchronized (lock) {
                queue.add(triageCase);
                lock.notifyAll();
            }
        }

        public void startDoctors(int count) {
            for (int i = 0; i < count; i++) {
                final String docId = "Dr-" + i;
                Thread docThread = new Thread(() -> {
                    while (true) {
                        TriageCase c = null;
                        synchronized (lock) {
                            while (queue.isEmpty() && !shutdown) {
                                try {
                                    lock.wait();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                            }
                            if (queue.isEmpty() && shutdown) {
                                break;
                            }
                            c = queue.poll();
                        }
                        if (c != null) {
                            synchronized (treatedLog) {
                                treatedLog.add(c.patientId() + "-Acuity" + c.acuityLevel());
                            }
                        }
                    }
                }, docId);
                doctorThreads.add(docThread);
                docThread.start();
            }
        }

        public void drainAndShutdown() throws InterruptedException {
            // Wait until queue is completely empty
            while (true) {
                synchronized (lock) {
                    if (queue.isEmpty()) {
                        shutdown = true;
                        lock.notifyAll();
                        break;
                    }
                }
                Thread.sleep(10);
            }

            for (Thread doc : doctorThreads) {
                doc.join();
            }
        }
    }
}
