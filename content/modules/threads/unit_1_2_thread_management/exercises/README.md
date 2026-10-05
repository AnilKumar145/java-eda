# Unit 1.2 Exercises: Java Thread Management

Master thread management, daemon threads, and cooperative interruption in isolation:
1. **Exercise 1: Cooperative Interruptible Counter** - Implement a thread that loops incrementing an integer, responding cleanly to `interrupt()`.
2. **Exercise 2: Daemon Watchdog Ping Engine** - Configure a daemon thread that continuously updates a timestamp without blocking JVM exit.
3. **Exercise 3: Timed Join Watchdog** - Coordinate a worker thread using `join(timeoutMs)` and interrupt it if it exceeds deadline.

### Running & Compiling
Compile and execute using `javac`:
```bash
javac exercises/ThreadManagementExercises.java
java -ea -cp exercises ThreadManagementExercises
```

Or verify with solutions:
```bash
javac exercises/solutions/ThreadManagementSolutions.java
java -ea -cp exercises/solutions ThreadManagementSolutions
```
