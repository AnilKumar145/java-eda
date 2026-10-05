# Learning Outcomes: Unit 2.7 - Choosing the Right Concurrency Tool

By the end of this unit, you will be able to:
1. **Navigate the Concurrency Decision Matrix**: Choose the optimal synchronization primitive or data structure based on read/write ratio, thread contention, and task latency.
2. **Evaluate Lock vs Lock-Free Trade-offs**: Contrast `AtomicInteger` with `ReentrantLock` and `synchronized` through profiling and benchmarking.
3. **Select Appropriate Collection Architectures**: Avoid the bottleneck of `Collections.synchronizedMap` in favor of segment-striped `ConcurrentHashMap` or `CopyOnWriteArrayList`.
4. **Distinguish Parallelism vs Asynchrony**: Select between `CompletableFuture` (for latency hiding and async I/O) and `ForkJoinPool` / Parallel Streams (for CPU-bound throughput).
5. **Understand Java 21 Virtual Threads**: Explain how lightweight user-mode fibers eliminate traditional OS thread exhaustion in high-concurrency microservices.
