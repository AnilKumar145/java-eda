# Learning Outcomes: Unit 2.3 Concurrent Collections (Java)

By the end of this unit, learners will be able to:

1. **Evaluate Thread-Safe Collection Strategies**: Contrast legacy synchronized wrappers (`Collections.synchronizedMap`) with modern lock-striped `ConcurrentHashMap` and snapshot-copying `CopyOnWriteArrayList`.
2. **Eliminate ConcurrentModificationException**: Perform safe concurrent iteration over collections without holding coarse-grained read locks or freezing writers.
3. **Apply Atomic Compute Methods in ConcurrentHashMap**: Utilize `computeIfAbsent()`, `merge()`, and `putIfAbsent()` to guarantee atomic state updates without manual external locking.
4. **Select Appropriate BlockingQueues**: Differentiate between bounded array-backed queues (`ArrayBlockingQueue`), linked node queues (`LinkedBlockingQueue`), and direct handoffs (`SynchronousQueue`).
