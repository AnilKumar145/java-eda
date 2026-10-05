# Unit 2.6: Concurrency Problems, Work-Stealing, and Patterns

---

## 1. Conceptual Foundation: The Gridlocked Intersection and The Busy Audit Office

To master concurrency, you must understand both the catastrophic failure modes (deadlock, livelock, starvation) and the high-performance parallel computation patterns (divide-and-conquer, work-stealing).

### Real-World Analogies:

- **Deadlock (The 4-Way Traffic Gridlock)**: Four cars arrive simultaneously at an intersection without traffic lights. Car A waits for Car B to clear; Car B waits for Car C; Car C waits for Car D; Car D waits for Car A. No car can move forward because each holds the road the other needs. All 4 drivers sit frozen forever.
- **Livelock (The Polite Corridor Dance)**: Two people walking in opposite directions meet in a narrow hallway. Person A steps to the right to make way; Person B steps to the left at the exact same instant, blocking Person A again. Both smile, apologize, and switch sides simultaneously. They repeat this dance 50 times. Neither is blocked or frozen (both are actively moving), but zero forward progress is made.
- **Starvation (The Ignored Bar Customer)**: A quiet patron stands at the bar counter waiting for a drink. The bartender serves whoever shouts loudest or waves money. Every time a new assertive customer arrives, the bartender serves them first. The quiet patron waits hours and never gets served.
- **Work-Stealing (The Team Audit Desk)**: A tax accounting office has 4 auditors. Auditor 1 is given a massive stack of 10,000 receipts, while Auditor 2, 3, and 4 get smaller stacks. When Auditor 2 finishes their pile, instead of sitting idle, they reach over to the bottom of Auditor 1's stack and steal half of the work. Every CPU core stays 100% occupied until the entire job is done.

---

## 2. The 4 Coffman Conditions of Deadlock

In 1971, computer scientist Edward G. Coffman Jr. proved that a deadlock can occur **if and only if all four** of the following conditions hold simultaneously:

```
+---------------------------------------------------------------------------------+
|                         THE 4 COFFMAN CONDITIONS                                |
+---------------------------------------------------------------------------------+
| 1. Mutual Exclusion  : At least one resource must be held in a non-shareable    |
|                        mode (only one thread can hold the lock at a time).      |
| 2. Hold and Wait     : A thread currently holds at least one lock while waiting |
|                        to acquire another lock held by another thread.          |
| 3. No Preemption     : Locks cannot be forcibly confiscated from a thread;      |
|                        only the holding thread can release it voluntarily.      |
| 4. Circular Wait     : A closed chain of threads exists where Thread A waits    |
|                        for Thread B, which waits for Thread C, which waits for A|
+---------------------------------------------------------------------------------+
```

### Breaking Deadlock: Global Lock Acquisition Ordering
You cannot eliminate Mutual Exclusion (locks exist to protect shared mutable state). But you can easily **break Circular Wait** by enforcing a strict global order on lock acquisition.

> **Rule**: If every thread in the JVM must acquire `Lock A` before `Lock B`, a circular cycle ($A \to B \to A$) is mathematically impossible!

---

## 3. Livelock vs. Starvation: Mechanisms & Solutions

| Problem | Thread State | CPU Usage | Root Cause | Solution |
| :--- | :--- | :--- | :--- | :--- |
| **Deadlock** | `WAITING` or `BLOCKED` | 0% CPU | Circular dependency on held locks | Global lock ordering, `tryLock(timeout)` |
| **Livelock** | `RUNNABLE` | 100% CPU | Over-compensating retry loops where threads react to each other simultaneously | Randomized exponential backoff (jitter) |
| **Starvation** | `WAITING` | 0% CPU | Unfair locking policies where greedy threads repeatedly acquire locks ahead of others | Use Fair Locks (`new ReentrantLock(true)`) |

---

## 4. The Fork/Join Framework & Work-Stealing Internals

Introduced in Java 7, the **Fork/Join framework** is designed for recursive, divide-and-conquer parallel algorithms that can be broken into independent subproblems.

### How Work-Stealing Operates:
1. Each worker thread in the `ForkJoinPool` maintains its own double-ended queue (**Deque**).
2. When a thread generates subtasks via `fork()`, it pushes them onto the **head** (top) of its own deque (LIFO order—cache friendly).
3. The owner thread pops tasks from the **head** of its deque.
4. When a thread's deque becomes empty, it becomes a "thief": it selects a random busy thread and steals a large chunk from the **tail** (bottom) of that thread's deque (FIFO order).

```
Worker Thread 1 (Owner):
   Pushes/Pops from HEAD (LIFO)
         |
         v
     +-------+-------+-------+-------+
     | Sub-1 | Sub-2 | Sub-3 | Sub-4 |  <--- Steals from TAIL (FIFO)
     +-------+-------+-------+-------+         ^
                                               |
                                     Worker Thread 2 (Thief)
```

