package unit_2_3_concurrent_collections.app_labs.lab_1_easy;

import java.util.Map;
import java.util.concurrent.*;

public class StarterCode {

    public record Incident(String id, String wardId, String severity, String description) {}

    @FunctionalInterface
    public interface IncidentObserver {
        void onIncidentLogged(Incident incident);
    }

    public static class IncidentAuditStream {
        // TODO: Declare BlockingQueue<Incident>
        // TODO: Declare ConcurrentHashMap<String, Integer> for ward incident counts
        // TODO: Declare CopyOnWriteArrayList<IncidentObserver> for observers

        public IncidentAuditStream(int queueCapacity) {
            // TODO: Initialize concurrent data structures
        }

        public boolean submitIncident(Incident incident, long timeoutMs) throws InterruptedException {
            // TODO: Offer incident to queue within timeoutMs
            return false;
        }

        public Incident pollIncident(long timeoutMs) throws InterruptedException {
            // TODO: Poll incident from queue within timeoutMs
            return null;
        }

        public void processIncident(Incident incident) {
            // TODO: Atomically increment ward count using ConcurrentHashMap.merge
            // TODO: Notify all registered observers
        }

        public int getWardCount(String wardId) {
            // TODO: Return count for ward or 0
            return 0;
        }

        public Map<String, Integer> getAllWardCounts() {
            // TODO: Return ward count map
            return null;
        }

        public void registerObserver(IncidentObserver observer) {
            // TODO: Add observer to copy-on-write list
        }
    }
}
