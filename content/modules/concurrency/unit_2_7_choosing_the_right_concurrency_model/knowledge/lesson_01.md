# Unit 2.7: Choosing the Right Concurrency Tool

---

## 1. Conceptual Foundation: The Fleet of Vehicles

Imagine you are a logistics director in charge of moving cargo across a country. You have an entire motor pool of different vehicles:

- **A Bicycle (`volatile`)**: Extremely lightweight, fast for a single courier carrying a small envelope (a single boolean flag or state marker). Useless if you need to load 50 heavy crates.
- **A Family Sedan (`synchronized`)**: Simple, built into everyday life, seats the whole family safely. Reliable for standard, straightforward protection without external machinery.
- **A Semi-Truck with Air Brakes (`ReentrantLock`)**: Has advanced gears, timed brakes (`tryLock`), fairness settings, and condition horns. You use it when the journey is complex and you need precise control.
- **A Bullet Train (`ConcurrentHashMap` / `BlockingQueue`)**: High-speed, multi-track, partitioned cargo transit. Hundreds of passengers board simultaneously without waiting in a single queue.
- **A Fleet of Drones (`ForkJoinPool` / Parallel Streams)**: Slices one massive shipment of 100,000 packages into tiny parcels and flies them out across all skies at once using CPU cores.
- **A Million Hovercrafts (Java 21 Virtual Threads)**: Ultra-cheap, microscopic crafts that float on air. If one gets stuck waiting for a bridge to open, the pilot unmounts instantly and lets another hovercraft fly.

Using a Semi-Truck to deliver a single letter is overkill; using a Bicycle to transport 50 tons will crash the vehicle. Choosing the right concurrency tool is about **matching the mechanism to the problem**.

---

## 2. The Master Java Concurrency Decision Tree

```
                       Do you have Shared Mutable State?
                                   /        \
                             [NO] /          \ [YES]
                                 v            v
                 Use Immutability (Records)   What kind of operation?
                 or ThreadLocal variables.        /              \
                                                 /                \
                       [Single Primitive/Reference]            [Complex Section or Collection]
                                  |                                     |
                Read-heavy flag?  | Compound update?       Collection?  |  Critical section?
               +------------------+---------------+       +-------------+--------------+
               |                                  |       |                            |
               v                                  v       v                            v
          `volatile`                      `AtomicInteger` `ConcurrentHashMap`    `synchronized`
                                          `AtomicReference` `BlockingQueue`       or `ReentrantLock`
                                          `LongAdder`     `CopyOnWriteArrayList`
```

### When Choosing Execution & Asynchrony:
```
                              What kind of Workload?
                                   /         \
                         [I/O-Bound]         [CPU-Bound]
                        (Network, DB)       (Math, Image, Search)
                             /                     \
                            v                       v
               `CompletableFuture`            Divide-and-Conquer?
               or Java 21 Virtual Threads         /           \
                                            [YES] /           \ [NO]
                                                 v             v
                                           `ForkJoinPool`   Parallel Streams
                                          (`RecursiveTask`) or `FixedThreadPool`
```

---

## 3. Tool Trade-offs and Decision Matrix

| Requirement | Recommended Tool | Why It Fits | What to Avoid |
| :--- | :--- | :--- | :--- |
| **Simple on/off boolean flag** | `volatile boolean` | Zero overhead; immediate CPU memory barrier visibility | Don't use `synchronized` |
| **High-throughput counter** | `AtomicLong` or `LongAdder` | Non-blocking hardware CAS instructions | Don't use `synchronized int count++` |
| **Shared Key-Value Map** | `ConcurrentHashMap` | Segment/bucket level lock striping allows concurrent writers | Don't use `Collections.synchronizedMap` |
| **Producer-Consumer Buffer** | `ArrayBlockingQueue` | Built-in thread suspension when full/empty via Condition | Don't use `ArrayList` with manual `wait()` |
| **Read-heavy, rare-write list** | `CopyOnWriteArrayList` | Readers never lock; snapshot copy on write | Don't use for high-frequency writes |
| **Advanced locking with timeout** | `ReentrantLock` | `tryLock(timeout)`, `lockInterruptibly()`, Fairness | Don't use when basic `synchronized` suffices |
| **Parallel multi-stage async I/O** | `CompletableFuture` | Non-blocking reactive callbacks (`thenCombine`, `allOf`) | Don't use blocking `Future.get()` |
| **Parallel large array math** | `ForkJoinPool` / Parallel Stream | Work-stealing balances CPU core utilization | Don't use for blocking HTTP / DB calls |
| **Thousands of blocking sockets** | **Java 21 Virtual Threads** | Lightweight fiber unmounts OS thread during I/O blocking | Don't allocate 10,000 OS platform threads |

