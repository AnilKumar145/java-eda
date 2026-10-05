# Unit 2.3: Java Concurrent Collections

---

## 1. What

### Simple Definition
Imagine checking in to a massive 2,000-room luxury hotel:
- **The Old Synchronized Way (`Collections.synchronizedMap`)**: The hotel has a single check-in desk with one clerk. Even if 100 guests are standing in the lobby, only one person can talk to the clerk at a time while everyone else waits in a massive queue outside.
- **The Lock-Striped Modern Way (`ConcurrentHashMap`)**: The hotel replaces the single desk with **16 independent automated check-in kiosks**.
  - Guests whose last names start with A–B go to Kiosk 1.
  - Guests with C–D go to Kiosk 2, and so on.
  - 16 guests check in at the exact same physical second without waiting for each other! Only two guests heading to the *exact same kiosk bucket* have to wait briefly.

In Java, **Concurrent Collections** (`java.util.concurrent`) are high-performance data structures specifically engineered for multi-threaded environments. Instead of putting one giant lock around the entire collection, they use **Lock Striping**, **Bucket-level CAS operations**, and **Copy-On-Write snapshots** to allow hundreds of threads to read and write concurrently.

---

## 2. Examples

Let us explore 4 complete, runnable Java examples demonstrating `ConcurrentHashMap`, `computeIfAbsent()`, `CopyOnWriteArrayList`, and `BlockingQueue`.

### Example 1: `ConcurrentHashMap.computeIfAbsent()` (Atomic Read-and-Write)
In multi-threaded code, `if (!map.containsKey(key)) map.put(key, new List())` has a race condition! `computeIfAbsent()` is guaranteed to execute atomically.

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AtomicComputeDemo {
    public static void main(String[] args) throws InterruptedException {
        // ConcurrentHashMap that groups patient IDs by Ward
        ConcurrentHashMap<String, List<String>> wardAdmissions = new ConcurrentHashMap<>();

        Runnable nurse1 = () -> {
            // Atomically retrieve or initialize the ward list
            wardAdmissions.computeIfAbsent("ICU-Ward-3", k -> new ArrayList<>())
                          .add("PATIENT-ALPHA");
        };

        Runnable nurse2 = () -> {
            wardAdmissions.computeIfAbsent("ICU-Ward-3", k -> new ArrayList<>())
                          .add("PATIENT-BETA");
        };

        Thread t1 = new Thread(nurse1);
        Thread t2 = new Thread(nurse2);

        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Ward Admissions: " + wardAdmissions);
    }
}
```

**Console Output**:
```text
Ward Admissions: {ICU-Ward-3=[PATIENT-ALPHA, PATIENT-BETA]}
```

---

### Example 2: Safe Concurrent Iteration with `CopyOnWriteArrayList`
Iterating over a standard `ArrayList` while another thread adds an item throws `ConcurrentModificationException`. `CopyOnWriteArrayList` creates an immutable snapshot during iteration, allowing iteration to proceed cleanly!

```java
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteDemo {
    public static void main(String[] args) throws InterruptedException {
        // Safe for read-heavy observer / listener lists
        List<String> eventListeners = new CopyOnWriteArrayList<>();
        eventListeners.add("TelemetryAlarmListener");
        eventListeners.add("AuditLogger");

        Thread modifier = new Thread(() -> {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            // Writes make a copy of the underlying array; does not affect active iterator!
            eventListeners.add("BillingWebhookNotifier");
            System.out.println("[Modifier] Added new listener.");
        });

        modifier.start();

        System.out.println("[Main] Iterating through listeners...");
        for (String listener : eventListeners) {
            System.out.println("  --> Dispatching event to: " + listener);
            Thread.sleep(60);
        }

        modifier.join();
        System.out.println("Final Listener Registry: " + eventListeners);
    }
}
```

**Console Output**:
```text
[Main] Iterating through listeners...
  --> Dispatching event to: TelemetryAlarmListener
[Modifier] Added new listener.
  --> Dispatching event to: AuditLogger
Final Listener Registry: [TelemetryAlarmListener, AuditLogger, BillingWebhookNotifier]
```

---

### Example 3: Thread-Safe Bounded Ingestion with `ArrayBlockingQueue`
Producers enqueue data without locks; consumers dequeue with automatic blocking.

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueuePipelineDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> orderQueue = new ArrayBlockingQueue<>(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    orderQueue.put("ORDER-" + i); // Automatically waits if queue has 3 items!
                    System.out.println("[Producer] Queued ORDER-" + i);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    Thread.sleep(60);
                    String item = orderQueue.take(); // Automatically waits if queue is empty!
                    System.out.println("  --> [Consumer] Processed " + item);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        consumer.start();
        producer.start();

        producer.join();
        consumer.join();
    }
}
```

**Console Output**:
```text
[Producer] Queued ORDER-1
[Producer] Queued ORDER-2
[Producer] Queued ORDER-3
  --> [Consumer] Processed ORDER-1
[Producer] Queued ORDER-4
  --> [Consumer] Processed ORDER-2
[Producer] Queued ORDER-5
  --> [Consumer] Processed ORDER-3
  --> [Consumer] Processed ORDER-4
  --> [Consumer] Processed ORDER-5
```

---

## 3. Explanation

### How `ConcurrentHashMap` Achieves Zero Read Locks in Java 8+
In modern Java (Java 8 to Java 21), `ConcurrentHashMap` completely abandons segment locks in favor of **per-bucket synchronized nodes and lock-free CAS reads**:
1. **Reads (`get()`)**: Completely lock-free. Nodes use `volatile` value and next pointers. Readers never acquire a lock.
2. **First Insert in Bucket**: Uses hardware Compare-And-Swap (CAS) to atomically link the head node. Zero locks!
3. **Subsequent Inserts in Same Bucket**: Synchronizes **only on the individual bucket's head node**. All other 63,999 buckets in the table remain 100% unlocked and available for concurrent writes!

---

## 4. Best Practices & Pitfalls

### Rule 1: Never Put `null` Keys or `null` Values in `ConcurrentHashMap`
Unlike standard `HashMap` (which allows a null key and null values), `ConcurrentHashMap` **strictly forbids null keys and null values**. Calling `map.put(null, "val")` throws `NullPointerException` instantly.
*Why*: In a concurrent map, you cannot distinguish whether `map.get(key) == null` means "the key does not exist" or "the key was set to null by another thread".

---

### Rule 2: Only Use `CopyOnWriteArrayList` for Read-Heavy Collections
Every write (`add()`, `set()`, `remove()`) on `CopyOnWriteArrayList` allocates and clones the **entire underlying array**. If you have 50,000 items and call `add()` 1,000 times, it will copy millions of array elements! Use it only when reads outnumber writes 100 to 1 (like event listener registries).
