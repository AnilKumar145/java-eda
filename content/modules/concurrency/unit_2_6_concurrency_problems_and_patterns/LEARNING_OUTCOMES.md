# Learning Outcomes: Unit 2.6 - Concurrency Problems and Patterns

By the end of this unit, you will be able to:
1. **Diagnose and Prevent Deadlocks**: Analyze thread dumps to detect cyclic lock dependencies and implement strict global lock acquisition ordering to break the Coffman circular wait condition.
2. **Distinguish Deadlock, Livelock, and Starvation**: Recognize when threads are stuck blocked (deadlock), continuously reacting without progress (livelock), or denied CPU scheduling due to priority bias (starvation).
3. **Master the Fork/Join Framework**: Implement divide-and-conquer parallel algorithms using `RecursiveTask<V>` and `ForkJoinPool`.
4. **Explain Work-Stealing Internals**: Describe how idle worker threads steal tasks from the tails of busy threads' double-ended queues (deques) to balance multi-core CPU loads.
5. **Utilize Parallel Streams Safely**: Identify appropriate workloads for `.parallelStream()`, avoiding stateful lambdas and blocking I/O on the common pool.
