# Unit 2.3 Exercises: Java Concurrent Collections

Master lock-striped maps, CopyOnWrite lists, and blocking queues in isolation:
1. **Exercise 1: Atomic Frequency Counter (ConcurrentHashMap)** - Count occurrences of words across concurrent worker threads using `merge()` or `compute()`.
2. **Exercise 2: Thread-Safe Listener Registry (CopyOnWriteArrayList)** - Register and dispatch events to listeners without `ConcurrentModificationException`.
3. **Exercise 3: Producer-Consumer Pipe (ArrayBlockingQueue)** - Coordinate buffered handoffs between threads using `put()` and `take()`.

### Running & Compiling
Compile and execute using `javac`:
```bash
javac exercises/ConcurrentCollectionsExercises.java
java -ea -cp exercises ConcurrentCollectionsExercises
```

Or verify with solutions:
```bash
javac exercises/solutions/ConcurrentCollectionsSolutions.java
java -ea -cp exercises/solutions ConcurrentCollectionsSolutions
```
