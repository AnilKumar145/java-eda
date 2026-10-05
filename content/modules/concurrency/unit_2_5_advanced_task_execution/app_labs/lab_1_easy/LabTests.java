package unit_2_5_advanced_task_execution.app_labs.lab_1_easy;

import unit_2_5_advanced_task_execution.app_labs.lab_1_easy.solution.Solution.DiagnosticReportAggregator;
import unit_2_5_advanced_task_execution.app_labs.lab_1_easy.solution.Solution.DiagnosticSummary;

import java.util.concurrent.*;

public class LabTests {

    public static void main(String[] args) throws Exception {
        System.out.println("Running Unit 2.5 Lab Tests...");

        testSuccessfulParallelAggregation();
        testDegradedFallbackAggregation();

        System.out.println("All Unit 2.5 Lab tests PASSED!");
    }

    private static void testSuccessfulParallelAggregation() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);

        DiagnosticReportAggregator aggregator = new DiagnosticReportAggregator(
                () -> "CBC Normal, Hemoglobin 14.2 g/dL",
                () -> "Chest X-Ray Clear, No Infiltrates",
                () -> "No Known Drug Allergies (NKDA)"
        );

        CompletableFuture<DiagnosticSummary> future = aggregator.generateReportAsync("PAT-101", pool);
        DiagnosticSummary summary = future.get(3, TimeUnit.SECONDS);

        assert summary.patientId().equals("PAT-101");
        assert summary.bloodLab().contains("Hemoglobin 14.2");
        assert summary.radiology().contains("Chest X-Ray Clear");
        assert summary.allergies().contains("NKDA");
        assert summary.status().equals("COMPLETE") : "Expected COMPLETE, got " + summary.status();

        pool.shutdown();
    }

    private static void testDegradedFallbackAggregation() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);

        DiagnosticReportAggregator aggregator = new DiagnosticReportAggregator(
                () -> "CBC Normal",
                () -> {
                    throw new RuntimeException("PACS Radiology Server Offline");
                },
                () -> "Penicillin Allergy"
        );

        CompletableFuture<DiagnosticSummary> future = aggregator.generateReportAsync("PAT-202", pool);
        DiagnosticSummary summary = future.get(3, TimeUnit.SECONDS);

        assert summary.patientId().equals("PAT-202");
        assert summary.bloodLab().equals("CBC Normal");
        assert summary.radiology().equals("RADIOLOGY_UNAVAILABLE") : "Radiology fallback expected";
        assert summary.allergies().equals("Penicillin Allergy");
        assert summary.status().equals("DEGRADED") : "Expected DEGRADED status";

        pool.shutdown();
    }
}