Because the owner and the thief operate on opposite ends of the deque, lock contention between threads is minimized!

### `RecursiveTask<V>` vs `RecursiveAction`:
- `RecursiveTask<V>`: Computes a result and returns a value of type `V` via `.join()`.
- `RecursiveAction`: Performs a computation in-place without returning a result (like sorting an array).

---

## 5. Comprehensive Runnable Code Examples

### Example 1: Deadlock Creation vs. Global Lock Ordering Prevention

```java
package unit_2_6_concurrency_problems_and_patterns;

public class DeadlockPreventionDemo {
    public static class Account {
        private final int id;
        private int balance;

        public Account(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }

        public int getId() { return id; }

        public static void transfer(Account from, Account to, int amount) {
            // Determine global acquisition order by unique account ID
            Account firstLock = from.getId() < to.getId() ? from : to;
            Account secondLock = from.getId() < to.getId() ? to : from;

            synchronized (firstLock) {
                synchronized (secondLock) {
                    from.balance -= amount;
                    to.balance += amount;
                    System.out.printf("[Transfer] Account %d -> %d: $%d (Success)%n", 
                            from.getId(), to.getId(), amount);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Account acc1 = new Account(1, 1000);
        Account acc2 = new Account(2, 1000);

        // Thread A transfers 1 -> 2, Thread B transfers 2 -> 1 concurrently
        Thread t1 = new Thread(() -> Account.transfer(acc1, acc2, 100));
        Thread t2 = new Thread(() -> Account.transfer(acc2, acc1, 200));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Transfers completed without deadlock!");
    }
}
```
**Expected Output:**
```
[Transfer] Account 1 -> 2: $100 (Success)
[Transfer] Account 2 -> 1: $200 (Success)
Transfers completed without deadlock!
```

---

### Example 2: Recursive Divide-and-Conquer with `RecursiveTask`

```java
package unit_2_6_concurrency_problems_and_patterns;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinSumDemo {

    public static class ArraySumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 5000;
        private final long[] array;
        private final int start;
        private final int end;

        public ArraySumTask(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;
            // Base case: Small enough to compute sequentially
            if (length <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            }

            // Recursive case: Split workload in half
            int mid = start + length / 2;
            ArraySumTask leftTask = new ArraySumTask(array, start, mid);
            ArraySumTask rightTask = new ArraySumTask(array, mid, end);

            leftTask.fork(); // Asynchronously push left subtask to deque
            long rightResult = rightTask.compute(); // Compute right subtask in current thread
            long leftResult = leftTask.join(); // Wait for left subtask and retrieve result

            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) {
        long[] data = new long[20000];
        for (int i = 0; i < data.length; i++) data[i] = 1;

        ForkJoinPool pool = new ForkJoinPool();
        long totalSum = pool.invoke(new ArraySumTask(data, 0, data.length));

        System.out.println("Array Sum computed via ForkJoin: " + totalSum);
        assert totalSum == 20000;
        pool.shutdown();
    }
}
```
**Expected Output:**
```
Array Sum computed via ForkJoin: 20000
```

---

### Example 3: Parallel Streams vs Sequential Streams

```java
package unit_2_6_concurrency_problems_and_patterns;

import java.util.List;
import java.util.stream.LongStream;

public class ParallelStreamDemo {
    public static void main(String[] args) {
        long count = 5_000_000;

        // Sequential stream
        long t0 = System.currentTimeMillis();
        long sumSeq = LongStream.rangeClosed(1, count)
                .map(n -> n * 2)
                .sum();
        long timeSeq = System.currentTimeMillis() - t0;

        // Parallel stream utilizing multi-core ForkJoinPool.commonPool
        long t1 = System.currentTimeMillis();
        long sumPar = LongStream.rangeClosed(1, count)
                .parallel()
                .map(n -> n * 2)
                .sum();
        long timePar = System.currentTimeMillis() - t1;

        System.out.println("Sequential Time: " + timeSeq + "ms, Sum: " + sumSeq);
        System.out.println("Parallel Time:   " + timePar + "ms, Sum: " + sumPar);
    }
}
```
**Expected Output:**
```
Sequential Time: 42ms, Sum: 25000005000000
Parallel Time:   18ms, Sum: 25000005000000
```

---

### Example 4: Livelock Prevention using Exponential Backoff

