# Unit 2.2 Exercises: Java Concurrency Utilities

Master explicit locks, semaphores, and latches in isolation:
1. **Exercise 1: Deadlock-Free Timed Lock (ReentrantLock)** - Acquire two contested locks using `tryLock(timeout)`.
2. **Exercise 2: Bounded Resource Throttler (Semaphore)** - Limit concurrent operations to at most N active permits.
3. **Exercise 3: Parallel Rendezvous Latch (CountDownLatch)** - Await completion of N worker threads using `CountDownLatch`.

### Running & Compiling
Compile and execute using `javac`:
```bash
javac exercises/JavaConcurrencyUtilitiesExercises.java
java -ea -cp exercises JavaConcurrencyUtilitiesExercises
```

Or verify with solutions:
```bash
javac exercises/solutions/JavaConcurrencyUtilitiesSolutions.java
java -ea -cp exercises/solutions JavaConcurrencyUtilitiesSolutions
```
