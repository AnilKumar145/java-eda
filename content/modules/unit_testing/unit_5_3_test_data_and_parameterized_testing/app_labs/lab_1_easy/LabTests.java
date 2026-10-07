import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LabTests {

    public static void main(String[] args) throws IOException {
        testReferenceRangeMatrix();
        testReagentConsumptionAndDepletion();
        testTemporaryFileAuditReport();
        System.out.println("All Unit 5.3 Lab 1 Tests Passed Successfully!");
    }

    public static void testReferenceRangeMatrix() {
        Solution.ChemistryAnalyzer analyzer = new Solution.ChemistryAnalyzer();
        analyzer.loadCartridge("GLUCOSE", 100);
        analyzer.loadCartridge("POTASSIUM", 100);
        analyzer.loadCartridge("SODIUM", 100);

        Object[][] matrix = new Object[][] {
            {"GLUCOSE", 65.0, "LOW"},
            {"GLUCOSE", 85.0, "NORMAL"},
            {"GLUCOSE", 140.0, "HIGH"},
            {"POTASSIUM", 3.1, "LOW"},
            {"POTASSIUM", 4.2, "NORMAL"},
            {"POTASSIUM", 5.8, "HIGH"},
            {"SODIUM", 130.0, "LOW"},
            {"SODIUM", 140.0, "NORMAL"},
            {"SODIUM", 152.0, "HIGH"}
        };

        for (Object[] item : matrix) {
            String analyte = (String) item[0];
            double val = (Double) item[1];
            String expected = (String) item[2];

            Solution.LabTestResult result = analyzer.evaluateSample(analyte, val);
            assert expected.equals(result.flag()) : "Failed for " + analyte + " " + val + ": expected " + expected + ", got " + result.flag();
        }
        System.out.println("Lab 1 Test 1 Passed: 9 reference matrix cases passed.");
    }

    public static void testReagentConsumptionAndDepletion() {
        Solution.ChemistryAnalyzer analyzer = new Solution.ChemistryAnalyzer();
        analyzer.loadCartridge("POTASSIUM", 2);

        Solution.LabTestResult r1 = analyzer.evaluateSample("POTASSIUM", 4.0);
        assert r1.remainingReagents() == 1 : "Should have 1 reagent left";

        Solution.LabTestResult r2 = analyzer.evaluateSample("POTASSIUM", 4.1);
        assert r2.remainingReagents() == 0 : "Should have 0 reagents left";

        boolean caughtDepleted = false;
        try {
            analyzer.evaluateSample("POTASSIUM", 4.2);
        } catch (IllegalStateException e) {
            caughtDepleted = true;
        }
        assert caughtDepleted : "Third evaluation must throw IllegalStateException on depleted cartridge";
        System.out.println("Lab 1 Test 2 Passed: Reagent depletion behavior verified.");
    }

    public static void testTemporaryFileAuditReport() throws IOException {
        Path tempDir = Files.createTempDirectory("lis_test_audit_");
        Path reportFile = tempDir.resolve("audit_report.txt");

        try {
            Solution.ChemistryAnalyzer analyzer = new Solution.ChemistryAnalyzer();
            analyzer.loadCartridge("GLUCOSE", 50);
            analyzer.evaluateSample("GLUCOSE", 88.0);
            analyzer.evaluateSample("GLUCOSE", 110.0);

            analyzer.exportAuditReport(reportFile);

            assert Files.exists(reportFile) : "Audit file must exist";
            List<String> lines = Files.readAllLines(reportFile);
            assert lines.stream().anyMatch(l -> l.contains("TOTAL_PROCESSED=2")) : "Processed count mismatch";
            assert lines.stream().anyMatch(l -> l.contains("REAGENT_GLUCOSE=48")) : "Remaining glucose mismatch";
            System.out.println("Lab 1 Test 3 Passed: Temporary audit report generation verified.");
        } finally {
            Files.deleteIfExists(reportFile);
            Files.deleteIfExists(tempDir);
        }
    }
}
