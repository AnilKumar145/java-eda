# Unit 2.1: Java Concurrency Fundamentals & Thread-Safe Design

---

## 1. What

### Simple Definition
Imagine two different ways of sharing information in an office:
- **The Shared Whiteboard (Mutable State)**: Employees write project statuses on a physical whiteboard in the hallway. If Alice is erasing and updating the revenue numbers at the exact second Bob is walking by reading them, Bob reads half-erased numbers and reports wrong data to the CEO. You need to hire a security guard to stand by the whiteboard so only one person touches it at a time (Locks).
- **The Printed Book (Immutability)**: Instead of a whiteboard, every morning the office prints a bound paperback book containing the daily financial report. Once printed, the pages can never be changed. 500 employees can read their copies at the exact same physical second without guards, without queues, and with zero chance of data corruption.

In Java, **Concurrency Fundamentals** is the study of how multiple threads interact within the JVM's shared-memory model. The ultimate secret to thread safety in Java is **Immutability**: an object that cannot be modified after construction is mathematically impossible to corrupt with a race condition!

### The Problems Concurrency Fundamentals Solve
1. **Concurrency vs. Parallelism Confusion**: Concurrency is about *handling* lots of things at once (structure); Parallelism is about *doing* lots of things at once (physical simultaneous CPU execution). Knowing the difference helps you architect systems that scale.
2. **Eliminating Synchronization Overhead**: Synchronizing on locks burns CPU cycles and causes lock contention. Designing immutable domain models (`records`, `final` fields) gives you 100% thread safety at maximum memory read speeds with zero locks.
3. **Escaping Partially Initialized Objects**: If a constructor starts a thread before finishing initialization, other threads can see the object in an invalid, half-created state. Understanding safe publication prevents subtle startup bugs.

### Key Terms You Must Know
- **Thread Safety**: A class is thread-safe if it behaves correctly when accessed by multiple threads simultaneously, regardless of OS scheduling, without requiring extra synchronization by the caller.
- **Immutability**: An object whose state cannot change after it is constructed.
- **Java `record`**: A modern Java feature (Java 16+) that creates immutable data-carrier classes with `final` fields, automatic getters, `equals()`, and `hashCode()`.
- **Safe Publication**: Making an object reference available to other threads such that all its fields are guaranteed to be fully initialized and visible.

---

## 2. Examples

Let us explore 4 complete, runnable Java examples demonstrating concurrency vs parallelism, immutability, and safe publication.

### Example 1: Concurrency vs. Parallelism in Java
Demonstrating that threads share CPU time on 1 core (concurrency) or execute on multiple physical cores simultaneously (parallelism).

```java
public class ConcurrencyVsParallelismDemo {
    public static void main(String[] args) throws InterruptedException {
        int availableCores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available CPU Hardware Cores in JVM: " + availableCores);

        // Creating 2 computational tasks
        Runnable cpuTask = () -> {
            long sum = 0;
            for (int i = 0; i < 50_000_000; i++) sum += i;
            System.out.println("[" + Thread.currentThread().getName() + "] Math sum complete: " + sum);
        };

        long t0 = System.currentTimeMillis();
        Thread t1 = new Thread(cpuTask, "CoreWorker-1");
        Thread t2 = new Thread(cpuTask, "CoreWorker-2");

        // On a multi-core machine, t1 and t2 run in TRUE PARALLEL across separate CPU cores!
        t1.start();
        t2.start();

        t1.join();
        t2.join();
        long elapsed = System.currentTimeMillis() - t0;
        System.out.println("Parallel computation completed in " + elapsed + "ms");
    }
}
```

**Console Output**:
```text
Available CPU Hardware Cores in JVM: 12
[CoreWorker-2] Math sum complete: 1249999975000000
[CoreWorker-1] Math sum complete: 1249999975000000
Parallel computation completed in 32ms
```

---

### Example 2: The Power of Immutability with Java `record`
Modern Java `record` classes are automatically `final`, with all fields `final` and private. They are 100% thread-safe by default.

```java
import java.util.List;

// Java Record: Completely immutable, thread-safe domain model
public record PatientTelemetry(
    String patientId,
    int heartRate,
    double bloodOxygen,
    List<String> activeAllergies
) {
    // Compact constructor ensuring defensive copying of mutable collections!
    public PatientTelemetry {
        // List.copyOf() returns an unmodifiable immutable copy of the list
        activeAllergies = List.copyOf(activeAllergies);
    }

    public static void main(String[] args) throws InterruptedException {
        PatientTelemetry record = new PatientTelemetry(
            "PAT-8812", 74, 98.2, List.of("Penicillin", "Sulfa")
        );

        // 10 concurrent threads reading the record simultaneously WITHOUT any locks!
        for (int i = 1; i <= 3; i++) {
            new Thread(() -> {
                System.out.println("[" + Thread.currentThread().getName() + "] Reading: " +
                    record.patientId() + " | HR=" + record.heartRate() +
                    " | Allergies=" + record.activeAllergies());
            }, "Reader-" + i).start();
        }
    }
}
```

