# Exercises: Unit 2.5 - Advanced Task Execution

Practice `Callable`, `Future`, and non-blocking asynchronous programming with `CompletableFuture`.

## Exercises Overview:
1. **Exercise 1: Callable Factorial with Timeout**: Implement a `Callable<Long>` factorial calculation submitted to an `ExecutorService` and retrieved via `Future.get(timeout)`.
2. **Exercise 2: Async Transformation Pipeline**: Implement a 3-stage `CompletableFuture` pipeline transforming a raw user input into an encrypted token.
3. **Exercise 3: Parallel Service Merge**: Implement `thenCombine` to merge two independent microservice calls with fallback error handling.

## How to Test:
Run:
```bash
javac -d .temp_bin content/modules/concurrency/unit_2_5_advanced_task_execution/exercises/solutions/AdvancedTaskExecutionSolutions.java
java -ea -cp .temp_bin unit_2_5_advanced_task_execution.exercises.solutions.AdvancedTaskExecutionSolutions
```
