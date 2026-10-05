# Learning Outcomes: Unit 1.3 Thread Synchronization (Java)

By the end of this unit, learners will be able to:

1. **Eliminate Race Conditions**: Identify critical sections in multi-threaded code and protect shared mutable state using `synchronized` methods and fine-grained `synchronized(lock)` blocks.
2. **Explain Intrinsic Monitors**: Understand the Java object header's Mark Word and how every Java `Object` acts as an intrinsic lock/monitor supporting reentrancy.
3. **Apply the `volatile` Keyword**: Enforce cross-thread variable visibility and prevent JVM instruction reordering without paying the performance cost of heavyweight mutual exclusion locks.
4. **Utilize Atomic Variables**: Replace locking with lock-free hardware Compare-And-Swap (CAS) instructions using `java.util.concurrent.atomic.AtomicInteger` and `AtomicBoolean`.
