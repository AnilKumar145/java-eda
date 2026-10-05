package unit_2_4_atomic_operations_and_memory.app_labs.lab_1_easy;

import unit_2_4_atomic_operations_and_memory.app_labs.lab_1_easy.solution.Solution.PatientVitalTracker;
import unit_2_4_atomic_operations_and_memory.app_labs.lab_1_easy.solution.Solution.TriageStatus;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LabTests {

    public static void main(String[] args) throws Exception {
        System.out.println("Running Unit 2.4 Lab Tests...");

        testHeartRateRecordingAndPeak();
        testAtomicStatusTransition();
        testStreamingFlagVisibility();

        System.out.println("All Unit 2.4 Lab tests PASSED!");
    }

    private static void testHeartRateRecordingAndPeak() throws Exception {
        PatientVitalTracker tracker = new PatientVitalTracker(72, TriageStatus.STABLE);
        int threads = 10;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final int tId = i;
            exec.submit(() -> {
                try {
                    // One thread sets a known high peak of 165
                    if (tId == 5) {
                        tracker.recordHeartRate(165);
                    } else {
                        tracker.recordHeartRate(70 + tId);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        exec.shutdown();

        assert tracker.getPeakHeartRate() == 165 : "Peak should be 165, got " + tracker.getPeakHeartRate();
    }

    private static void testAtomicStatusTransition() throws Exception {
        PatientVitalTracker tracker = new PatientVitalTracker(72, TriageStatus.STABLE);
        int racers = 5;
        ExecutorService exec = Executors.newFixedThreadPool(racers);
        CountDownLatch latch = new CountDownLatch(racers);
        AtomicInteger successfulTransitions = new AtomicInteger(0);

        for (int i = 0; i < racers; i++) {
            exec.submit(() -> {
                try {
                    if (tracker.transitionStatus(TriageStatus.STABLE, TriageStatus.CRITICAL)) {
                        successfulTransitions.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        exec.shutdown();

        assert successfulTransitions.get() == 1 : "Only 1 thread should succeed in CAS transition";
        assert tracker.getStatus() == TriageStatus.CRITICAL : "Final status must be CRITICAL";
    }

    private static void testStreamingFlagVisibility() {
        PatientVitalTracker tracker = new PatientVitalTracker(75, TriageStatus.STABLE);
        assert !tracker.isStreamingActive() : "Initial streaming should be false";

        tracker.startStreaming();
        assert tracker.isStreamingActive() : "Streaming should be active";

        tracker.stopStreaming();
        assert !tracker.isStreamingActive() : "Streaming should be stopped";
    }
}
