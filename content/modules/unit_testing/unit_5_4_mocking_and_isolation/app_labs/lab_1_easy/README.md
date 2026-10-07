# Application Lab 1: Critical Alert SMS and Pager Notification Gateway

## Clinical & Architectural Context
In an acute care hospital, rapid response dispatchers broadcast critical alerts (Code Blue, Sepsis Alert, STEMI alert) to attending physicians. The dispatch engine uses a primary cloud SMS gateway. However, cellular networks can experience outages or congestion. If the SMS gateway fails or raises an error, the system must immediately fall back to the hospital high-frequency radio pager service.

In this lab, you will design and verify an **Emergency Alert Dispatcher** in Java using test doubles, verifying method stubbing contracts, fallback routing, and strict zero-interaction assertions on secondary channels during primary success paths.

---

## Architecture Overview

```
                      +-----------------------------------+
                      |            LabTests               |
                      |   - Mock Primary SMS Gateway      |
                      |   - Mock Secondary Pager Gateway  |
                      |   - Interaction & Fallback Verif  |
                      +-----------------+-----------------+
                                        |
                                        v
                      +-----------------------------------+
                      |     EmergencyAlertDispatcher      |
                      |   - dispatchCriticalAlert(...)    |
                      |   - Primary Channel: SMS          |
                      |   - Fallback Channel: PAGER       |
                      +-----------------------------------+
```

---

## Lab Deliverables
1. **`StarterCode.java`**: Gateway interfaces and dispatcher stubs.
2. **`tasks.md`**: Specification and requirements.
3. **`solution/Solution.java`**: Complete reference implementation.
4. **`LabTests.java`**: Verification suite with mock test doubles.

---

## Verification
Compile and run:
```bash
javac -d .temp_bin content/modules/unit_testing/unit_5_4_mocking_and_isolation/app_labs/lab_1_easy/solution/Solution.java content/modules/unit_testing/unit_5_4_mocking_and_isolation/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```
