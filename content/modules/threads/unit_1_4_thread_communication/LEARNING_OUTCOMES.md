# Learning Outcomes: Unit 1.4 Thread Communication (Java)

By the end of this unit, learners will be able to:

1. **Implement Monitor Signaling Protocols**: Understand and correctly invoke `Object.wait()`, `notify()`, and `notifyAll()` strictly from within synchronized context.
2. **Prevent Spurious Wakeups**: Always enclose `wait()` calls within `while (!condition)` loops rather than `if` statements to defend against operating system spurious wakeups.
3. **Build Bounded Producer–Consumer Pipelines**: Implement custom synchronized circular buffers that coordinate producer and consumer threads without spinning or CPU polling.
4. **Transition to Modern Concurrent Queues**: Compare low-level wait/notify monitors with modern high-level `java.util.concurrent.BlockingQueue` implementations (`ArrayBlockingQueue`, `LinkedBlockingQueue`).
