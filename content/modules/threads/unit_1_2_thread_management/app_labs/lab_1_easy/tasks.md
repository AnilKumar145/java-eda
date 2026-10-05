# Lab 1 Tasks: ICU Watchdog Telemetry Supervisor (Java)

Follow these steps to complete `StarterCode.java`.

---

### Task 1: Implement `ICUWatchdogSupervisor.supervisePoller(Thread pollerThread, long deadlineMs)`
1. Call `pollerThread.join(deadlineMs)` to wait for completion.
2. If `pollerThread.isAlive()`:
   - Call `pollerThread.interrupt()` to cooperatively cancel the stalled sensor read.
   - Return `"STATUS_TIMED_OUT"`.
3. If `pollerThread` finished within the deadline:
   - Return `"STATUS_OK"`.

---

### Task 2: Implement `ICUWatchdogSupervisor.startHeartbeatDaemon(AtomicLong pingCounter, long intervalMs)`
1. Construct a thread that loops while `!Thread.currentThread().isInterrupted()`.
2. Inside the loop, increment `pingCounter.incrementAndGet()` and sleep for `intervalMs`.
3. Call `setDaemon(true)` on the thread before starting.
4. Start and return the daemon thread.
