package unit_2_3_concurrent_collections.app_labs.lab_1_easy;

import unit_2_3_concurrent_collections.app_labs.lab_1_easy.solution.Solution;
import unit_2_3_concurrent_collections.app_labs.lab_1_easy.solution.Solution.Incident;
import unit_2_3_concurrent_collections.app_labs.lab_1_easy.solution.Solution.IncidentAuditStream;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LabTests {

    public static void main(String[] args) throws Exception {
        System.out.println("Running Unit 2.3 Lab Tests...");

        testQueueSubmissionAndPoll();
        testConcurrentWardAggregation();
        testObserverNotification();

        System.out.println("All Unit 2.3 Lab tests PASSED!");
    }

    private static void testQueueSubmissionAndPoll() throws Exception {
        IncidentAuditStream stream = new IncidentAuditStream(2);
        boolean s1 = stream.submitIncident(new Incident("INC-1", "ICU", "HIGH", "Oxygen drop"), 50);
        boolean s2 = stream.submitIncident(new Incident("INC-2", "ER", "MED", "Bed delay"), 50);
        boolean s3 = stream.submitIncident(new Incident("INC-3", "WARD-B", "LOW", "Call bell"), 50);

        assert s1 : "First incident should be accepted";
        assert s2 : "Second incident should be accepted";
        assert !s3 : "Queue full: third incident should time out";

        Incident polled = stream.pollIncident(50);
        assert polled != null && polled.id().equals("INC-1") : "Should poll INC-1";
    }

    private static void testConcurrentWardAggregation() throws Exception {
        IncidentAuditStream stream = new IncidentAuditStream(500);
        int threads = 10;
        int perThread = 50;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final int tId = i;
            exec.submit(() -> {
                try {
                    String ward = (tId % 2 == 0) ? "ICU" : "ER";
                    for (int j = 0; j < perThread; j++) {
                        stream.processIncident(new Incident("INC-" + tId + "-" + j, ward, "MEDIUM", "Checkup"));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        exec.shutdown();

        assert stream.getWardCount("ICU") == 250 : "ICU should have 250 incidents, got " + stream.getWardCount("ICU");
        assert stream.getWardCount("ER") == 250 : "ER should have 250 incidents, got " + stream.getWardCount("ER");
        assert stream.getWardCount("CARDIOLOGY") == 0 : "Unknown ward should have 0";
    }

    private static void testObserverNotification() {
        IncidentAuditStream stream = new IncidentAuditStream(10);
        AtomicInteger notified = new AtomicInteger(0);

        stream.registerObserver(incident -> notified.incrementAndGet());
        stream.registerObserver(incident -> notified.incrementAndGet());

        stream.processIncident(new Incident("INC-99", "ICU", "CRITICAL", "Cardiac arrest"));

        assert notified.get() == 2 : "Both observers should be notified, got: " + notified.get();
    }
}
