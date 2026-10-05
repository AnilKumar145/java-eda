# Unit 2.5: Advanced Task Execution and Asynchronous Programming

---

## 1. Conceptual Foundation: The Fast-Food Buzzer vs. Waiting at the Counter

Imagine ordering a meal at a busy gourmet burger bistro:

- **The `Runnable` Approach (Fire-and-Forget)**: You shout your order to the kitchen and walk away. The kitchen cooks your burger, but you have no receipt, no callback, and no way to receive the burger back. (Runnable cannot return values or throw checked exceptions).
- **The `Future.get()` Approach (The Waiting Stare)**: You order your burger. The cashier gives you a paper receipt (`Future`). You immediately stand frozen in front of the cashier counter staring into the kitchen, waiting for your burger (`future.get()`). While you are frozen, nobody else behind you can order, and you cannot do anything else like finding a table or ordering drinks.
- **The `CompletableFuture` Approach (The Smart Electronic Pager)**: You order your burger. The cashier hands you an electronic buzzer pager. You walk away, find a comfortable table, read the news, and order your drink at the juice bar. When the burger is ready, the pager buzzes automatically. It can even trigger next steps automatically: *"When the burger is ready, toast the bun (`thenApply`), then pour the drink (`thenCombine`), and if the grill caught fire, serve a complimentary sandwich instead (`exceptionally`)."*

`CompletableFuture` transforms Java from blocking, thread-starved concurrency into **non-blocking, event-driven reactive pipelines**.

---

## 2. The Evolution: `Runnable` $\to$ `Callable` $\to$ `Future` $\to$ `CompletableFuture`

```
  Java 1.0                Java 5 (java.util.concurrent)               Java 8+
+-----------+          +-------------------------------+       +---------------------+
| Runnable  |  ----->  | Callable<V>  +   Future<V>    | ----> |  CompletableFuture  |
| void run()|          | V call()        V get()       |       | Non-blocking chains |
+-----------+          +-------------------------------+       +---------------------+
No return value        Returns typed value, throws     Asynchronous pipelines,
Cannot throw checked   exceptions. BUT .get() blocks   callback chaining,
exceptions.            the calling thread!             rich error handling.
```

### The Flaw of Classic `Future<V>`:
```java
// With classic Future:
Future<String> userFuture = pool.submit(() -> fetchUser(id));
Future<List<Order>> ordersFuture = pool.submit(() -> fetchOrders(id));

// To combine them, you MUST block!
String user = userFuture.get(); // BLOCKS thread!
List<Order> orders = ordersFuture.get(); // BLOCKS thread!
renderDashboard(user, orders);
```
If you have 500 concurrent HTTP requests, 500 threads sit completely idle blocked on `.get()`. This wastes gigabytes of memory and exhausts thread pools.

---

## 3. Anatomy of `CompletableFuture`

`CompletableFuture<T>` implements both `Future<T>` and `CompletionStage<T>`. A completion stage represents a node in a directed acyclic computation graph.

```
[ supplyAsync(Fetch User) ]
            |
            v  .thenApply(user -> parseToken(user))
[ Transform User to Token ] 
            |
            +-------------------------+
            |                         |
            v                         v
[ supplyAsync(Credit Score) ]   [ supplyAsync(Address Verification) ]
            \                         /
             \                       /  .thenCombine(...)
              v                     v
            [ Composite Verification Report ]
```

### Key Functional Operators:

| Method | Signature / Purpose | Analogy |
| :--- | :--- | :--- |
| `supplyAsync(Supplier<U>)` | Starts async task returning a value on a worker pool | Place initial kitchen order |
| `thenApply(Function<T, U>)` | Transforms value when stage completes (sync/in-thread) | Chop cooked vegetables |
| `thenApplyAsync(Function<T, U>)` | Transforms value asynchronously on pool thread | Send vegetables to second chef |
| `thenAccept(Consumer<T>)` | Consumes value without returning a result | Serve the meal to guest |
| `thenCompose(Function<T, CompletionStage<U>>)` | Flattens nested futures (like `flatMap`) | Order side dish that returns another pager |
| `thenCombine(CompletionStage<U>, BiFunction)` | Merges two independent futures when **both** complete | Pair burger with milkshake |
| `CompletableFuture.allOf(...)` | Waits for an arbitrary array of futures to finish | Wait for all table guests to finish |

