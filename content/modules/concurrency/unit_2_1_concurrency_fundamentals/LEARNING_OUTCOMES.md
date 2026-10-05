# Learning Outcomes: Unit 2.1 Concurrency Fundamentals (Java)

By the end of this unit, learners will be able to:

1. **Contrast Concurrency and Parallelism in the JVM**: Distinguish logical task interleaving (concurrency) from hardware simultaneous execution across multi-core CPUs (parallelism).
2. **Apply Immutability for Thread Safety**: Design robust, thread-safe domain objects using Java `record` classes, `final` fields, and unmodifiable defensive collections (`List.copyOf()`).
3. **Analyze Shared-Memory Concurrency**: Identify the hazards of shared mutable state and evaluate trade-offs between shared memory models vs actor/message-passing designs.
4. **Enforce Safe Publication**: Ensure newly constructed objects are safely published across threads without exposing partially initialized references.