---

## 4. Modern Innovation: Java 21 Virtual Threads (Project Loom)

In traditional Java, every `java.lang.Thread` is a **Platform Thread** mapped 1:1 to an operating system kernel thread.
- Memory: Each platform thread consumes $\sim 1\text{ MB}$ of memory for its call stack.
- Limit: A server typically tops out at 2,000 to 5,000 platform threads before throwing `OutOfMemoryError: unable to create native thread`.

### The Java 21 Virtual Thread Revolution:
Java 21 introduces **Virtual Threads** (`Thread.ofVirtual()`).
- Virtual threads are lightweight user-space threads managed entirely by the Java runtime, **not the operating system**.
- Memory: Just a few hundred bytes per thread!
- Scale: You can spawn **1,000,000 concurrent virtual threads** on a normal laptop without breaking a sweat!
- **Under the Hood**: When a virtual thread performs a blocking I/O operation (e.g. `InputStream.read()`, JDBC query, `Thread.sleep()`), the JVM automatically **unmounts** it from the underlying carrier platform thread, freeing that CPU core to run other tasks. When the I/O finishes, the virtual thread is rescheduled.

```java
// Java 21: Spawning 10,000 lightweight virtual threads effortlessly
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10_000; i++) {
        executor.submit(() -> {
            Thread.sleep(1000); // Does NOT block OS kernel thread!
            return "done";
        });
    }
} // Automatically awaits completion
```

---

## 5. Comprehensive Runnable Code Examples

### Example 1: Contention Benchmark — `synchronized` vs `ReentrantLock` vs `AtomicLong`

```java
package unit_2_7_choosing_the_right_concurrency_model;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContentionBenchmarkDemo {

    private static long syncVal = 0;
    private static final Object syncLock = new Object();

    private static long reentrantVal = 0;
    private static final Lock rLock = new ReentrantLock();

    private static final AtomicLong atomicVal = new AtomicLong(0);

    public static void main(String[] args) throws Exception {
        int threads = 8;
        int iterations = 200_000;

        // 1. Benchmark synchronized
        long t0 = System.currentTimeMillis();
        runBenchmark(threads, () -> {
            for (int i = 0; i < iterations; i++) {
                synchronized (syncLock) { syncVal++; }
            }
        });
        long syncTime = System.currentTimeMillis() - t0;

        // 2. Benchmark ReentrantLock
        long t1 = System.currentTimeMillis();
        runBenchmark(threads, () -> {
            for (int i = 0; i < iterations; i++) {
                rLock.lock();
                try { reentrantVal++; } finally { rLock.unlock(); }
            }
        });
        long lockTime = System.currentTimeMillis() - t1;

        // 3. Benchmark AtomicLong
        long t2 = System.currentTimeMillis();
        runBenchmark(threads, () -> {
            for (int i = 0; i < iterations; i++) {
                atomicVal.incrementAndGet();
            }
        });
        long atomicTime = System.currentTimeMillis() - t2;

        System.out.printf("synchronized:  %d ms (Total: %d)%n", syncTime, syncVal);
        System.out.printf("ReentrantLock: %d ms (Total: %d)%n", lockTime, reentrantVal);
        System.out.printf("AtomicLong:    %d ms (Total: %d)%n", atomicTime, atomicVal.get());
    }

    private static void runBenchmark(int threads, Runnable task) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try { task.run(); } finally { latch.countDown(); }
            });
        }
        latch.await();
        pool.shutdown();
    }
}
```
**Expected Output:**
```
synchronized:  32 ms (Total: 1600000)
ReentrantLock: 28 ms (Total: 1600000)
AtomicLong:    15 ms (Total: 1600000)
```