---

## 4. Error Handling and Resilience

In an asynchronous pipeline, exceptions occur in background threads. `CompletableFuture` provides declarative operators to catch and recover from failures without try-catch blocks:

### 1. `exceptionally(Function<Throwable, T>)`
Recovers with a fallback default value if an exception occurred earlier in the pipeline:
```java
CompletableFuture<String> priceFuture = CompletableFuture.supplyAsync(() -> fetchLiveStockPrice("AAPL"))
    .exceptionally(ex -> {
        System.err.println("Live price API down: " + ex.getMessage());
        return "150.00 (Cached Fallback)";
    });
```

### 2. `handle(BiFunction<T, Throwable, R>)`
Always executes, receiving both result and error (one of which is null), allowing comprehensive inspection:
```java
CompletableFuture<String> statusFuture = CompletableFuture.supplyAsync(() -> callMicroservice())
    .handle((result, ex) -> {
        if (ex != null) {
            return "DEGRADED: " + ex.getMessage();
        }
        return "HEALTHY: " + result;
    });
```

### 3. `orTimeout(long timeout, TimeUnit unit)` (Java 9+)
Fails the future with `TimeoutException` if it doesn't complete within the duration:
```java
userFuture.orTimeout(2, TimeUnit.SECONDS);
```

---

## 5. Comprehensive Runnable Code Examples

### Example 1: `Callable` vs `Runnable` with `ExecutorService`

```java
package unit_2_5_advanced_task_execution;

import java.util.concurrent.*;

public class CallableDemo {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        // 1. Runnable: No return value, cannot throw checked exception
        Runnable printTask = () -> System.out.println("[Runnable] Running task with no result.");
        pool.submit(printTask).get(); // returns null

        // 2. Callable: Returns typed value, can throw checked exceptions
        Callable<Integer> calculationTask = () -> {
            System.out.println("[Callable] Computing complex factorial...");
            int result = 1;
            for (int i = 1; i <= 5; i++) result *= i;
            return result;
        };

        Future<Integer> future = pool.submit(calculationTask);
        System.out.println("[Main] Doing other work while calculation runs...");
        Integer result = future.get(1, TimeUnit.SECONDS); // Blocking get with timeout
        System.out.println("[Main] Calculation result: " + result);

        pool.shutdown();
    }
}
```
**Expected Output:**
```
[Runnable] Running task with no result.
[Callable] Computing complex factorial...
[Main] Doing other work while calculation runs...
[Main] Calculation result: 120
```

---

### Example 2: Non-Blocking Pipeline with `supplyAsync` and `thenApply`

```java
package unit_2_5_advanced_task_execution;

import java.util.concurrent.CompletableFuture;

public class AsyncPipelineDemo {
    public static void main(String[] args) {
        System.out.println("[Main] Initiating order pipeline on thread: " + Thread.currentThread().getName());

        CompletableFuture<String> pipeline = CompletableFuture.supplyAsync(() -> {
            System.out.println("[Stage 1] Querying database for user on: " + Thread.currentThread().getName());
            return "user_1092";
        }).thenApply(userId -> {
            System.out.println("[Stage 2] Formatting user profile on: " + Thread.currentThread().getName());
            return "Profile[" + userId.toUpperCase() + "]";
        }).thenApply(profile -> {
            System.out.println("[Stage 3] Appending authentication token on: " + Thread.currentThread().getName());
            return profile + "_AUTH_OK";
        });

        // Join to print in main demo
        String finalResult = pipeline.join();
        System.out.println("[Main] Final Pipeline Output: " + finalResult);
    }
}
```
**Expected Output:**
```
[Main] Initiating order pipeline on thread: main
[Stage 1] Querying database for user on: ForkJoinPool.commonPool-worker-1
[Stage 2] Formatting user profile on: ForkJoinPool.commonPool-worker-1
[Stage 3] Appending authentication token on: ForkJoinPool.commonPool-worker-1
[Main] Final Pipeline Output: Profile[USER_1092]_AUTH_OK
```

