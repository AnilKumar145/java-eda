package unit_2_4_atomic_operations_and_memory.app_labs.lab_1_easy;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class StarterCode {

    public enum TriageStatus { STABLE, ELEVATED, CRITICAL, DISCHARGED }

    public static class PatientVitalTracker {
        // TODO: Declare AtomicInteger currentHeartRate
        // TODO: Declare AtomicInteger peakHeartRate
        // TODO: Declare AtomicReference<TriageStatus> status
        // TODO: Declare volatile boolean streamingActive

        public PatientVitalTracker(int initialHeartRate, TriageStatus initialStatus) {
            // TODO: Initialize fields
        }

        public void recordHeartRate(int bpm) {
            // TODO: Set currentHeartRate and update peakHeartRate using CAS / accumulateAndGet
        }

        public int getCurrentHeartRate() {
            // TODO: Return current heart rate
            return 0;
        }

        public int getPeakHeartRate() {
            // TODO: Return peak heart rate
            return 0;
        }

        public boolean transitionStatus(TriageStatus expected, TriageStatus next) {
            // TODO: Atomically transition status using compareAndSet
            return false;
        }

        public TriageStatus getStatus() {
            // TODO: Return current status
            return null;
        }

        public void startStreaming() {
            // TODO: Set streamingActive to true
        }

        public void stopStreaming() {
            // TODO: Set streamingActive to false
        }

        public boolean isStreamingActive() {
            // TODO: Return streamingActive
            return false;
        }
    }
}
