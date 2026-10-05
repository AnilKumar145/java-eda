# Unit 1.4 Exercises: Java Thread Communication

Master inter-thread signaling, wait/notify, and Producer-Consumer pipelines in isolation:
1. **Exercise 1: Single-Item Drop Box (Wait / Notify)** - Coordinate a producer placing an item into a 1-slot box and a consumer taking it.
2. **Exercise 2: Custom Bounded Queue** - Implement a thread-safe bounded circular queue using `wait()` and `notifyAll()`.
3. **Exercise 3: Multi-Worker Barrier Signal** - Release multiple waiting worker threads simultaneously when a gate is opened.

### Running & Compiling
Compile and execute using `javac`:
```bash
javac exercises/ThreadCommunicationExercises.java
java -ea -cp exercises ThreadCommunicationExercises
```

Or verify with solutions:
```bash
javac exercises/solutions/ThreadCommunicationSolutions.java
java -ea -cp exercises/solutions ThreadCommunicationSolutions
```