---

### Example 3: Merging Two Independent Services with `thenCombine`

```java
package unit_2_5_advanced_task_execution;

import java.util.concurrent.CompletableFuture;

public class ThenCombineDemo {
    public static void main(String[] args) {
        System.out.println("[Main] Starting parallel service calls...");

        CompletableFuture<Double> flightPriceFuture = CompletableFuture.supplyAsync(() -> {
            sleep(100);
            return 320.50; // Flight price
        });

        CompletableFuture<Double> hotelPriceFuture = CompletableFuture.supplyAsync(() -> {
            sleep(120);
            return 180.25; // Hotel price
        });

        // Combine both independent results concurrently
        CompletableFuture<String> packageDealFuture = flightPriceFuture.thenCombine(
            hotelPriceFuture,
            (flight, hotel) -> {
                double total = flight + hotel;
                double discounted = total * 0.90; // 10% bundle discount
                return String.format("Flight: $%.2f + Hotel: $%.2f -> Bundle Total (10%% off): $%.2f", 
                        flight, hotel, discounted);
            }
        );

        System.out.println("[Main] Deal Package: " + packageDealFuture.join());
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
```
**Expected Output:**
```
[Main] Starting parallel service calls...
[Main] Deal Package: Flight: $320.50 + Hotel: $180.25 -> Bundle Total (10% off): $450.68
```

---

### Example 4: Aggregating Multiple Futures with `allOf` and Resilience Fallback

```java
package unit_2_5_advanced_task_execution;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AllOfAggregationDemo {
    public static void main(String[] args) {
        CompletableFuture<String> serviceA = CompletableFuture.supplyAsync(() -> "Service A: UP");
        CompletableFuture<String> serviceB = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("Service B database unreachable");
            return "Service B: UP";
        }).exceptionally(ex -> "Service B: DEGRADED (" + ex.getMessage() + ")");
        CompletableFuture<String> serviceC = CompletableFuture.supplyAsync(() -> "Service C: UP");

        // Wait for all three services to complete
        CompletableFuture<Void> allServices = CompletableFuture.allOf(serviceA, serviceB, serviceC);

        CompletableFuture<List<String>> combinedReport = allServices.thenApply(v -> 
            List.of(serviceA.join(), serviceB.join(), serviceC.join())
        );

        List<String> statuses = combinedReport.join();
        System.out.println("System Health Report:");
        statuses.forEach(status -> System.out.println(" - " + status));
    }
}
```
**Expected Output:**
```
System Health Report:
 - Service A: UP
 - Service B: DEGRADED (java.lang.RuntimeException: Service B database unreachable)
 - Service C: UP
```

---

## 6. Architecture & Implementation Comparison

| Feature | `Runnable` | `Callable<V>` | `Future<V>` | `CompletableFuture<V>` |
| :--- | :--- | :--- | :--- | :--- |
| **Return Type** | `void` | `V` | `V` | `V` |
| **Exception Handling** | Must catch inside | `throws Exception` | Throws `ExecutionException` | Functional (`.exceptionally()`) |
| **Execution Trigger** | Fire and forget | Submit to pool | Submit to pool | `supplyAsync()` or manual `.complete()` |
| **Non-blocking Callbacks** | ❌ No | ❌ No | ❌ No (Requires `.get()`) | ✅ Yes (`thenApply`, `thenAccept`) |
| **Multiple Future Composition** | ❌ No | ❌ No | ❌ No | ✅ Yes (`thenCombine`, `allOf`) |
| **Manual Completion** | ❌ No | ❌ No | ❌ No | ✅ Yes (`cf.complete(val)`) |

