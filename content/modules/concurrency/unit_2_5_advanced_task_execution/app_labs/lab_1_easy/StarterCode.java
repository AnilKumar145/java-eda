package unit_2_5_advanced_task_execution.app_labs.lab_1_easy;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class StarterCode {

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
            // TODO:
            // 1. supplyAsync for bloodLabService with .exceptionally fallback: "LABS_UNAVAILABLE"
            // 2. supplyAsync for radiologyService with .exceptionally fallback: "RADIOLOGY_UNAVAILABLE"
            // 3. supplyAsync for allergyService with .exceptionally fallback: "ALLERGIES_UNAVAILABLE"
            // 4. Combine all three and return CompletableFuture<DiagnosticSummary>
            //    status is "DEGRADED" if any returned fallback, otherwise "COMPLETE"
            return null;
        }
    }
}