---

### Example 2: Collection Throughput: `ConcurrentHashMap` vs `Collections.synchronizedMap`

```java
package unit_2_7_choosing_the_right_concurrency_model;

import java.util.*;
import java.util.concurrent.*;

public class CollectionComparisonDemo {
    public static void main(String[] args) throws Exception {
        int threads = 10;
        int operationsPerThread = 50_000;

        // Synchronized Map (Global lock on every read and write)
        Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
        long tSync = benchmarkMap(threads, operationsPerThread, syncMap);

        // ConcurrentHashMap (Lock-free reads + Stripe-locked writes)
        Map<String, Integer> chm = new ConcurrentHashMap<>();
        long tChm = benchmarkMap(threads, operationsPerThread, chm);

        System.out.printf("Collections.synchronizedMap: %d ms%n", tSync);
        System.out.printf("ConcurrentHashMap:           %d ms (Speedup: %.1fx)%n", 
                tChm, (double) tSync / Math.max(tChm, 1));
    }

    private static long benchmarkMap(int threads, int ops, Map<String, Integer> map) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        long start = System.currentTimeMillis();

        for (int i = 0; i < threads; i++) {
            final int tId = i;
            pool.submit(() -> {
                try {
                    for (int j = 0; j < ops; j++) {
                        String key = "KEY_" + (j % 50);
                        map.put(key, tId);
                        map.get(key);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();
        return System.currentTimeMillis() - start;
    }
}
```
**Expected Output:**
```
Collections.synchronizedMap: 78 ms
ConcurrentHashMap:           24 ms (Speedup: 3.2x)
```

---

### Example 3: Java 21 Virtual Threads in Action

```java
package unit_2_7_choosing_the_right_concurrency_model;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadsDemo {
    public static void main(String[] args) throws Exception {
        int taskCount = 10_000;
        AtomicInteger completedTasks = new AtomicInteger(0);

        long start = System.currentTimeMillis();
        // Java 21 newVirtualThreadPerTaskExecutor creates virtual threads on the fly
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                executor.submit(() -> {
                    // Simulate blocking I/O (e.g. database query)
                    Thread.sleep(50);
                    completedTasks.incrementAndGet();
                    return null;
                });
            }
        } // Auto-closes and waits for all 10,000 tasks to finish!

        long duration = System.currentTimeMillis() - start;
        System.out.printf("Successfully executed %d Virtual Threads in %d ms!%n", 
                completedTasks.get(), duration);
    }
}
```
**Expected Output:**
```
Successfully executed 10000 Virtual Threads in 120 ms!
```

---

### Example 4: The Tool Selector Pattern

```java
package unit_2_7_choosing_the_right_concurrency_model;

public class ConcurrencyToolSelectorDemo {

    public enum WorkloadType {
        SINGLE_FLAG,
        HIGH_CONTENTION_COUNTER,
        FREQUENT_READ_SHARED_CACHE,
        CPU_BOUND_RECURSIVE_MATH,
        MASSIVE_CONCURRENT_IO
    }

    public static String recommendTool(WorkloadType type) {
        return switch (type) {
            case SINGLE_FLAG -> "Use 'volatile boolean'. Lightweight, guarantees visibility without locking.";
            case HIGH_CONTENTION_COUNTER -> "Use 'LongAdder'. Stripes counts across cells to avoid CAS contention.";
            case FREQUENT_READ_SHARED_CACHE -> "Use 'ConcurrentHashMap'. Provides lock-free non-blocking reads.";
            case CPU_BOUND_RECURSIVE_MATH -> "Use 'ForkJoinPool' with 'RecursiveTask'. Maximizes core work-stealing.";
            case MASSIVE_CONCURRENT_IO -> "Use 'Java 21 Virtual Threads' or 'CompletableFuture' on dedicated I/O pool.";
        };
    }

    public static void main(String[] args) {
        for (WorkloadType workload : WorkloadType.values()) {
            System.out.printf("[%s] -> %s%n", workload, recommendTool(workload));
        }
    }
}
```
**Expected Output:**
```
[SINGLE_FLAG] -> Use 'volatile boolean'. Lightweight, guarantees visibility without locking.
[HIGH_CONTENTION_COUNTER] -> Use 'LongAdder'. Stripes counts across cells to avoid CAS contention.
[FREQUENT_READ_SHARED_CACHE] -> Use 'ConcurrentHashMap'. Provides lock-free non-blocking reads.
[CPU_BOUND_RECURSIVE_MATH] -> Use 'ForkJoinPool' with 'RecursiveTask'. Maximizes core work-stealing.
[MASSIVE_CONCURRENT_IO] -> Use 'Java 21 Virtual Threads' or 'CompletableFuture' on dedicated I/O pool.
```

