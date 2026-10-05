# Unit 1.4: Java Thread Communication

---

## 1. What

### Simple Definition
Imagine a popular sit-down restaurant with a waiting lounge:
- **Busy Waiting (The Bad Way)**: Every 5 seconds, a hungry customer walks up to the host stand and asks: *"Is our table ready yet? Is it ready now?"* The host gets annoyed, and the customer burns all their energy asking over and over (this is what a `while (!ready) Thread.sleep(10)` loop does to your CPU).
- **The Restaurant Buzzer Pager (`wait()` and `notify()`)**: Instead, the host hands the customer a vibrating buzzer pager and says: *"Please sit quietly in the waiting lounge. When your table is ready, the pager will buzz."*
  - The customer sits down and relaxes (`customer.wait()`), consuming zero energy.
  - In the dining room, a busser clears a table. The host presses a button on the transmitter (`host.notify()`).
  - The customer's buzzer lights up, they wake up, walk to the host stand, and are seated at their table.

In Java, **Thread Communication** allows threads to talk to each other and coordinate activities. Instead of having worker threads spin in CPU-intensive polling loops waiting for a condition to become true, threads put themselves to sleep using **`wait()`** and are awakened by other threads using **`notify()`** or **`notifyAll()`**.

### The Problems Thread Communication Solves
1. **CPU Spin-Wait Wastage**: Without signaling, threads must repeatedly query shared variables in tight loops (`while (queue.isEmpty())`), pinning CPU cores to 100% usage while doing zero productive work.
2. **Coordinated Asymmetric Workloads**: Producers create data (e.g. incoming network packets), and consumers process data (e.g. writing to disk). Thread communication allows consumers to sleep when the buffer is empty and producers to sleep when the buffer is full.
3. **Graceful Pipeline Flow**: Enables multi-stage pipelines where Stage 2 only runs once Stage 1 has produced output.

### Key Terms You Must Know
- **`wait()`**: Causes the current thread to immediately **release its lock** and sleep in the object's **WaitSet** until another thread calls `notify()`.
- **`notify()`**: Wakes up a single arbitrary thread waiting in the object's WaitSet.
- **`notifyAll()`**: Wakes up **all** threads currently waiting in the object's WaitSet.
- **WaitSet vs. EntrySet**: The EntrySet holds threads waiting to get the lock for the first time; the WaitSet holds threads that voluntarily released the lock via `wait()` and are waiting for a signal.
- **Spurious Wakeup**: A phenomenon where an operating system wakes up a waiting thread even though no thread called `notify()`. This is why `wait()` must **always** be in a `while` loop!

---

## 2. Examples

Let us explore 4 complete, runnable Java examples building from basic signaling to a full Producer–Consumer buffer.

### Example 1: Basic Inter-Thread Signaling (`wait()` and `notify()`)
A worker thread waits for a readiness signal from the main thread.

```java
public class BasicWaitNotifyDemo {
    private static final Object lock = new Object();
    private static boolean isReady = false;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            synchronized (lock) {
                System.out.println("[Worker] Waiting for readiness signal...");
                // ALWAYS check condition inside a while loop to guard against spurious wakeups!
                while (!isReady) {
                    try {
                        lock.wait(); // Releases lock and sleeps!
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("[Worker] Signal received! Processing payload...");
            }
        });

        worker.start();
        Thread.sleep(300); // Give worker time to enter wait state

        synchronized (lock) {
            System.out.println("[Main] Preparing payload and sending notify()...");
            isReady = true;
            lock.notify(); // Wakes up the waiting worker!
        }

        worker.join();
        System.out.println("[Main] Pipeline completed!");
    }
}
```

**Console Output**:
```text
[Worker] Waiting for readiness signal...
[Main] Preparing payload and sending notify()...
[Worker] Signal received! Processing payload...
[Main] Pipeline completed!
```

---

### Example 2: Bounded Producer–Consumer Buffer with `wait()` and `notifyAll()`
A classic fixed-capacity circular queue where producers wait when full, and consumers wait when empty.

```java
import java.util.LinkedList;
import java.util.Queue;

public class BoundedBuffer<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void produce(T item) throws InterruptedException {
        // While buffer is FULL, producer must wait
        while (queue.size() == capacity) {
            System.out.println("  [Buffer FULL] Producer waiting...");
            wait(); // Releases lock
        }

        queue.add(item);
        System.out.println("Produced item: " + item + " (Size: " + queue.size() + ")");
        // Notify consumers that an item is now available
        notifyAll();
    }

    public synchronized T consume() throws InterruptedException {
        // While buffer is EMPTY, consumer must wait
        while (queue.isEmpty()) {
            System.out.println("  [Buffer EMPTY] Consumer waiting...");
            wait(); // Releases lock
        }

        T item = queue.poll();
        System.out.println("Consumed item: " + item + " (Size: " + queue.size() + ")");
        // Notify producers that space is now available
        notifyAll();
        return item;
    }

    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 4; i++) {
                    buffer.produce(i);
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 4; i++) {
                    buffer.consume();
                    Thread.sleep(120);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        consumer.start();
        producer.start();

        producer.join();
        consumer.join();
        System.out.println("All items produced and consumed cleanly!");
    }
}
```

