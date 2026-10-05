# Learning Outcomes: Unit 1.5 Thread Pools (Java)

By the end of this unit, learners will be able to:

1. **Leverage the Executor Framework**: Eliminate thread creation overhead by decoupling task submission from thread management using `ExecutorService` and `Executors` factory methods.
2. **Execute Valued Tasks with `Callable` and `Future`**: Return values and checked exceptions from asynchronous worker tasks using `Callable<V>`, handling results with `Future.get(timeout, unit)`.
3. **Select Optimal Pool Configurations**: Distinguish between Fixed, Cached, and Scheduled thread pools, sizing pool boundaries to prevent OutOfMemoryError and thread starvation.
4. **Implement Two-Phase Graceful Shutdown**: Teardown thread pools cleanly using `shutdown()`, `awaitTermination()`, and `shutdownNow()`, ensuring zero dropped tasks and zero zombie worker threads.
