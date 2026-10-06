# Lab Tasks: ICU Diagnostic Query Profiler & Slow Statement Watchdog

## Task 1: Execution Duration Profiler
Implement `IcuQueryProfiler.profileExecution(String sqlFingerprint, long durationNanos)`:
- Convert `durationNanos` to milliseconds (`durationNanos / 1_000_000.0`).
- Classify execution according to latency thresholds:
  - `< 10.0 ms`: `FAST`
  - `10.0 ms` to `< 50.0 ms`: `NORMAL`
  - `50.0 ms` to `< 200.0 ms`: `SLOW`
  - `>= 200.0 ms`: `CRITICAL`
- Record the execution in the fingerprint's statistical aggregate (`StatementMetrics`).

## Task 2: Aggregated Statement Metrics
Implement `StatementMetrics.record(double durationMs)`:
- Increment execution count.
- Update `totalDurationMs`.
- Update `minDurationMs` and `maxDurationMs`.
- Calculate `getAverageDurationMs()` = `totalDurationMs / count`.

## Task 3: Slow Statement Alerts & Watchdog
Implement `IcuQueryProfiler.getSlowQueryAlerts(double thresholdMs)`:
- Filter all recorded profile events whose duration exceeded `thresholdMs`.
- Return list of `ExecutionLogEntry` sorted in descending order of duration (slowest first).
