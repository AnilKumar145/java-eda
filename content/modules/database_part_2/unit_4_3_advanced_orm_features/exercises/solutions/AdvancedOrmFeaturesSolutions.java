import java.util.*;

/**
 * Unit 4.3 Solutions: Advanced ORM Features
 * Complete reference implementation for Unit 4.3 exercises.
 */
public class AdvancedOrmFeaturesSolutions {

    public static class Ward {
        private final Long id;
        private final List<Bed> beds = new ArrayList<>();

        public Ward(Long id) { this.id = id; }
        public Long getId() { return id; }
        public List<Bed> getBeds() { return beds; }
    }

    public static class Bed {
        private final Long id;
        private Ward ward;

        public Bed(Long id) { this.id = id; }
        public Long getId() { return id; }
        public Ward getWard() { return ward; }
        public void setWard(Ward ward) { this.ward = ward; }
    }

    public static class Patient {
        private final Long id;
        private final Set<Allergen> allergens = new HashSet<>();

        public Patient(Long id) { this.id = id; }
        public Long getId() { return id; }
        public Set<Allergen> getAllergens() { return allergens; }
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

    public static List<Long> applyCascadeAndOrphanRemoval(Ward ward, List<Bed> newBeds, Set<Long> bedsToKeep) {
        if (ward == null) return Collections.emptyList();

        // 1. Cascade persist incoming beds
        if (newBeds != null) {
            for (Bed bed : newBeds) {
                if (!ward.getBeds().contains(bed)) {
                    ward.getBeds().add(bed);
                }
                bed.setWard(ward);
            }
        }

        // 2. Orphan removal
        List<Long> removedIds = new ArrayList<>();
        Iterator<Bed> it = ward.getBeds().iterator();
        while (it.hasNext()) {
            Bed b = it.next();
            if (bedsToKeep != null && !bedsToKeep.contains(b.getId())) {
                removedIds.add(b.getId());
                b.setWard(null);
                it.remove();
            }
        }

        return removedIds;
    }

    public record JoinPair(Long patientId, Long allergenId) {}

    public static List<JoinPair> linkManyToMany(Patient patient, Set<Allergen> allergens) {
        if (patient == null || allergens == null) return Collections.emptyList();

        List<JoinPair> pairs = new ArrayList<>();
        for (Allergen allergen : allergens) {
            patient.getAllergens().add(allergen);
            allergen.getPatients().add(patient);
            pairs.add(new JoinPair(patient.getId(), allergen.getId()));
        }
        return pairs;
    }

    public static int calculateQueryCount(int totalParents, boolean useJoinFetch) {
        if (useJoinFetch) {
            return 1;
        } else {
            return 1 + totalParents;
        }
    }

    public static String generateJoinFetchSql(String parentTable, String childTable,
                                             String parentPk, String childFk,
                                             List<String> parentCols, List<String> childCols) {
        List<String> qualifiedCols = new ArrayList<>();
        for (String col : parentCols) {
            qualifiedCols.add(parentTable + "." + col);
        }
        for (String col : childCols) {
            qualifiedCols.add(childTable + "." + col);
        }

        return "SELECT " + String.join(", ", qualifiedCols) +
               " FROM " + parentTable + " LEFT JOIN " + childTable +
               " ON " + parentTable + "." + parentPk + " = " + childTable + "." + childFk + ";";
    }

    public static void main(String[] args) {
        // Test 1: Cascade and Orphan Removal
        Ward ward = new Ward(1L);
        ward.getBeds().add(new Bed(10L));
        ward.getBeds().add(new Bed(20L));
        List<Bed> incoming = List.of(new Bed(30L));
        Set<Long> keep = Set.of(10L, 30L); // 20 should be orphaned

        List<Long> orphaned = applyCascadeAndOrphanRemoval(ward, incoming, keep);
        assert orphaned.size() == 1 && orphaned.contains(20L) : "Orphaned bed 20 not detected";
        assert ward.getBeds().size() == 2 : "Ward should retain 2 beds";
        System.out.println("Exercise 1 passed: Cascade and orphan removal validated.");

        // Test 2: Many-to-Many Linker
        Patient p = new Patient(101L);
        Allergen a1 = new Allergen(1L, "PENICILLIN");
        Allergen a2 = new Allergen(2L, "LATEX");
        List<JoinPair> pairs = linkManyToMany(p, Set.of(a1, a2));
        assert pairs.size() == 2 : "Should generate 2 join pairs";
        assert p.getAllergens().size() == 2 : "Patient should have 2 allergens";
        assert a1.getPatients().contains(p) : "Allergen 1 should contain patient";
        assert a2.getPatients().contains(p) : "Allergen 2 should contain patient";
        System.out.println("Exercise 2 passed: Many-to-many associations linked.");

        // Test 3: N+1 Calculator
        assert calculateQueryCount(50, false) == 51 : "Lazy N+1 should be 51 queries";
        assert calculateQueryCount(50, true) == 1 : "Eager JOIN FETCH should be 1 query";
        System.out.println("Exercise 3 passed: N+1 query count calculation validated.");

        // Test 4: JOIN FETCH Query Generator
        String sql = generateJoinFetchSql("wards", "beds", "id", "ward_id",
                                          List.of("id", "name"), List.of("id", "bed_number"));
        assert sql.contains("SELECT wards.id, wards.name, beds.id, beds.bed_number") : "Select columns mismatch";
        assert sql.contains("FROM wards LEFT JOIN beds ON wards.id = beds.ward_id;") : "Join clause mismatch";
        System.out.println("Exercise 4 passed: JOIN FETCH SQL generated.");

        System.out.println("All Unit 4.3 Exercise Tests Passed!");
    }
}
