package unit_2_3_concurrent_collections.app_labs.lab_1_easy.solution;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;

public class Solution {

    public record Incident(String id, String wardId, String severity, String description) {}

    @FunctionalInterface
    public interface IncidentObserver {
        void onIncidentLogged(Incident incident);
    }

    public static class IncidentAuditStream {
        private final BlockingQueue<Incident> queue;
        private final ConcurrentHashMap<String, Integer> wardCounts;
        private final CopyOnWriteArrayList<IncidentObserver> observers;

        public IncidentAuditStream(int queueCapacity) {
            this.queue = new ArrayBlockingQueue<>(queueCapacity);
            this.wardCounts = new ConcurrentHashMap<>();
            this.observers = new CopyOnWriteArrayList<>();
        }

        public boolean submitIncident(Incident incident, long timeoutMs) throws InterruptedException {
            return queue.offer(incident, timeoutMs, TimeUnit.MILLISECONDS);
        }

        public Incident pollIncident(long timeoutMs) throws InterruptedException {
            return queue.poll(timeoutMs, TimeUnit.MILLISECONDS);
        }

        public void processIncident(Incident incident) {
            wardCounts.merge(incident.wardId(), 1, Integer::sum);
            for (IncidentObserver obs : observers) {
                obs.onIncidentLogged(incident);
            }
        }

        public int getWardCount(String wardId) {
            return wardCounts.getOrDefault(wardId, 0);
        }

        public Map<String, Integer> getAllWardCounts() {
            return Collections.unmodifiableMap(wardCounts);
        }

        public void registerObserver(IncidentObserver observer) {
            observers.add(observer);
        }
    }
}
