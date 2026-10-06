import java.util.*;

public class Solution {

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Patient patient = (Patient) o;
            return Objects.equals(id, patient.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Allergen allergen = (Allergen) o;
            return Objects.equals(id, allergen.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class ClinicalAllergyHub {
        private final List<JoinRow> joinTable = new ArrayList<>();

        public List<JoinRow> getJoinTable() { return joinTable; }

        public void documentAllergy(Patient patient, Allergen allergen, String severity) {
            if (patient == null || allergen == null) return;
            patient.getAllergies().add(allergen);
            allergen.getPatients().add(patient);
            joinTable.add(new JoinRow(patient.getId(), allergen.getId(), severity));
        }

        public int simulateLazyLoadCount(int patientCount) {
            return 1 + patientCount;
        }

        public int simulateEagerLoadCount() {
            return 1;
        }

        public List<Patient> hydrateEagerGraph(List<JoinedRowData> rows) {
            if (rows == null || rows.isEmpty()) return Collections.emptyList();

            Map<Long, Patient> patientMap = new LinkedHashMap<>();
            Map<Long, Allergen> allergenMap = new HashMap<>();

            for (JoinedRowData row : rows) {
                Patient patient = patientMap.computeIfAbsent(
                    row.patientId(),
                    id -> new Patient(id, row.patientName())
                );

                if (row.allergenId() != null) {
                    Allergen allergen = allergenMap.computeIfAbsent(
                        row.allergenId(),
                        id -> new Allergen(id, row.allergenCode())
                    );

                    patient.getAllergies().add(allergen);
                    allergen.getPatients().add(patient);
                }
            }

            return new ArrayList<>(patientMap.values());
        }
    }
}