---

## 6. Architecture & Implementation Comparison Matrix

| Technology | Latency | Scalability | Complexity | Memory Footprint |
| :--- | :--- | :--- | :--- | :--- |
| **`synchronized`** | Low (uncontended) | Moderate | Very Low | Minimal |
| **`ReentrantLock`** | Low | High | Medium | 1 object allocation |
| **`AtomicLong` / CAS** | Ultra-Low | Very High (Low Contention) | Low | 1 object allocation |
| **`ConcurrentHashMap`** | Ultra-Low (Reads) | Extremely High | Low | Moderate |
| **`CompletableFuture`** | Low (Async) | Extremely High | Medium-High | Stage chain objects |
| **Virtual Threads (Java 21)**| Low | **Highest** ($10^6$ threads) | Very Low | $\sim 1\text{ KB}$ per thread |

---

## 7. Real-World Industry Use Cases

### 1. Healthcare: High-Throughput EHR Telemetry Broker
A hospital network aggregates vital telemetry from 50,000 bedside patient monitors. By leveraging Java 21 Virtual Threads combined with `ConcurrentHashMap`, each incoming monitor connection is assigned a dedicated virtual thread that parses telemetry packets and stores current patient vitals in striped buckets without memory exhaustion.

### 2. eCommerce: Distributed Cart Synchronization
During checkout, shopping carts require strict atomic validation to prevent negative warehouse stock. The service uses `AtomicInteger` CAS for inventory counts, `ConcurrentHashMap` for session caching, and `CompletableFuture` for fan-out payment and tax calculation.

### 3. Banking: High-Frequency Matching Engine
A financial stock exchange matching engine requires sub-microsecond transaction latency. Using coarse-grained locks would induce catastrophic jitter. The engine combines `LongAdder` for trade volume metrics, lock-free ring buffers (`Disruptor` pattern using CAS), and `CopyOnWriteArrayList` for infrequently updated exchange rule observers.

---

## 8. Best Practices, Anti-Patterns, and Top 3 Mistakes

### Best Practices:
1. **Prefer Immutability**: If an object's fields are `final` and never change after construction, it is automatically thread-safe without locks, atomics, or volatile keywords.
2. **Profile Before Over-Engineering**: Do not reach for complex lock-free algorithms when a simple `synchronized` method or `AtomicInteger` achieves the target latency.
3. **Use Modern Java 21 Features**: In Java 21+, prefer Virtual Threads for synchronous-style blocking I/O instead of building convoluted asynchronous callback pyramids.

### Top 3 Mistakes Developers Make:

#### Mistake 1: Using `Collections.synchronizedMap` in High-Throughput Web Services
A single global lock on every `get()` and `put()` serializes all web request threads into a single file line.
- **Fix**: Always migrate to `ConcurrentHashMap`.

#### Mistake 2: Blocking Inside Parallel Streams
Executing database or network operations inside `.parallelStream()` occupies the global `ForkJoinPool.commonPool()`, which starves unrelated parallel streams across the entire JVM.
- **Fix**: Use `CompletableFuture` with a dedicated, bounded `ThreadPoolExecutor` or Java 21 Virtual Threads.

#### Mistake 3: Unbounded Thread Creation
Spawning `new Thread(task).start()` on every incoming request will quickly exhaust OS file descriptors and cause JVM memory crash (`OutOfMemoryError`).
- **Fix**: Always constrain concurrency with an `ExecutorService` or switch to Java 21 Virtual Threads.
