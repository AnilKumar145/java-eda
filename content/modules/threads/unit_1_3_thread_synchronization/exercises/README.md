# Unit 1.3 Exercises: Java Thread Synchronization

Master race condition elimination, synchronization blocks, and atomic variables in isolation:
1. **Exercise 1: Thread-Safe Bank Vault (Synchronized Block)** - Protect deposit and withdrawal operations with a private lock.
2. **Exercise 2: High-Throughput Request Counter (AtomicInteger)** - Build a lock-free request counter that survives concurrent updates across 20 threads.
3. **Exercise 3: Volatile Graceful Stopper** - Coordinate a worker loop using a `volatile boolean` flag.

### Running & Compiling
Compile and execute using `javac`:
```bash
javac exercises/ThreadSynchronizationExercises.java
java -ea -cp exercises ThreadSynchronizationExercises
```

Or verify with solutions:
```bash
javac exercises/solutions/ThreadSynchronizationSolutions.java
java -ea -cp exercises/solutions ThreadSynchronizationSolutions
```
