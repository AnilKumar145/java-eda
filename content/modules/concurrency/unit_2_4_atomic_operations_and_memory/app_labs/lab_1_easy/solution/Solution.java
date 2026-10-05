package unit_2_4_atomic_operations_and_memory.app_labs.lab_1_easy.solution;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Solution {

    public enum TriageStatus { STABLE, ELEVATED, CRITICAL, DISCHARGED }

    public static class PatientVitalTracker {
        private final AtomicInteger currentHeartRate;
        private final AtomicInteger peakHeartRate;
        private final AtomicReference<TriageStatus> status;
        private volatile boolean streamingActive;

        public PatientVitalTracker(int initialHeartRate, TriageStatus initialStatus) {
            this.currentHeartRate = new AtomicInteger(initialHeartRate);
            this.peakHeartRate = new AtomicInteger(initialHeartRate);
            this.status = new AtomicReference<>(initialStatus);
            this.streamingActive = false;
        }

        public void recordHeartRate(int bpm) {
            currentHeartRate.set(bpm);
            peakHeartRate.accumulateAndGet(bpm, Math::max);
        }

        public int getCurrentHeartRate() {
            return currentHeartRate.get();
        }

        public int getPeakHeartRate() {
            return peakHeartRate.get();
        }

        public boolean transitionStatus(TriageStatus expected, TriageStatus next) {
            return status.compareAndSet(expected, next);
        }

        public TriageStatus getStatus() {
            return status.get();
        }

        public void startStreaming() {
            streamingActive = true;
        }

        public void stopStreaming() {
            streamingActive = false;
        }

        public boolean isStreamingActive() {
            return streamingActive;
        }
    }
}
