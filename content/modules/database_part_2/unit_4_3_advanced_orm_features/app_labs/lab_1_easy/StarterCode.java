import java.util.*;

public class StarterCode {

    public record JoinedRowData(
        Long patientId,
        String patientName,
        Long allergenId,
        String allergenCode,
        String severity
    ) {}

    public record JoinRow(Long patientId, Long allergenId, String severity) {}

    public static class Patient {
        private final Long id;
        private final String name;
        private final Set<Allergen> allergies = new HashSet<>();

        public Patient(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public Set<Allergen> getAllergies() { return allergies; }
    }

    public static class Allergen {
        private final Long id;
        private final String code;
        private final Set<Patient> patients = new HashSet<>();

        public Allergen(Long id, String code) {
            this.id = id;
            this.code = code;
        }

        public Long getId() { return id; }
        public String getCode() { return code; }
        public Set<Patient> getPatients() { return patients; }
    }

    public static class ClinicalAllergyHub {
        private final List<JoinRow> joinTable = new ArrayList<>();

        public List<JoinRow> getJoinTable() { return joinTable; }

        public void documentAllergy(Patient patient, Allergen allergen, String severity) {
            // TODO: Link patient and allergen bidirectionally and record in joinTable
        }

        public int simulateLazyLoadCount(int patientCount) {
            // TODO: Return 1 + patientCount
            return 0;
        }

        public int simulateEagerLoadCount() {
            // TODO: Return 1
            return 0;
        }

        public List<Patient> hydrateEagerGraph(List<JoinedRowData> rows) {
            // TODO: Reconstruct distinct Patients and Allergens with bidirectional links from joined rows
            return Collections.emptyList();
        }
    }
}
