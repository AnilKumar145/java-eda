import java.util.*;

public class LabTests {

    public static void main(String[] args) {
        testManyToManyDocumentation();
        testNPlusOneSimulations();
        testHydrateEagerGraph();
        System.out.println("All Unit 4.3 Lab 1 Tests Passed Successfully!");
    }

    public static void testManyToManyDocumentation() {
        Solution.ClinicalAllergyHub hub = new Solution.ClinicalAllergyHub();

        Solution.Patient p1 = new Solution.Patient(101L, "Bruce Wayne");
        Solution.Allergen a1 = new Solution.Allergen(501L, "PENICILLIN");
        Solution.Allergen a2 = new Solution.Allergen(502L, "LATEX");

        hub.documentAllergy(p1, a1, "ANAPHYLACTIC");
        hub.documentAllergy(p1, a2, "MILD_RASH");

        assert p1.getAllergies().size() == 2 : "Patient should have 2 documented allergies";
        assert a1.getPatients().contains(p1) : "Penicillin should contain patient";
        assert a2.getPatients().contains(p1) : "Latex should contain patient";
        assert hub.getJoinTable().size() == 2 : "Join table should contain 2 rows";
        System.out.println("Lab 1 Test 1 Passed: Many-to-many allergy documentation validated.");
    }

    public static void testNPlusOneSimulations() {
        Solution.ClinicalAllergyHub hub = new Solution.ClinicalAllergyHub();

        assert hub.simulateLazyLoadCount(100) == 101 : "100 patients with lazy loading must be 101 queries";
        assert hub.simulateEagerLoadCount() == 1 : "Eager loading must always be 1 query";
        System.out.println("Lab 1 Test 2 Passed: N+1 vs Eager query execution counts validated.");
    }

    public static void testHydrateEagerGraph() {
        Solution.ClinicalAllergyHub hub = new Solution.ClinicalAllergyHub();

        // Flatted join rows (like from SQL LEFT JOIN)
        List<Solution.JoinedRowData> rows = List.of(
            new Solution.JoinedRowData(1L, "Clark Kent", 10L, "KRYPTONITE", "FATAL"),
            new Solution.JoinedRowData(1L, "Clark Kent", 11L, "POLLEN", "MILD"),
            new Solution.JoinedRowData(2L, "Diana Prince", null, null, null), // No allergies
            new Solution.JoinedRowData(3L, "Barry Allen", 11L, "POLLEN", "SEVERE") // Shared allergy
        );

        List<Solution.Patient> patients = hub.hydrateEagerGraph(rows);
        assert patients.size() == 3 : "Should hydrate 3 distinct patients";

        Solution.Patient clark = patients.get(0);
        assert clark.getId().equals(1L) : "First patient id mismatch";
        assert clark.getAllergies().size() == 2 : "Clark Kent should have 2 allergies";

        Solution.Patient diana = patients.get(1);
        assert diana.getId().equals(2L) : "Second patient id mismatch";
        assert diana.getAllergies().isEmpty() : "Diana Prince should have 0 allergies";

        Solution.Patient barry = patients.get(2);
        assert barry.getId().equals(3L) : "Third patient id mismatch";
        assert barry.getAllergies().size() == 1 : "Barry Allen should have 1 allergy";

        System.out.println("Lab 1 Test 3 Passed: Eager joined graph hydration without duplicates validated.");
    }
}
