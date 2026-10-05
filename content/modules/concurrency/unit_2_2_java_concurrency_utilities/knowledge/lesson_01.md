# Unit 2.2: Java Concurrency Utilities

---

## 1. What

### Simple Definition
In Java 1.0, the only synchronization tool was the `synchronized` keyword. While simple, `synchronized` had strict limitations: you couldn't back out if a lock was unavailable, you couldn't enforce timeouts, and you couldn't let multiple readers read at the same time.

In Java 5, Doug Lea designed the **`java.util.concurrent` (JUC)** package, providing specialized synchronization tools:
1. **`ReentrantLock` (The Smart Keycard Lock)**: Works like `synchronized`, but lets you check: *"Can I get the lock right now? If not, I'll wait 2 seconds, and if it's still busy, I'll walk away"* (`tryLock(2, TimeUnit.SECONDS)`).
2. **`ReentrantReadWriteLock` (The Library Reading Room)**: In a library, 50 people can read the reference encyclopedia simultaneously without any conflict (Read Lock). But when the librarian needs to update an errata page, all readers pause while the librarian writes exclusively (Write Lock).
3. **`Semaphore` (The Parking Garage Counter)**: A parking garage has 5 parking spaces. When a car enters, it takes a permit (`acquire()`). When 5 cars are parked, the gate stays down until a car leaves and returns a permit (`release()`).
4. **`CountDownLatch` (The Space Shuttle Countdown)**: A rocket cannot launch until 3 pre-flight checks are finished: Fuel Loaded, Oxygen Tank Pressurized, Navigational Software Verified. The countdown starts at 3 (`new CountDownLatch(3)`). As each team finishes, they call `countDown()`. When the count reaches 0, the main engine ignites!

---

## 2. Examples

Let us explore 4 complete, runnable Java examples demonstrating `ReentrantLock`, `ReadWriteLock`, `Semaphore`, and `CountDownLatch`.

