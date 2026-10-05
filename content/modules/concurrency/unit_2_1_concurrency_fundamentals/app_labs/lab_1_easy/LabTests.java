import java.util.ArrayList;
import java.util.List;

/**
 * Verification Tests for Lab 1 Easy: Immutable Patient Medical Profile Cache (Java)
 */
public class LabTests {

    public static void main(String[] args) throws Exception {
        testDefensiveCopyingAgainstExternalMutation();
        testConcurrentMultiThreadedReads();
        System.out.println("All Lab 1 Easy Tests Passed!");
    }

    public static void testDefensiveCopyingAgainstExternalMutation() {
        Solution.PatientProfileCache cache = new Solution.PatientProfileCache();

        List<String> mutableAllergies = new ArrayList<>();
        mutableAllergies.add("Penicillin");

        List<String> mutableMeds = new ArrayList<>();
        mutableMeds.add("Aspirin");

        Solution.PatientProfile profile = new Solution.PatientProfile(
            "PAT-100", "John Doe", 45, mutableAllergies, mutableMeds
        );
        cache.putProfile(profile);

        // Attempt external modification of lists
        mutableAllergies.add("Latex");
        mutableMeds.add("Metformin");

        // Verify cached profile is UNCHANGED
        Solution.PatientProfile cached = cache.getProfile("PAT-100");
        assert cached.allergies().size() == 1 : "Defensive copy failed for allergies!";
        assert cached.activeMeds().size() == 1 : "Defensive copy failed for activeMeds!";
        assert !cached.allergies().contains("Latex") : "Cached profile leaked external mutation";
        System.out.println("Test 1 passed! Defensive copying protected cached profile from external mutation.");
    }

    public static void testConcurrentMultiThreadedReads() throws Exception {
        Solution.PatientProfileCache cache = new Solution.PatientProfileCache();
        for (int i = 0; i < 50; i++) {
            cache.putProfile(new Solution.PatientProfile(
                "P-" + i, "Patient-" + i, 30 + i, List.of("None"), List.of("Multivitamin")
            ));
        }

        List<Thread> readerThreads = new ArrayList<>();
        for (int t = 0; t < 10; t++) {
            readerThreads.add(new Thread(() -> {
                for (int i = 0; i < 50; i++) {
                    Solution.PatientProfile p = cache.getProfile("P-" + i);
                    assert p != null && p.allergies().contains("None");
                }
            }));
        }

        for (Thread t : readerThreads) t.start();
        for (Thread t : readerThreads) t.join();

        assert cache.size() == 50 : "Cache size mismatch";
        System.out.println("Test 2 passed! 10 concurrent reader threads verified consistent state.");
    }
}
