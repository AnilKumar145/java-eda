# Unit 5.4: Test Doubles, Mockito Fundamentals, and Interaction Verification

---

## Part 1: Clinical & Conceptual Motivation

In hospital telemetry monitoring, when a patient enters ventricular fibrillation (cardiac arrest), the telemetry monitor triggers an emergency code blue alert. The notification gateway attempts to deliver an urgent SMS to the on-duty rapid response physician. If the SMS gateway fails (e.g., carrier outage or timeout), the system must immediately fall back to the hospital radio-frequency pager network.

If we attempted to test this alerting service using live SMS gateways:
1. Every automated test run would send real emergency alerts to doctors' personal cell phones, creating alert fatigue or emergency panic.
2. Every test run would cost money in SMS API fees.
3. If the hospital Wi-Fi or cellular network drops, our test suite would fail even if our Java code was completely defect-free.

To test this behavior deterministically, we need **Test Doubles** (Mocks and Stubs) to isolate our code under test from external infrastructure.

---

## Part 2: Core Concepts & Definitions

### 1. The Taxonomy of Test Doubles
Defined by Gerard Meszaros in *xUnit Test Patterns*:
- **Dummy**: An object passed around simply to satisfy method signatures, never actually used or called.
- **Stub**: Provides canned, pre-programmed answers to method calls made during the test.
- **Spy**: A wrapper around a real object that records method calls and arguments for later inspection.
- **Mock**: An object pre-programmed with expectations about calls it should receive, verifying interactions directly.
- **Fake**: A lightweight, working implementation of an interface (e.g., an in-memory repository or hash map).

### 2. Mockito in the Java Ecosystem
Mockito is the gold standard mocking framework for Java. It uses dynamic byte-code proxies to intercept method calls:
- `Mockito.mock(Service.class)` creates a dynamic proxy.
- `when(mock.method()).thenReturn(result)` programs return values.
- `verify(mock, times(1)).method(args)` asserts that the method was invoked exactly once with matching parameters.

---

## Part 3: Code Architecture & Implementation Patterns

### 1. Stubbing Indirect Inputs
```java
SmsGateway mockSms = Mockito.mock(SmsGateway.class);

// Stubbing success
Mockito.when(mockSms.sendSms(eq("+15551234567"), anyString()))
       .thenReturn(new SmsResponse("MSG-001", "DELIVERED"));

// Stubbing failure
Mockito.when(mockSms.sendSms(eq("+15550000000"), anyString()))
       .thenThrow(new GatewayTimeoutException("Carrier timed out"));
```

### 2. Interaction Verification
```java
// Assert method was called exactly once
Mockito.verify(mockSms, Mockito.times(1)).sendSms(anyString(), anyString());

// Assert secondary fallback was NEVER called on primary success
Mockito.verify(mockPager, Mockito.never()).broadcastPager(anyString(), anyString());

// Assert zero interactions across entire mock
Mockito.verifyNoInteractions(mockPager);
```

---

## Part 4: Common Pitfalls & Anti-Patterns

### 1. "Over-Mocking" (Testing Mockito Instead of Your Business Logic)
Mocking simple Java POJOs, Records, or utility classes instead of instantiating real ones.
*Rule*: **Only mock across architectural boundaries** (external HTTP APIs, hardware interfaces, email/SMS services, database connections). Never mock pure domain logic.

### 2. Mocking What You Don't Own Without Contract Tests
Mocking a third-party vendor SDK with assumptions that don't match the vendor's real runtime behavior. When the vendor updates their API, your tests pass but production crashes.

### 3. Missing `verifyNoInteractions` on Negative Paths
Verifying that the primary service was called, but forgetting to assert that critical secondary channels (or billing meters) were **not** called.

---

## Part 5: Production Patterns & Enterprise Recipes

### Pattern: Primary Gateway with Automatic Fallback
```java
public class AlertDispatcher {
    private final SmsGateway smsGateway;
    private final PagerGateway pagerGateway;

    public AlertDispatcher(SmsGateway smsGateway, PagerGateway pagerGateway) {
        this.smsGateway = smsGateway;
        this.pagerGateway = pagerGateway;
    }

    public DispatchReport dispatchEmergencyAlert(String recipientPhone, String pagerCode, String message) {
        try {
            SmsReceipt receipt = smsGateway.send(recipientPhone, message);
            return new DispatchReport(receipt.id(), "SMS", true);
        } catch (Exception e) {
            // Primary channel failed, fall back to RF pager
            PagerReceipt receipt = pagerGateway.page(pagerCode, message);
            return new DispatchReport(receipt.id(), "PAGER_FALLBACK", true);
        }
    }
}
```

---

## Part 6: Hands-On Walkthrough & Analysis

To test `AlertDispatcher`:
1. **Test 1 (Primary Success)**:
   - Stub `smsGateway.send(...)` to succeed.
   - Act: call `dispatchEmergencyAlert(...)`.
   - Assert: returns channel `"SMS"`.
   - Verify: `smsGateway` called once, `pagerGateway` called **zero times** (`never()`).
2. **Test 2 (Primary Failure -> Fallback Success)**:
   - Stub `smsGateway.send(...)` to throw `RuntimeException("Network down")`.
   - Stub `pagerGateway.page(...)` to succeed.
   - Act: call `dispatchEmergencyAlert(...)`.
   - Assert: returns channel `"PAGER_FALLBACK"`.
   - Verify: `smsGateway` called once, `pagerGateway` called once.

---

## Part 7: Exercises & Application Lab Preview

- **Exercises (`exercises/`)**:
  - Implement and verify stubbing contracts and interaction counters with standalone Java test doubles.
- **Application Lab (`app_labs/lab_1_easy/`)**:
  - Build and verify the **Critical Alert SMS and Pager Notification Gateway**, asserting interaction counts, fallback transitions, and error handling.

---

## Part 8: Key Takeaways & Review Checklist

- [ ] Use stubs to provide indirect inputs into the code under test.
- [ ] Use mocks to verify indirect outputs and method call frequency.
- [ ] Never mock value objects or simple domain entities.
- [ ] Always assert `never()` on fallback channels during primary success cases.