---

## 7. Real-World Industry Use Cases

### 1. Healthcare: Composite Diagnostic Patient Summary
When a doctor opens an ICU patient's chart, the clinical portal must fetch:
- Vital signs telemetry stream
- Recent blood lab panels
- Radiology imaging reports
- Active drug allergy conflicts

Rather than querying these 4 hospital microservices sequentially (taking $200\text{ms} \times 4 = 800\text{ms}$), all 4 are triggered asynchronously using `supplyAsync` on dedicated I/O pools and merged with `CompletableFuture.allOf()`, reducing doctor screen load time to the duration of the single slowest service ($\sim 200\text{ms}$).

### 2. eCommerce: Parallel Checkout Price Enrichment
At checkout, an order needs:
- Calculating real-time sales tax via Avalara API
- Applying coupon discount rules from promo engine
- Verifying warehouse shipping inventory and estimating freight carrier delivery times

`thenCombine` executes tax and freight calculation in parallel, merging them into a final invoice total before presenting payment methods to the buyer.

### 3. Banking: Real-Time Anti-Fraud & Credit composite evaluation
Before approving a $10,000 wire transfer:
- Service 1 checks the recipient account against OFAC sanctions lists.
- Service 2 runs machine learning fraud pattern analysis.
- Service 3 queries customer daily withdrawal velocity.

If OFAC times out after 1.5 seconds, `completeOnTimeout` triggers a manual audit flag without locking customer funds indefinitely.

---

## 8. Best Practices, Anti-Patterns, and Top 3 Mistakes

### Best Practices:
1. **Always Supply a Custom `Executor` for I/O**: By default, `CompletableFuture.supplyAsync()` runs on `ForkJoinPool.commonPool()`. This pool is sized to CPU core count ($N$) and is meant for CPU-bound computations. If you execute blocking HTTP/DB calls on it, all worker threads become blocked, crippling the entire JVM! Pass a custom `ThreadPoolExecutor` sized for I/O.
2. **Use `thenCompose` for Dependent Async Calls**: If your transformation function itself returns a `CompletableFuture`, use `thenCompose` instead of `thenApply` to avoid ending up with `CompletableFuture<CompletableFuture<T>>`.
3. **Always Register an Exception Handler**: Always attach `.exceptionally()` or `.handle()` to prevent asynchronous exceptions from being swallowed silently.

### Top 3 Mistakes Developers Make:

#### Mistake 1: Calling `.get()` or `.join()` Immediately After Creation
```java
// WRONG: Defeats the entire purpose of asynchronous programming!
CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> fetchUser());
User user = userFuture.join(); // BLOCKS immediately!

// CORRECT: Chain actions reactively
CompletableFuture.supplyAsync(() -> fetchUser(), ioPool)
    .thenApply(user -> formatCard(user))
    .thenAccept(card -> renderUI(card));
```

#### Mistake 2: Starving the Shared `ForkJoinPool` with Blocking I/O
```java
// DANGEROUS: Stalls common pool
CompletableFuture.supplyAsync(() -> makeSlowHttpCall()); // Uses ForkJoinPool.commonPool()

// SAFE: Provide an I/O optimized thread pool
private static final ExecutorService IO_POOL = Executors.newFixedThreadPool(50);
CompletableFuture.supplyAsync(() -> makeSlowHttpCall(), IO_POOL);
```

#### Mistake 3: Nested Callbacks (CompletableFuture Hell)
Instead of nesting `future.thenApply(f -> f2.thenApply(...))`, use `thenCombine` and `thenCompose` to keep pipelines flat, readable, and maintainable.
