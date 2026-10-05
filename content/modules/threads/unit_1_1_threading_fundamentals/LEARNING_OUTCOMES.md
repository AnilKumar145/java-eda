# Learning Outcomes: Unit 1.1 Threading Fundamentals (Java)

By the end of this unit, learners will be able to:

1. **Explain the Java Thread Model**: Articulate how the Java Virtual Machine (JVM) maps Java threads directly to native OS kernel threads, and differentiate between process-level and thread-level memory boundaries.
2. **Implement Thread Creation Patterns**: Choose cleanly between extending `java.lang.Thread` and implementing `java.lang.Runnable` (or lambda expressions), understanding the benefits of composition over inheritance.
3. **Control Thread Execution**: Initiate concurrent execution paths correctly using `thread.start()` rather than synchronous `.run()`, and coordinate thread completion with `.join()`.
4. **Trace the Thread Lifecycle**: Identify and inspect the 6 official JVM thread states (`NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, `TERMINATED`) to diagnose thread hangs and verify system health.
