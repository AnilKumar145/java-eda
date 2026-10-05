# Lab 1 Tasks: Bedside Vitals Telemetry Poller (Java)

Follow these steps to complete the telemetry ingestion engine in `StarterCode.java`.

---

### Task 1: Define `MonitorConfig` Record / Class
Create a data container `MonitorConfig` holding:
- `String deviceId`
- `long latencyMs`
- `Object telemetryValue`

---

### Task 2: Implement `TelemetryIngestionEngine.pollMonitors`
1. Initialize a thread-safe `ConcurrentHashMap<String, Object> results = new ConcurrentHashMap<>()`.
2. Create a `List<Thread> threads = new ArrayList<>()`.
3. For each `MonitorConfig` in `configs`:
   - Construct a `Thread` with target runnable:
     - Simulate latency using `Thread.sleep(config.latencyMs())`.
     - Put `config.telemetryValue()` into `results` keyed by `config.deviceId()`.
     - Handle `InterruptedException` by restoring thread interrupt flag `Thread.currentThread().interrupt()`.
   - Name the thread `device-poller-{deviceId}`.
   - Start the thread and add to `threads` list.
4. Iterate through `threads` and call `thread.join()` on each.
5. Return the populated `results` map.
