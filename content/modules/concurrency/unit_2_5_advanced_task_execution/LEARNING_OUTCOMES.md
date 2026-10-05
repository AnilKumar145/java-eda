# Learning Outcomes: Unit 2.5 - Advanced Task Execution

By the end of this unit, you will be able to:
1. **Differentiate `Callable<V>` and `Runnable`**: Return typed computed values and propagate checked exceptions out of worker threads.
2. **Understand `Future<V>` Limitations**: Recognize why blocking `.get()` calls stall threads and reduce concurrency scalability.
3. **Build Non-Blocking Pipelines with `CompletableFuture`**: Chain reactive stages using `thenApply`, `thenAccept`, and `thenRun`.
4. **Compose and Combine Parallel Futures**: Merge independent asynchronous operations using `thenCombine` and aggregate lists using `CompletableFuture.allOf()`.
5. **Handle Asynchronous Failures Gracefully**: Implement fallback values and circuit breakers using `.exceptionally()`, `.handle()`, and timeout guards.