**Console Output**:
```text
  [Buffer EMPTY] Consumer waiting...
Produced item: 1 (Size: 1)
Consumed item: 1 (Size: 0)
Produced item: 2 (Size: 1)
Produced item: 3 (Size: 2)
  [Buffer FULL] Producer waiting...
Consumed item: 2 (Size: 1)
Produced item: 4 (Size: 2)
Consumed item: 3 (Size: 1)
Consumed item: 4 (Size: 0)
All items produced and consumed cleanly!
```

---

### Example 3: Why `notifyAll()` is Safer Than `notify()`
When multiple producers and multiple consumers share a buffer, calling `notify()` can wake up another producer instead of a consumer, resulting in a permanent **Signal Deadlock**!

```java
public class NotifyAllSafetyDemo {
    private static final Object lock = new Object();
    private static int availableTasks = 0;

    public static void main(String[] args) throws InterruptedException {
        Runnable workerTask = () -> {
            synchronized (lock) {
                while (availableTasks == 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ignored) {}
                }
                availableTasks--;
                System.out.println("[" + Thread.currentThread().getName() + "] Claimed a task!");
            }
        };

        // 3 workers waiting for tasks
        Thread w1 = new Thread(workerTask, "Worker-1");
        Thread w2 = new Thread(workerTask, "Worker-2");
        Thread w3 = new Thread(workerTask, "Worker-3");

        w1.start(); w2.start(); w3.start();
        Thread.sleep(100);

        synchronized (lock) {
            System.out.println("[Main] Adding 3 tasks and broadcasting notifyAll()...");
            availableTasks = 3;
            // notifyAll() wakes ALL waiting workers, allowing all 3 tasks to be claimed!
            lock.notifyAll();
        }

        w1.join(); w2.join(); w3.join();
        System.out.println("All 3 workers claimed tasks successfully!");
    }
}
```

**Console Output**:
```text
[Main] Adding 3 tasks and broadcasting notifyAll()...
[Worker-1] Claimed a task!
[Worker-3] Claimed a task!
[Worker-2] Claimed a task!
All 3 workers claimed tasks successfully!
```

---

