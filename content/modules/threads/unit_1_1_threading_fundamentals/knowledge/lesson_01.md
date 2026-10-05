# Unit 1.1: Java Threading Fundamentals

---

## 1. What

### Simple Definition
Imagine a large manufacturing factory:
- The **Factory Building (Process)**: Represents the entire operating system program. It has its own private plot of land, its own security gate, and its own storage warehouse (Process Memory Heap).
- The **Factory Workers (Threads)**: Are individual workers moving around inside the factory. All workers share the same warehouse tools, supply bins, and cafeteria (Shared Heap Memory). However, each worker wears their own backpack containing their private notebook and pencil (Private Thread Stack and Program Counter).

When you double-click a Java application, the operating system launches a **Process** (running the Java Virtual Machine / JVM). Inside that process, the JVM automatically starts the **Main Thread**, which begins executing your `public static void main(String[] args)` method. 

By creating additional threads, your Java program can perform multiple tasks concurrently—such as listening for incoming network requests, saving patient records to a database, and updating a dashboard screen all at the exact same time.

### The Problems Threading Solves
1. **Preventing Frozen Applications**: If you click "Export PDF" in a desktop or enterprise web application and the code runs sequentially on the main thread, the entire application freezes for 10 seconds. Spawning a worker thread allows the PDF export to run in the background while the UI remains instant and responsive.
2. **Utilizing Modern Multi-Core Hardware**: Unlike Python (which has a Global Interpreter Lock), **Java threads are true 1-to-1 native operating system kernel threads**. When you start 8 threads on an 8-core CPU in Java, the operating system scheduler runs them in true physical parallel across all 8 cores!
3. **Overlapping Slow I/O Calls**: When your application waits for a remote database or external REST API to reply, the CPU is completely idle. Creating threads allows the CPU to switch to other pending tasks while waiting for network responses.

### Key Terms You Must Know
- **Thread**: The smallest sequence of instructions executed independently by the operating system.
- **Main Thread**: The initial thread created by the JVM to execute the `main()` method.
- **`Runnable`**: A Java functional interface with a single method: `void run()`. It defines the job to be done.
- **`Thread.start()`**: The method that asks the operating system to create a new native execution thread.
- **`Thread.join()`**: Pauses the calling thread until the target thread finishes executing.

---

## 2. Examples

Let us examine 4 complete, runnable Java examples building from simple syntax to production-grade patterns.

### Example 1: Creating a Thread via `Runnable` Lambda (Recommended Approach)
Implementing `Runnable` via a lambda expression is the cleanest and most common way to create threads in modern Java.

```java
public class BasicThreadExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("[" + Thread.currentThread().getName() + "] Main thread starting...");

        // Create a new thread using a Runnable lambda
        Thread worker = new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("  [" + threadName + "] Worker thread is doing background work...");
            try {
                Thread.sleep(500); // Simulate 0.5s of work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("  [" + threadName + "] Worker finished!");
        }, "BackgroundWorker-1");

        // Start thread (allocates OS resources and starts execution)
        worker.start();

        System.out.println("[" + Thread.currentThread().getName() + "] Main thread waiting for worker to finish...");
        worker.join(); // Waits until worker completes

        System.out.println("[" + Thread.currentThread().getName() + "] Main thread resuming. Program finished!");
    }
}
```

**Console Output**:
```text
[main] Main thread starting...
[main] Main thread waiting for worker to finish...
  [BackgroundWorker-1] Worker thread is doing background work...
  [BackgroundWorker-1] Worker finished!
[main] Main thread resuming. Program finished!
```

---

### Example 2: Extending `Thread` Class (Subclassing Approach)
You can also create a thread by creating a class that extends `java.lang.Thread` and overriding `run()`.

```java
class MedicalTelemetryDevice extends Thread {
    private final String deviceId;

    public MedicalTelemetryDevice(String deviceId) {
        super("DeviceThread-" + deviceId);
        this.deviceId = deviceId;
    }

    @Override
    public void run() {
        System.out.println("[" + getName() + "] Polling sensor data for " + deviceId + "...");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            System.err.println("Sensor read interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        System.out.println("[" + getName() + "] Sensor data recorded for " + deviceId);
    }
}

public class SubclassThreadExample {
    public static void main(String[] args) throws InterruptedException {
        MedicalTelemetryDevice dev1 = new MedicalTelemetryDevice("BED-101");
        MedicalTelemetryDevice dev2 = new MedicalTelemetryDevice("BED-102");

        dev1.start();
        dev2.start();

        dev1.join();
        dev2.join();
        System.out.println("Both bedside devices polled successfully!");
    }
}
```

