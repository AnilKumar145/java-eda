# Exercises: Unit 2.7 - Choosing the Right Concurrency Tool

Practice selecting and implementing the right concurrency primitive for various realistic application profiles.

## Exercises Overview:
1. **Exercise 1: Concurrency Tool Advisor**: Implement a recommendation engine matching use case characteristics to optimal Java concurrency tools.
2. **Exercise 2: Thread-Safe Metric Collector Selection**: Implement and compare a lock-based metric counter with an atomic counter under high-frequency updates.
3. **Exercise 3: Virtual Thread Task Dispatcher (Java 21)**: Implement a lightweight task runner dispatching simulated I/O tasks on virtual threads.

## How to Test:
Run:
```bash
javac -d .temp_bin content/modules/concurrency/unit_2_7_choosing_the_right_concurrency_model/exercises/solutions/ChoosingConcurrencyToolSolutions.java
java -ea -cp .temp_bin unit_2_7_choosing_the_right_concurrency_model.exercises.solutions.ChoosingConcurrencyToolSolutions
```