### Example 4: Modern High-Level Alternative: `ArrayBlockingQueue`
While understanding `wait()` and `notify()` is essential for mastering JVM internals, modern Java provides `java.util.concurrent.BlockingQueue`, which handles all `wait()` and `notify()` calls under the hood with zero boilerplate!

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ModernBlockingQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        // Bounded queue with capacity of 2 items
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);

        Thread producer = new Thread(() -> {
            try {
                queue.put("Patient-101"); // Automatically waits if full!
                queue.put("Patient-102");
                queue.put("Patient-103"); // Automatically pauses here until space freed!
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(100);
                System.out.println("Dequeued: " + queue.take()); // Automatically waits if empty!
                System.out.println("Dequeued: " + queue.take());
                System.out.println("Dequeued: " + queue.take());
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
        System.out.println("BlockingQueue pipeline finished!");
    }
}
```

**Console Output**:
```text
Dequeued: Patient-101
Dequeued: Patient-102
Dequeued: Patient-103
BlockingQueue pipeline finished!
```

---

## 3. Explanation

### JVM WaitSet and Monitor Lifecycle

```text
               MONITOR SYNCHRONIZATION & SIGNALING
               
      +-------------------------------------------------+
      |                 ENTRY SET                       |
      |   (Threads waiting to acquire the lock: BLOCKED)|
      +-------------------------------------------------+
                               │
                               │ Lock becomes free
                               ▼
      +-------------------------------------------------+
      |             ACTIVE OWNER THREAD                 |
      |             (Currently in synchronized block)   |
      +-------------------------------------------------+
             │                                   ▲
             │ Calls: wait()                     │ Woken by notify()
             │ (Releases lock!)                  │ Re-enters EntrySet
             ▼                                   │
      +-------------------------------------------------+
      |                  WAIT SET                       |
      |     (Threads asleep waiting for signal: WAITING)|
      +-------------------------------------------------+
```

1. **Releasing the Lock on `wait()`**: Unlike `Thread.sleep()` (which keeps holding its lock while sleeping!), calling `object.wait()` **releases the monitor lock immediately**. This allows other threads to enter the synchronized block and make state changes.
2. **Re-acquiring the Lock on `notify()`**: When `notify()` is called, the waiting thread does not instantly jump back into execution. It is moved from the **WaitSet** to the **EntrySet**, where it must wait for the current owner to exit the synchronized block before it can re-acquire the lock and continue.

---

### Why the `while` Loop is Mandatory (Spurious Wakeups)
Consider this broken code:
```java
// DANGEROUS MISTAKE: Using if instead of while!
if (queue.isEmpty()) {
    wait();
}
queue.remove(); // CRASH! NoSuchElementException!
```
Why does this crash?
1. **Spurious Wakeup**: OS schedulers can wake up a sleeping thread without any notify call.
2. **Race Between Consumers**: Thread A calls `notify()`. Thread B and Thread C were both waiting. Thread B re-acquires the lock first and empties the queue. Thread C now wakes up, skips the `if` check, and tries to remove from an empty queue!
With a `while` loop, Thread C wakes up and immediately re-evaluates `queue.isEmpty()`. Seeing that the queue is empty, it safely goes back to sleep.

---

## 4. Why Thread Communication Matters

### 1. Zero CPU Wastage
A thread in `wait()` state consumes 0.00% CPU cycles. It is completely suspended by the OS kernel, allowing your server's processors to focus on active workloads.

### 2. Built-in Backpressure and Rate Limiting
Bounded queues coordinate producers and consumers naturally. When consumers slow down, producers automatically wait on `buffer.wait()`, preventing your server from running out of RAM.

---

## 5. Advantages & Disadvantages

### Advantages
- Built directly into every Java `Object` (`java.lang.Object`).
- Clean cooperative sleep-and-wake mechanics.
- Releases intrinsic locks during waiting periods.

### Disadvantages
- Easy to make mistakes (forgetting `while`, forgetting `notifyAll`).
- Low-level: Modern concurrent code should usually prefer `BlockingQueue` or `CompletableFuture`.

---

## 6. Real-World Use Cases

### Domain 1: Healthcare (ER Patient Triage Dispatcher)
- **Problem**: Ambulances arrive at random intervals. ER doctors should sleep or rest until a patient arrives. When an ambulance checks in, the on-duty doctor must be notified instantly.
- **Solution**: A priority triage case buffer where doctor threads call `triageQueue.take()`. When a case is admitted, `notifyAll()` wakes the doctor pool.

### Domain 2: eCommerce (Asynchronous Order Invoice Generator)
- **Problem**: Checkout web servers produce 500 order events per second. A separate PDF invoice service must render PDFs in the background.
- **Solution**: A bounded queue using `wait()` and `notifyAll()` smooths out order bursts without memory exhaustion.

### Domain 3: Banking (Interbank Settlement File Processor)
- **Problem**: A batch reader downloads settlement XML chunks from Federal Reserve servers, while validation workers parse transactions.
- **Solution**: Reader threads signal parsing workers when chunks are placed in the staging buffer.

---

## 7. Best Practices

### Practice 1: Always Call `wait()` Inside a `while` Loop
**Rule**: Never use `if (condition) wait();`. Always use `while (condition) wait();`.

---

### Practice 2: Prefer `notifyAll()` Over `notify()`
**Rule**: Unless you have mathematically proven that exactly one thread can make progress and all waiting threads are identical, always use `notifyAll()`.

---

### Practice 3: Always Hold the Monitor When Calling `wait()` or `notify()`
**Rule**: Calling `obj.wait()` when not inside `synchronized (obj)` will instantly throw `IllegalMonitorStateException`.

---

## 8. Top 3 Mistakes

### Mistake 1: Calling `wait()` Outside a Synchronized Block
#### What's the Problem?
```java
Object lock = new Object();
lock.wait(); // Throws IllegalMonitorStateException!
```
#### Lesson Learned
You must own the monitor lock before calling `wait()`, `notify()`, or `notifyAll()`.

---

### Mistake 2: Calling `Thread.sleep()` Instead of `wait()` When Waiting for State
#### What's the Problem?
```java
synchronized (lock) {
    while (!ready) {
        Thread.sleep(100); // DOES NOT RELEASE LOCK! Nobody can set ready = true!
    }
}
```
#### Impact
Complete deadlock! Because `sleep()` holds the lock, other threads cannot enter the synchronized block to change `ready = true`!
#### Lesson Learned
Use `wait()` to sleep AND release the lock.

---

### Mistake 3: Missed Signals
#### What's the Problem?
Calling `notify()` before the receiver thread has called `wait()`.
#### Impact
In Java, signals sent via `notify()` are not buffered. If no thread is waiting when `notify()` is called, the signal is lost.
#### Lesson Learned
Always use a shared state variable (e.g. `boolean hasMessage = true`) checked inside the `while` loop so the waiting thread knows the signal already arrived.