**Console Output**:
```text
[DeviceThread-BED-101] Polling sensor data for BED-101...
[DeviceThread-BED-102] Polling sensor data for BED-102...
[DeviceThread-BED-101] Sensor data recorded for BED-101
[DeviceThread-BED-102] Sensor data recorded for BED-102
Both bedside devices polled successfully!
```

---

### Example 3: Inspecting Thread States Across the Lifecycle
Java provides the `Thread.State` enum. This example tracks a thread from `NEW` to `RUNNABLE`, `TIMED_WAITING`, and `TERMINATED`.

```java
public class ThreadLifecycleExample {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            try {
                // Thread will enter TIMED_WAITING state during sleep
                Thread.sleep(300);
            } catch (InterruptedException ignored) {}
        });

        // 1. NEW: Created but not yet started
        System.out.println("State after creation:  " + worker.getState());

        worker.start();
        // 2. RUNNABLE: Executing in JVM / scheduled by OS
        System.out.println("State right after start: " + worker.getState());

        // Wait briefly for worker to enter sleep
        Thread.sleep(100);
        // 3. TIMED_WAITING: Sleeping
        System.out.println("State during sleep:     " + worker.getState());

        worker.join();
        // 4. TERMINATED: Execution completed
        System.out.println("State after completion: " + worker.getState());
    }
}
```

**Console Output**:
```text
State after creation:  NEW
State right after start: RUNNABLE
State during sleep:     TIMED_WAITING
State after completion: TERMINATED
```

---

### Example 4: Real-World Healthcare Scenario (Concurrent Patient Vitals Aggregator)
When an ICU doctor opens a chart, we poll 3 independent bedside IoT devices concurrently: Heart Rate, Blood Oxygen (SPO2), and Core Temperature.

```java
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VitalsAggregator {
    public static void main(String[] args) throws InterruptedException {
        Map<String, String> patientVitals = new ConcurrentHashMap<>();
        long startTime = System.currentTimeMillis();

        Thread hrThread = new Thread(() -> {
            simulateLatency(120);
            patientVitals.put("HeartRate", "76 bpm");
        }, "HR-Poller");

        Thread spo2Thread = new Thread(() -> {
            simulateLatency(150);
            patientVitals.put("SpO2", "99%");
        }, "SpO2-Poller");

        Thread tempThread = new Thread(() -> {
            simulateLatency(100);
            patientVitals.put("Temperature", "98.6 F");
        }, "Temp-Poller");

        // Fan-Out: Start all 3 queries concurrently
        hrThread.start();
        spo2Thread.start();
        tempThread.start();

        // Fan-In: Wait for all 3 sensors to finish
        hrThread.join();
        spo2Thread.join();
        tempThread.join();

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Patient Vitals Assembled in " + duration + "ms: " + patientVitals);
    }

    private static void simulateLatency(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**Console Output**:
```text
Patient Vitals Assembled in 154ms: {HeartRate=76 bpm, Temperature=98.6 F, SpO2=99%}
```
*(Notice: If run sequentially, it would take 120 + 150 + 100 = 370ms. Concurrently, it takes only 154ms—matching the slowest sensor!)*

---

## 3. Explanation

### JVM Memory Architecture for Threads

```text
               +-------------------------------------------+
               |         JVM SHARED MEMORY HEAP            |
               |  (All threads read/write objects here)   |
               |                                           |
               |  PatientRecord, AccountLedger, Databases  |
               +-------------------------------------------+
                               |     |     |
               +---------------+     |     +---------------+
               |                     |                     |
               v                     v                     v
    +--------------------+  +--------------------+  +--------------------+
    |   THREAD 1 STACK   |  |   THREAD 2 STACK   |  |   THREAD 3 STACK   |
    | - Local Variables  |  | - Local Variables  |  | - Local Variables  |
    | - Method Callframes|  | - Method Callframes|  | - Method Callframes|
    | - Program Counter  |  | - Program Counter  |  | - Program Counter  |
    +--------------------+  +--------------------+  +--------------------+
               |                     |                     |
               v                     v                     v
    +--------------------+  +--------------------+  +--------------------+
    | OS Native Thread 1 |  | OS Native Thread 2 |  | OS Native Thread 3 |
    |   (CPU Core 0)     |  |   (CPU Core 1)     |  |   (CPU Core 2)     |
    +--------------------+  +--------------------+  +--------------------+
