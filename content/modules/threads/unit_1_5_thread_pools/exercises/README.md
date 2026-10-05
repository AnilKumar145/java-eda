# Unit 1.5 Exercises: Java Thread Pools & ExecutorService

Master thread pool execution, Callable/Future, and graceful shutdown in isolation:
1. **Exercise 1: Parallel Batch Square Calculator (Callable & Future)** - Submit a list of integers to a FixedThreadPool and gather computed square values via `Future.get()`.
2. **Exercise 2: Timed Multi-Service Scatter-Gather** - Execute multiple tasks with a strict timeout limit using `Future.get(timeout, unit)`.
3. **Exercise 3: Two-Phase Graceful Shutdown** - Implement robust two-phase termination for an `ExecutorService`.

### Running & Compiling
Compile and execute using `javac`:
```bash
javac exercises/ThreadPoolsExercises.java
java -ea -cp exercises ThreadPoolsExercises
```

Or verify with solutions:
```bash
javac exercises/solutions/ThreadPoolsSolutions.java
java -ea -cp exercises/solutions ThreadPoolsSolutions
```
