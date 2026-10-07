import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Solution {

    public record ReagentCartridge(String analyte, int remainingTests) {}

    public record LabTestResult(String analyte, double value, String flag, int remainingReagents) {}

    public static class ChemistryAnalyzer {

        private final Map<String, Integer> reagentInventory = new HashMap<>();
        private int processedCount = 0;

        public void loadCartridge(String analyte, int initialTests) {
            if (initialTests <= 0) {
                throw new IllegalArgumentException("Cartridge tests must be positive: " + initialTests);
            }
            reagentInventory.put(analyte.trim().toUpperCase(), initialTests);
        }

        public LabTestResult evaluateSample(String analyte, double value) {
            String key = analyte.trim().toUpperCase();
            if (!reagentInventory.containsKey(key) || reagentInventory.get(key) <= 0) {
                throw new IllegalStateException("Reagent cartridge depleted or missing for: " + analyte);
            }

            int currentStock = reagentInventory.get(key);
            reagentInventory.put(key, currentStock - 1);
            processedCount++;

            String flag = classifyAnalyte(key, value);
            return new LabTestResult(key, value, flag, currentStock - 1);
        }

        private String classifyAnalyte(String analyte, double value) {
            return switch (analyte) {
                case "GLUCOSE" -> {
                    if (value < 70.0) yield "LOW";
                    if (value > 99.0) yield "HIGH";
                    yield "NORMAL";
                }
                case "POTASSIUM" -> {
                    if (value < 3.5) yield "LOW";
                    if (value > 5.0) yield "HIGH";
                    yield "NORMAL";
                }
                case "SODIUM" -> {
                    if (value < 135.0) yield "LOW";
                    if (value > 145.0) yield "HIGH";
                    yield "NORMAL";
                }
                default -> throw new IllegalArgumentException("Unknown clinical analyte: " + analyte);
            };
        }

        public void exportAuditReport(Path destinationFile) throws IOException {
            List<String> lines = new ArrayList<>();
            lines.add("TOTAL_PROCESSED=" + processedCount);
            for (Map.Entry<String, Integer> entry : reagentInventory.entrySet()) {
                lines.add("REAGENT_" + entry.getKey() + "=" + entry.getValue());
            }
            Files.write(destinationFile, lines);
        }
    }
}