```java
package unit_2_6_concurrency_problems_and_patterns;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LivelockBackoffDemo {
    private static final Lock lockA = new ReentrantLock();
    private static final Lock lockB = new ReentrantLock();
    private static final Random random = new Random();

    public static boolean tryTransferWithBackoff(String threadName, Lock first, Lock second) {
        int attempts = 0;
        while (attempts < 5) {
            attempts++;
            if (first.tryLock()) {
                try {
                    if (second.tryLock()) {
                        try {
                            System.out.println("[" + threadName + "] Acquired both locks on attempt " + attempts);
                            return true;
                        } finally {
                            second.unlock();
                        }
                    }
                } finally {
                    first.unlock();
                }
            }
            // Sleep random backoff duration (jitter) to prevent lockstep livelock
            try {
                Thread.sleep(random.nextInt(15) + 5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> tryTransferWithBackoff("Worker-1", lockA, lockB));
        Thread t2 = new Thread(() -> tryTransferWithBackoff("Worker-2", lockB, lockA));

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
```
**Expected Output:**
```
[Worker-1] Acquired both locks on attempt 1
[Worker-2] Acquired both locks on attempt 2
```

---

## 6. Architecture & Implementation Comparison

| Mechanism | Ideal Workload | Overhead | Scheduling Model |
| :--- | :--- | :--- | :--- |
| **Sequential Loop** | Small datasets ($N < 10,000$), simple math | None (Zero context switches) | Single core |
| **`ThreadPoolExecutor`** | Coarse-grained, independent I/O tasks | Thread queue synchronization | Task queue dispatch |
| **`ForkJoinPool`** | Fine-grained, CPU-intensive divide-and-conquer | Low (Work-stealing minimizes contention) | Deque work-stealing |
| **Parallel Streams** | Large in-memory collections with cheap stateless operations | Medium (Spliterator overhead + common pool sharing) | Under-the-hood `ForkJoinPool` |

---

## 7. Real-World Industry Use Cases

### 1. Healthcare: Parallel Medical Imaging (DICOM) Tile Filter
A 4K resolution medical scan (e.g. Brain MRI, 4096 $\times$ 4096 pixels) contains 16 million pixels. Applying edge detection or contrast enhancement filters sequentially takes 800ms. A `RecursiveAction` recursively tiles the image into quadrants until each block is $256 \times 256$ pixels. On an 8-core CPU, the work-stealing pool processes all tiles in 110ms, rendering real-time scans for the radiologist.

### 2. eCommerce: Real-Time Catalog Price & Inventory Indexing
An online marketplace has 2,000,000 product SKUs. Daily price adjustments and discount rules must be recalculated when promotions activate. A parallel stream filters out-of-stock items, applies currency conversion, and groups products by category in seconds across all available CPU cores.

### 3. Banking: Anti-Money-Laundering (AML) Graph Traversal
To detect cyclic money laundering schemes (Account A transfers to B, B to C, C to A across offshore shell companies), the compliance engine performs parallel recursive depth-first search (DFS) using `ForkJoinTask`. Work-stealing prevents slow branch exploration from blocking fast branches.

---

## 8. Best Practices, Anti-Patterns, and Top 3 Mistakes

### Best Practices:
1. **Always use global lock ordering**: When acquiring multiple locks, sort them by a natural unique identifier (e.g. UUID, ID, or `System.identityHashCode`).
2. **Proper Fork/Join order**: Always call `left.fork()`, followed by `right.compute()`, and finally `left.join()`. This ensures the current thread stays busy executing `right` while another worker steals `left`.
3. **Avoid parallel streams for I/O**: Parallel streams execute on `ForkJoinPool.commonPool()`. If you block on database queries or network HTTP calls inside a parallel stream, you exhaust threads for all other parts of your application!

### Top 3 Mistakes Developers Make:

#### Mistake 1: Forking Both Tasks (`fork()` + `fork()`)
```java
// WRONG: Wastes current thread cycles!
leftTask.fork();
rightTask.fork(); // Pushes both to queue; current thread sits idle waiting!
Long right = rightTask.join();
Long left = leftTask.join();

// CORRECT: Fork one, compute one in current thread!
leftTask.fork();
Long right = rightTask.compute(); // Current thread does real work!
Long left = leftTask.join();
```

#### Mistake 2: Stateful Lambdas in Parallel Streams
```java
// DANGEROUS: Race condition! ArrayList is not thread-safe!
List<Integer> results = new ArrayList<>();
numbers.parallelStream().map(n -> n * 2).forEach(results::add); // CORRUPTS LIST!

// CORRECT: Use reduction or thread-safe collectors
List<Integer> results = numbers.parallelStream().map(n -> n * 2).toList();
```

#### Mistake 3: Locking in Inconsistent Order
```java
// Thread 1:
synchronized (lockA) { synchronized (lockB) { ... } }
// Thread 2:
synchronized (lockB) { synchronized (lockA) { ... } } // DEADLOCK!
```
- **Fix**: Enforce `idA < idB ? lockA : lockB`.
