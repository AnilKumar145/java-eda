# Unit 2.1 Exercises: Java Concurrency Fundamentals

Master immutability, thread-safe records, and defensive copying in isolation:
1. **Exercise 1: Immutable Domain Record** - Implement a thread-safe record with a defensive copy constructor.
2. **Exercise 2: Thread-Safe State Snapshot Container** - Implement an atomic reference container that transitions between immutable states without locks.
3. **Exercise 3: Safe Multi-Threaded Read Benchmark** - Demonstrate concurrent lock-free reads across multiple threads.

### Running & Compiling
Compile and execute using `javac`:
```bash
javac exercises/JavaConcurrencyFundamentalsExercises.java
java -ea -cp exercises JavaConcurrencyFundamentalsExercises
```

Or verify with solutions:
```bash
javac exercises/solutions/JavaConcurrencyFundamentalsSolutions.java
java -ea -cp exercises/solutions JavaConcurrencyFundamentalsSolutions
```
