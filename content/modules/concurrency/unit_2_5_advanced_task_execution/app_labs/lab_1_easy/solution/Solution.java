package unit_2_5_advanced_task_execution.app_labs.lab_1_easy.solution;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class Solution {

    public record DiagnosticSummary(
            String patientId,
            String bloodLab,
            String radiology,
            String allergies,
            String status
    ) {}

    public static class DiagnosticReportAggregator {
        private final Supplier<String> bloodLabService;
        private final Supplier<String> radiologyService;
        private final Supplier<String> allergyService;

        public DiagnosticReportAggregator(
                Supplier<String> bloodLabService,
                Supplier<String> radiologyService,
                Supplier<String> allergyService) {
            this.bloodLabService = bloodLabService;
            this.radiologyService = radiologyService;
            this.allergyService = allergyService;
        }

        public CompletableFuture<DiagnosticSummary> generateReportAsync(String patientId, ExecutorService pool) {
            CompletableFuture<String> labsFuture = CompletableFuture.supplyAsync(bloodLabService, pool)
                    .exceptionally(ex -> "LABS_UNAVAILABLE");

            CompletableFuture<String> radFuture = CompletableFuture.supplyAsync(radiologyService, pool)
                    .exceptionally(ex -> "RADIOLOGY_UNAVAILABLE");

            CompletableFuture<String> allergyFuture = CompletableFuture.supplyAsync(allergyService, pool)
                    .exceptionally(ex -> "ALLERGIES_UNAVAILABLE");

            return labsFuture.thenCombine(radFuture, (labs, rad) -> new String[]{labs, rad})
                    .thenCombine(allergyFuture, (parts, allergy) -> {
                        String labs = parts[0];
                        String rad = parts[1];
                        boolean degraded = labs.equals("LABS_UNAVAILABLE")
                                || rad.equals("RADIOLOGY_UNAVAILABLE")
                                || allergy.equals("ALLERGIES_UNAVAILABLE");
                        return new DiagnosticSummary(
                                patientId,
                                labs,
                                rad,
                                allergy,
                                degraded ? "DEGRADED" : "COMPLETE"
                        );
                    });
        }
    }
}
