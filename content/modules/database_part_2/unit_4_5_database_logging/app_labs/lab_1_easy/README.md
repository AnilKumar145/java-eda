# Application Lab 1: ICU Diagnostic Query Profiler & Slow Statement Watchdog

## Scenario
You are developing real-time diagnostic middleware for an Intensive Care Unit (ICU) clinical telemetry monitoring system. Due to continuous patient cardiac and respiratory data streams, database latency spikes can delay life-critical alert notifications. You will build an execution profiler that intercepts JDBC statements, calculates performance statistics (min, max, average, total count), and detects slow queries exceeding strict latency budgets.

## Learning Objectives
- Profile query execution duration accurately at microsecond and millisecond resolutions.
- Categorize statements into latency classifications (`FAST`, `NORMAL`, `SLOW`, `CRITICAL`).
- Maintain query performance metrics per SQL fingerprint.
- Monitor connection checkout and release lifetimes to identify connection hoarding.

## Lab Structure
- `tasks.md`: Detailed specifications for Tasks 1, 2, and 3.
- `StarterCode.java`: Starter skeleton containing class stubs and TODO markers.
- `solution/Solution.java`: Complete reference implementation.
- `LabTests.java`: Assertion test suite validating your implementation on live H2 database instances.

## How to Test
```bash
# Test Reference Solution
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_2/unit_4_5_database_logging/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_5_database_logging/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests
```
