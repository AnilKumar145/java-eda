# Exercises: Unit 2.4 - Atomic Operations and Memory

Practice lock-free programming, CAS mechanics, and memory visibility in Java.

## Exercises Overview:
1. **Exercise 1: Lock-Free Decrement Counter**: Implement an inventory decrementing counter using `AtomicInteger` that rejects when stock falls below zero.
2. **Exercise 2: Atomic State Machine**: Implement a circuit breaker state transition using `AtomicReference<State>`.
3. **Exercise 3: Volatile Graceful Worker**: Implement a background polling worker controlled by a `volatile boolean` shutdown flag.

## How to Test:
Run:
```bash
javac -d .temp_bin content/modules/concurrency/unit_2_4_atomic_operations_and_memory/exercises/solutions/AtomicOperationsSolutions.java
java -ea -cp .temp_bin unit_2_4_atomic_operations_and_memory.exercises.solutions.AtomicOperationsSolutions
```
