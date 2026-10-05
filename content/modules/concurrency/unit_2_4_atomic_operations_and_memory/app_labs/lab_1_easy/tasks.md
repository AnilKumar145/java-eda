# Lab 1 Tasks: Patient Vital Metric Tracker

## Task 1: Lock-Free Heart Rate Telemetry
- In `PatientVitalTracker`, declare:
  - `AtomicInteger currentHeartRate`
  - `AtomicInteger peakHeartRate`
  - `volatile boolean streamingActive`
- Implement `void recordHeartRate(int bpm)`:
  - Set `currentHeartRate`.
  - Atomically update `peakHeartRate` using a CAS loop or `peakHeartRate.accumulateAndGet(bpm, Math::max)`.

## Task 2: Atomic Triage State Transition
- Implement enum `TriageStatus { STABLE, ELEVATED, CRITICAL, DISCHARGED }`.
- In `PatientVitalTracker`, declare `AtomicReference<TriageStatus> status`.
- Implement `boolean transitionStatus(TriageStatus expected, TriageStatus next)`:
  - Use `status.compareAndSet(expected, next)`.
  - Return `true` if successful, `false` otherwise.

## Task 3: Sensor Streaming Control
- Implement `void startStreaming()` setting `streamingActive = true`.
- Implement `void stopStreaming()` setting `streamingActive = false`.
- Implement `boolean isStreamingActive()` returning `streamingActive`.