```

1. **Shared Heap**: Objects created with `new` (like `String`, `Map`, `Patient`) live on the JVM Heap. Every thread in the application can access them.
2. **Private Thread Stack**: Each thread has its own private stack memory (typically 1 MB). Local variables defined inside methods are strictly private to that thread.
3. **1-to-1 Kernel Thread Mapping**: Modern JVMs (HotSpot on Linux, Windows, macOS) use a 1-to-1 model where every `java.lang.Thread` corresponds directly to an operating system native kernel thread.

---

### The 6 Official Thread States in Java (`Thread.State`)

```text
        +---------------+
        |     NEW       |  (Thread object created: new Thread())
        +---------------+
                |
                | thread.start()
                v
        +---------------+
        |   RUNNABLE    | ◄-------+  (Running or waiting for OS CPU slice)
        +---------------+         |
           |    |     |           |
           |    |     |           | Lock acquired / Timeout expired / Notify
           |    |     |           |
           v    |     v           |
      BLOCKED   |   WAITING /     |
   (Waiting for | TIMED_WAITING --+
      lock)     | (sleep/join/wait)
                |
                | run() finishes or uncaught exception
                v
        +---------------+
        |  TERMINATED   |  (Thread dead, cannot be restarted)
        +---------------+
```

1. **`NEW`**: The thread object exists in memory, but `.start()` has not yet been called.
2. **`RUNNABLE`**: The thread is executing in the JVM or ready to be scheduled by the OS kernel.
3. **`BLOCKED`**: The thread is paused waiting to acquire a monitor lock (`synchronized` block).
4. **`WAITING`**: The thread is waiting indefinitely for another thread to perform a specific action (e.g. calling `wait()` or `join()`).
5. **`TIMED_WAITING`**: The thread is waiting for a specified period (e.g. calling `Thread.sleep(1000)` or `join(500)`).
6. **`TERMINATED`**: The thread has finished executing its `run()` method or died from an uncaught exception.

---

### Comparison: Extending `Thread` vs. Implementing `Runnable`

| Dimension | Extending `Thread` | Implementing `Runnable` (Recommended) |
| :--- | :--- | :--- |
| **Inheritance** | Consumes your single Java class inheritance (`extends`) | Leaves class free to extend any other class (`implements`) |
| **Separation of Concerns**| Blends thread lifecycle with task business logic | Cleanly separates the *task* (`Runnable`) from the *worker* (`Thread`) |
| **Lambda Support** | Cannot use lambda expressions | Supports modern Java lambda syntax: `() -> { ... }` |
| **Thread Pool Compatibility**| Difficult to use with `ExecutorService` | Fully compatible with `ExecutorService.execute(runnable)` |

---

## 4. Why Threading Matters in Java

### 1. True Multi-Core Parallel Processing
Unlike interpreted scripting languages with global locks, Java was architected from day one for multi-threaded server hardware. Java threads run simultaneously across physical CPU cores, allowing enterprise backends to process tens of thousands of business transactions per second.

### 2. Built-in Language Support
Java includes threading natively in its core runtime (`java.lang.Thread`, `java.lang.Runnable`, `synchronized`, `volatile`). You don't need third-party dependencies to build high-performance concurrent software.

### 3. High-Throughput Web Servers
Enterprise frameworks like Spring Boot, Tomcat, and Netty manage internal thread pools that assign each incoming HTTP request to an independent worker thread, isolating user requests from one another.

---

## 5. Advantages & Disadvantages

### Advantages
- **True Parallelism**: Bypasses any interpreter lock; utilizes all available CPU cores.
- **Fast Inter-Thread Communication**: Threads communicate instantly through shared memory references without serialization.
- **Lightweight Compared to Processes**: Starting a thread takes ~1 millisecond and ~1MB RAM, compared to 50MB+ for an entire new operating system process.

### Disadvantages
- **Risk of Race Conditions**: Shared mutable variables can be corrupted if not synchronized properly.
- **Memory Overhead**: Spawning 10,000 native OS threads will consume ~10 GB of stack memory and crash the JVM with `OutOfMemoryError: unable to create native thread`. (Use thread pools or Java 21 Virtual Threads instead!).
- **Debugging Complexity**: Thread interleaving bugs are non-deterministic and difficult to catch in basic unit tests.

---

## 6. Real-World Use Cases

### Domain 1: Healthcare (Multi-Sensor ICU Telemetry Monitor)
- **Problem**: An ICU bedside system must poll Heart Rate, Blood Oxygen (SPO2), and Arterial Pressure every second. If one sensor has a network hiccup and takes 800ms, sequential polling causes vital alarms to trigger late.
- **Solution**: The telemetry engine spawns 3 concurrent threads to read all sensors in parallel, completing in under 150ms.

### Domain 2: eCommerce (Asynchronous Order Checkout Processing)
- **Problem**: When a customer clicks "Buy Now", the checkout service must deduct inventory, charge the credit card, generate a receipt PDF, and send a push notification. Running all 4 steps sequentially makes the user wait 3 seconds.
- **Solution**: The payment is processed synchronously on the main thread, while receipt generation and push notification dispatch are handed off to background worker threads. The user sees their confirmation screen in 300ms.

### Domain 3: Banking (Multi-Branch Overnight Ledger Reconciliation)
- **Problem**: A central bank must reconcile transactions from 50 branch databases every night. Sequential processing takes 8 hours.
- **Solution**: A multi-threaded reconciliation batch job queries and reconciles 10 branch databases concurrently. Total reconciliation time drops to 50 minutes.

---

## 7. Best Practices

### Practice 1: Always Favor `Runnable` Over Extending `Thread`
**When to apply**: Whenever creating a concurrent task.
**Why**: Java does not support multiple class inheritance. Implementing `Runnable` separates your task logic from the thread execution engine and allows effortless integration with thread pools.

```java
// Good Practice
Runnable task = () -> System.out.println("Processing transaction...");
Thread worker = new Thread(task, "OrderWorker-1");
worker.start();
```

---

### Practice 2: Always Assign Descriptive Names to Threads
**When to apply**: In all production Java code.
**Why**: In server logs and thread dumps (`jstack`), names like `Thread-14` are impossible to diagnose. Names like `PaymentProcessor-1` tell you instantly which component is misbehaving.

```java
// Good Practice
Thread worker = new Thread(task, "AuditLogWriter-Thread");
```

---

### Practice 3: Always Restore Interrupt Status on `InterruptedException`
**When to apply**: Inside every `catch (InterruptedException e)` block.
**Why**: Catching `InterruptedException` clears the thread's interrupted flag. If you don't call `Thread.currentThread().interrupt()`, higher-level callers will never know the thread was requested to stop!

```java
// Good Practice
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // Restore interrupted status for upstream callers
    Thread.currentThread().interrupt();
    System.err.println("Thread was interrupted cleanly.");
}
```

---

## 8. Top 3 Mistakes

### Mistake 1: Calling `.run()` Instead of `.start()`
#### What's the Problem?
Invoking `worker.run()` instead of `worker.start()`.
#### Why It Happens
`run()` contains the code you wrote, so developers instinctively call it.
#### Impact
`run()` executes synchronously inside the **current caller thread**! No new OS thread is spawned, and the application freezes.
#### Incorrect Code
```java
Thread t = new Thread(() -> doSlowTask());
t.run(); // BUG: Runs on main thread! Blocks everything!
```
#### Correct Code
```java
Thread t = new Thread(() -> doSlowTask());
t.start(); // Spawns new OS thread and calls run() in the background
```

---

### Mistake 2: Attempting to Restart a Terminated Thread
#### What's the Problem?
Calling `.start()` a second time on a thread that has already completed.
#### Impact
The JVM throws `IllegalThreadStateException` at runtime.
#### Lesson Learned
A Java thread can only be started **once**. Once its state reaches `TERMINATED`, it cannot be restarted. Create a new `Thread` instance instead.

---

### Mistake 3: Swallowing `InterruptedException` Silently
#### What's the Problem?
Empty catch block: `catch (InterruptedException ignored) {}`.
#### Impact
When a thread pool tries to shut down or cancel a task, the thread refuses to terminate, keeping the JVM process alive indefinitely.
#### Correct Approach
Always re-interrupt the current thread:
```java
try {
    Thread.sleep(500);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // Preserves interruption signal
}
```
