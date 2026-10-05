import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Immutable Patient Medical Profile Cache - Starter Code
 */
public class StarterCode {

    public record PatientProfile(
        String patientId,
        String name,
        int age,
        List<String> allergies,
        List<String> activeMeds
    ) {
        public PatientProfile {
            // TODO: Enforce immutability with List.copyOf()
        }
    }

    public static class PatientProfileCache {
        // TODO: Implement thread-safe cache
        public void putProfile(PatientProfile profile) {}
        public PatientProfile getProfile(String patientId) { return null; }
        public int size() { return 0; }
    }
}
