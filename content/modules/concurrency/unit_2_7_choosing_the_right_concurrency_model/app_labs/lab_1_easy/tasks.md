# Lab 1 Tasks: Clinical Metric Benchmark Harness

## Task 1: Interface & Implementations
- Define `TelemetryCounter` interface with `void recordSample()` and `long getCount()`.
- Implement `SynchronizedCounter`:
  - Uses `synchronized (lock)` to increment a primitive `long count`.
- Implement `ReentrantLockCounter`:
  - Uses `ReentrantLock.lock()` and `unlock()` in a `try-finally` block.
- Implement `AtomicCounter`:
  - Uses `AtomicLong.incrementAndGet()`.

## Task 2: Benchmark Result Record
- Define `BenchmarkResult(String strategyName, long totalCount, long elapsedMs)`.

## Task 3: Benchmark Runner
- Implement `BenchmarkResult runBenchmark(TelemetryCounter counter, String name, int threads, int incrementsPerThread)`:
  - Spawn `threads` using an `ExecutorService`.
  - Use `CountDownLatch` to await completion.
  - Measure elapsed time in milliseconds.
  - Return `new BenchmarkResult(name, counter.getCount(), elapsedMs)`.
