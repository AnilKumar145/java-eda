# Lab 1 Tasks: Hospital ER Patient Triage Dispatcher (Java)

Follow these steps to complete `StarterCode.java`.

---

### Task 1: Define `TriageCase` Record / Class
Implement `Comparable<TriageCase>`:
- `int acuityLevel` (1 = critical, 5 = minor)
- `String patientId`
- `String condition`
- Compare by `acuityLevel` ascending (`this.acuityLevel - o.acuityLevel`).

---

### Task 2: Implement `ERTriageDispatcher`
1. Use a `PriorityQueue<TriageCase> queue` guarded by `private final Object lock = new Object()`.
2. Implement `admitPatient(TriageCase c)`:
   - Synchronize on `lock`.
   - Add case to `queue`.
   - Call `lock.notifyAll()`.
3. Implement `doctorWorker(String doctorId)`:
   - Synchronize on `lock`.
   - While `queue.isEmpty()` and not shutdown, call `lock.wait()`.
   - Treat case and record in `treatedCases` list.
4. Implement `startDoctors(int count)` and `drainAndShutdown()`.
