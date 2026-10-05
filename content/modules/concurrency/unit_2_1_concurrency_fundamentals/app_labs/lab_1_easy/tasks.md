# Lab 1 Tasks: Immutable Patient Medical Profile Cache (Java)

Follow these steps to complete `StarterCode.java`.

---

### Task 1: Define `PatientProfile` Record
1. Fields: `String patientId`, `String name`, `int age`, `List<String> allergies`, `List<String> activeMeds`.
2. Compact Constructor:
   - Defensively copy `allergies = List.copyOf(allergies)`.
   - Defensively copy `activeMeds = List.copyOf(activeMeds)`.

---

### Task 2: Implement `PatientProfileCache`
1. Use a `ConcurrentHashMap<String, PatientProfile> cacheMap`.
2. Implement `putProfile(PatientProfile profile)`.
3. Implement `getProfile(String patientId)`.
4. Implement `size()`.
