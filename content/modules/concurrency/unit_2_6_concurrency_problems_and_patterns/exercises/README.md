# Exercises: Unit 2.6 - Concurrency Problems and Patterns

Practice deadlock prevention, work-stealing with Fork/Join, and parallel streams in Java.

## Exercises Overview:
1. **Exercise 1: Deadlock-Free Resource Transfer**: Implement a deadlock-free transfer between two resource accounts using consistent ID-based lock ordering.
2. **Exercise 2: Fork/Join Array Maximum**: Implement a `RecursiveTask<Integer>` that finds the maximum value in an array using divide-and-conquer.
3. **Exercise 3: Safe Parallel Stream Aggregation**: Use `.parallelStream()` with stateless map and reduction to aggregate values safely without side effects.

## How to Test:
Run:
```bash
javac -d .temp_bin content/modules/concurrency/unit_2_6_concurrency_problems_and_patterns/exercises/solutions/ConcurrencyProblemsSolutions.java
java -ea -cp .temp_bin unit_2_6_concurrency_problems_and_patterns.exercises.solutions.ConcurrencyProblemsSolutions
```
