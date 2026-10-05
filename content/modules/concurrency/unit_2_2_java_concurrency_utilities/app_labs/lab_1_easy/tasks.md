# Lab 1 Tasks: ICU Bed Reservation Gateway (Java)

Follow these steps to complete `StarterCode.java`.

---

### Task 1: Create `ICUBedReservationGateway`
1. Initialize a `Semaphore availableBeds = new Semaphore(totalBeds, true)`.
2. Initialize a `Set<String> activeReservations = new HashSet<>()` guarded by `private final ReentrantLock registryLock = new ReentrantLock()`.
3. Implement `public boolean reserveBed(String patientId, long timeoutMs)`:
   - Call `availableBeds.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)`.
   - If acquired:
     - Lock `registryLock`.
     - Try/finally: add `patientId` to `activeReservations`.
     - Return `true`.
   - If not acquired, return `false`.
4. Implement `public boolean releaseBed(String patientId)`:
   - Lock `registryLock`.
   - In try/finally: check if `patientId` was present and removed.
   - If removed, call `availableBeds.release()` and return `true`.
   - Otherwise return `false`.
5. Implement `public int getOccupiedCount()`.