### Example 1: Non-Blocking Deadlock Prevention with `ReentrantLock.tryLock()`
Using `tryLock(timeout)` completely eliminates deadlocks because threads back out if a lock is unavailable.

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TryLockDemo {
    private static final ReentrantLock lockA = new ReentrantLock();
    private static final ReentrantLock lockB = new ReentrantLock();

    public static void safeTransfer(String workerName, ReentrantLock first, ReentrantLock second) {
        try {
            // Try to acquire first lock within 100ms
            if (first.tryLock(100, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println("[" + workerName + "] Acquired first lock. Attempting second lock...");
                    // Try to acquire second lock within 100ms
                    if (second.tryLock(100, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println("  --> [" + workerName + "] SUCCESS: Acquired BOTH locks! Transferring...");
                            Thread.sleep(50);
                        } finally {
                            second.unlock();
                        }
                    } else {
                        System.out.println("  --> [" + workerName + "] Second lock busy. Backing out to prevent deadlock!");
                    }
                } finally {
                    first.unlock(); // Always release first lock
                }
            } else {
                System.out.println("[" + workerName + "] First lock busy. Skipping.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Worker 1 acquires lockA then lockB
        Thread t1 = new Thread(() -> safeTransfer("Worker-1", lockA, lockB));
        // Worker 2 simultaneously acquires lockB then lockA (opposite order!)
        Thread t2 = new Thread(() -> safeTransfer("Worker-2", lockB, lockA));

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        System.out.println("[Main] Zero deadlocks occurred! Both threads finished safely.");
    }
}
```

**Console Output**:
```text
[Worker-1] Acquired first lock. Attempting second lock...
[Worker-2] Acquired first lock. Attempting second lock...
  --> [Worker-1] Second lock busy. Backing out to prevent deadlock!
  --> [Worker-2] SUCCESS: Acquired BOTH locks! Transferring...
[Main] Zero deadlocks occurred! Both threads finished safely.
```

---

### Example 2: High-Concurrency Read/Write Cache with `ReentrantReadWriteLock`
Multiple reader threads read simultaneously; writes are strictly exclusive.

```java
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteCache<K, V> {
    private final Map<K, V> map = new HashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public V get(K key) {
        rwLock.readLock().lock(); // Multiple threads can hold readLock concurrently!
        try {
            return map.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void put(K key, V value) {
        rwLock.writeLock().lock(); // Exclusive: all readers & writers wait!
        try {
            map.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReadWriteCache<String, String> cache = new ReadWriteCache<>();
        cache.put("BED-101", "STABLE");

        // 3 concurrent readers read at the exact same time
        for (int i = 1; i <= 3; i++) {
            new Thread(() -> {
                System.out.println("[" + Thread.currentThread().getName() + "] Status: " + cache.get("BED-101"));
            }, "Reader-" + i).start();
        }
    }
}
```

**Console Output**:
```text
[Reader-1] Status: STABLE
[Reader-2] Status: STABLE
[Reader-3] Status: STABLE
```

---

### Example 3: Rate Limiting & Resource Throttling with `Semaphore`
A hospital server allows at most 2 concurrent database connections.

```java
import java.util.concurrent.Semaphore;

public class SemaphoreThrottlingDemo {
    // Semaphore with 2 permits
    private static final Semaphore dbPermits = new Semaphore(2);

    public static void accessDatabase(int doctorId) {
        try {
            System.out.println("[Doctor " + doctorId + "] Requesting DB connection...");
            dbPermits.acquire(); // Blocks if 2 doctors are already connected!
            try {
                System.out.println("  --> [Doctor " + doctorId + "] CONNECTED! (Available permits: " +
                                   dbPermits.availablePermits() + ")");
                Thread.sleep(150); // Query database
            } finally {
                System.out.println("  <-- [Doctor " + doctorId + "] Releasing connection.");
                dbPermits.release(); // Return permit
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 4; i++) {
            final int id = i;
            new Thread(() -> accessDatabase(id)).start();
        }
    }
}
```

**Console Output**:
```text
[Doctor 1] Requesting DB connection...
[Doctor 2] Requesting DB connection...
[Doctor 3] Requesting DB connection...
[Doctor 4] Requesting DB connection...
  --> [Doctor 1] CONNECTED! (Available permits: 1)
  --> [Doctor 2] CONNECTED! (Available permits: 0)
  <-- [Doctor 1] Releasing connection.
  <-- [Doctor 2] Releasing connection.
  --> [Doctor 3] CONNECTED! (Available permits: 1)
  --> [Doctor 4] CONNECTED! (Available permits: 0)
  <-- [Doctor 3] Releasing connection.
  <-- [Doctor 4] Releasing connection.
```

---

### Example 4: Coordinated Startup Countdown with `CountDownLatch`
Main server waits until 3 subsystem health checks report ready.

```java
import java.util.concurrent.CountDownLatch;

public class SubsystemCountdownDemo {
    public static void main(String[] args) throws InterruptedException {
        // Countdown from 3
        CountDownLatch latch = new CountDownLatch(3);

        Runnable checkTask = () -> {
            String name = Thread.currentThread().getName();
            System.out.println("[" + name + "] Performing self-test...");
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            System.out.println("[" + name + "] Ready!");
            latch.countDown(); // Decrements counter by 1
        };

        new Thread(checkTask, "PACS-Imaging-Service").start();
        new Thread(checkTask, "EHR-Database-Connection").start();
        new Thread(checkTask, "IoT-Telemetry-Bus").start();

        System.out.println("[Main Server] Waiting for all 3 subsystems to initialize...");
        latch.await(); // Blocks until counter reaches 0!

        System.out.println("[Main Server] ALL SUBSYSTEMS OPERATIONAL! Accepting patient admissions.");
    }
}
```

**Console Output**:
```text
[Main Server] Waiting for all 3 subsystems to initialize...
[PACS-Imaging-Service] Performing self-test...
[EHR-Database-Connection] Performing self-test...
[IoT-Telemetry-Bus] Performing self-test...
[PACS-Imaging-Service] Ready!
[IoT-Telemetry-Bus] Ready!
[EHR-Database-Connection] Ready!
[Main Server] ALL SUBSYSTEMS OPERATIONAL! Accepting patient admissions.
```

---

## 3. Explanation

### AbstractQueuedSynchronizer (AQS)
Under the hood, almost all classes in `java.util.concurrent` (`ReentrantLock`, `Semaphore`, `CountDownLatch`) are built upon a single foundational framework written by Doug Lea: **AbstractQueuedSynchronizer (AQS)**.

AQS maintains:
1. An `int state` variable updated using hardware atomic Compare-And-Swap (CAS).
   - In `ReentrantLock`: `state` is the recursion hold count (0 = unlocked, 1+ = locked).
   - In `Semaphore`: `state` is the number of remaining available permits.
   - In `CountDownLatch`: `state` is the remaining countdown count.
2. A double-linked FIFO queue of waiting threads. When a thread cannot acquire the state, AQS parks the thread using `LockSupport.park()` and enqueues a node. When the state becomes available, AQS unparks the head thread.

---

## 4. Best Practices

### Practice 1: ALWAYS Unlock in a `finally` Block
Unlike `synchronized` (which unlocks automatically on method exit or exception), an explicit `Lock` **must** be unlocked manually.

```java
// Good Practice
lock.lock();
try {
    // Critical section
} finally {
    lock.unlock(); // GUARANTEED to execute even if an exception is thrown!
}
```

---

### Practice 2: Never Call `unlock()` in `finally` Without Confirming Lock Was Acquired
```java
// Good Practice
boolean acquired = lock.tryLock();
if (acquired) {
    try {
        // Work
    } finally {
        lock.unlock();
    }
}
```