**Console Output**:
```text
[Reader-1] Reading: PAT-8812 | HR=74 | Allergies=[Penicillin, Sulfa]
[Reader-2] Reading: PAT-8812 | HR=74 | Allergies=[Penicillin, Sulfa]
[Reader-3] Reading: PAT-8812 | HR=74 | Allergies=[Penicillin, Sulfa]
```

---

### Example 3: Thread-Safe State Transition with Immutable Snapshots
How do you update state if an object is immutable? You create a **new immutable snapshot** and store it in an atomic or volatile reference!

```java
import java.util.concurrent.atomic.AtomicReference;

public class ImmutableStateContainer {
    public record SystemStatus(long activeConnections, double cpuLoad, String health) {}

    // AtomicReference holds reference to the current immutable status
    private final AtomicReference<SystemStatus> currentStatus =
        new AtomicReference<>(new SystemStatus(0, 0.0, "INITIALIZING"));

    public void updateStatus(long connections, double cpuLoad) {
        // Atomic compare-and-swap update to a new immutable record
        currentStatus.updateAndGet(old ->
            new SystemStatus(connections, cpuLoad, cpuLoad > 90.0 ? "CRITICAL" : "HEALTHY")
        );
    }

    public SystemStatus getStatus() {
        return currentStatus.get(); // Never requires locks!
    }

    public static void main(String[] args) {
        ImmutableStateContainer container = new ImmutableStateContainer();
        container.updateStatus(1500, 42.5);
        System.out.println("System Status: " + container.getStatus());

        container.updateStatus(4500, 95.2);
        System.out.println("System Status: " + container.getStatus());
    }
}
```

**Console Output**:
```text
System Status: SystemStatus[activeConnections=1500, cpuLoad=42.5, health=HEALTHY]
System Status: SystemStatus[activeConnections=4500, cpuLoad=95.2, health=CRITICAL]
```

---

### Example 4: Unsafe Publication vs. Safe Publication
Demonstrating how a leaking `this` reference in a constructor exposes half-initialized variables to other threads.

```java
public class SafePublicationDemo {
    public static class SafeObject {
        private final int x;
        private final int y;

        public SafeObject(int x, int y) {
            this.x = x;
            this.y = y;
            // The JMM guarantees that final fields are fully visible
            // once the constructor completes!
        }

        public int getSum() {
            return x + y;
        }
    }

    public static void main(String[] args) {
        SafeObject obj = new SafeObject(10, 20);
        System.out.println("Safe published sum: " + obj.getSum());
    }
}
```

**Console Output**:
```text
Safe published sum: 30
```

---

## 3. Explanation

### JVM Happens-Before & Final Field Freeze Guarantees
In Java, the **Java Memory Model (JMM)** defines the rules for when a write by one thread is guaranteed to be visible to another thread.
One of the most powerful guarantees in the JMM is the **Final Field Freeze**:
- When an object is constructed with `final` fields, the JVM inserts a memory fence at the end of the constructor.
- When any other thread receives a reference to that object, **all `final` fields are guaranteed to be fully visible and initialized**, even if the object was published without synchronization!

```text
               FINAL FIELD MEMORY FREEZE
               
Constructor executing:
1. this.patientId = "PAT-101"
2. this.heartRate = 78
---------------------------------------------
[JMM MEMORY FENCE: End of Constructor]
All final writes are frozen and flushed to RAM!
---------------------------------------------
Other thread reads reference:
Guaranteed to see patientId="PAT-101" and heartRate=78!
Can NEVER see partial or null values!
```

---

## 4. Why Immutability is the Gold Standard

### 1. Zero Synchronization Costs
Locks consume CPU overhead for acquiring monitors and managing entry queues. Immutable objects can be read by 1,000 threads simultaneously with zero locks, delivering maximum hardware memory bandwidth.

### 2. Elimination of Defensive Copying on Reads
When returning mutable objects (like a `Date` or `ArrayList`), you must clone them to prevent callers from corrupting your internal state. Immutable objects can be shared freely without copying.

---

## 5. Best Practices

### Practice 1: Use Java `record` for Data Carriers
**Rule**: For DTOs, messages, and telemetry payloads, prefer `record` over traditional mutable classes with getters and setters.

---

### Practice 2: Defensively Copy Collections Inside Records
**Rule**: If an immutable record contains a `List` or `Map`, always wrap it in `List.copyOf()` or `Collections.unmodifiableList()`. Otherwise, a caller holding the original list can modify it from the outside!

```java
// Good Practice
public record Order(String id, List<String> items) {
    public Order {
        items = List.copyOf(items); // Immutable snapshot!
    }
}
```

---

### Practice 3: Never Start a Thread Inside a Constructor
**Rule**: Starting a thread in a constructor leaks `this` before initialization finishes. Always start threads from an explicit `start()` or `init()` method.
