import java.util.ArrayList;
import java.util.List;

/**
 * Hospital ER Patient Triage Dispatcher - Starter Code
 */
public class StarterCode {

    public record TriageCase(int acuityLevel, String patientId, String condition)
            implements Comparable<TriageCase> {
        @Override
        public int compareTo(TriageCase o) {
            return Integer.compare(this.acuityLevel, o.acuityLevel);
        }
    }

    public static class ERTriageDispatcher {
        public List<String> treatedLog = new ArrayList<>();

        public void admitPatient(TriageCase triageCase) {
            // TODO: Synchronize, add to priority queue, notifyAll()
        }

        public void startDoctors(int count) {
            // TODO: Launch count doctor worker threads
        }

        public void drainAndShutdown() throws InterruptedException {
            // TODO: Wait for queue to empty, signal termination, join doctor threads
        }
    }
}
