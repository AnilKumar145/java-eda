import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Immutable Patient Medical Profile Cache - Solution
 */
public class Solution {

    public record PatientProfile(
        String patientId,
        String name,
        int age,
        List<String> allergies,
        List<String> activeMeds
    ) {
        public PatientProfile {
            allergies = List.copyOf(allergies);
            activeMeds = List.copyOf(activeMeds);
        }
    }

    public static class PatientProfileCache {
        private final Map<String, PatientProfile> cacheMap = new ConcurrentHashMap<>();

        public void putProfile(PatientProfile profile) {
            cacheMap.put(profile.patientId(), profile);
        }

        public PatientProfile getProfile(String patientId) {
            return cacheMap.get(patientId);
        }

        public int size() {
            return cacheMap.size();
        }
    }
}
