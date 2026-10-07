import java.io.IOException;
import java.nio.file.Path;

public class StarterCode {

    public record ReagentCartridge(String analyte, int remainingTests) {}

    public record LabTestResult(String analyte, double value, String flag, int remainingReagents) {}

    public static class ChemistryAnalyzer {

        public void loadCartridge(String analyte, int initialTests) {
            // TODO: Store cartridge inventory.
            throw new UnsupportedOperationException("TODO: Implement loadCartridge");
        }

        public LabTestResult evaluateSample(String analyte, double value) {
            // TODO: If cartridge not present or stock <= 0, throw IllegalStateException.
            // Decrement remaining cartridge tests.
            // Classify value into LOW, NORMAL, HIGH according to reference intervals.
            // Return LabTestResult.
            throw new UnsupportedOperationException("TODO: Implement evaluateSample");
        }

        public void exportAuditReport(Path destinationFile) throws IOException {
            // TODO: Write processed test count and remaining reagent levels to file.
            throw new UnsupportedOperationException("TODO: Implement exportAuditReport");
        }
    }
}
