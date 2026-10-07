# Lab Tasks: Critical Alert SMS and Pager Notification Gateway

## Task 1: Declare Service Interfaces & Result Model
In `Solution.java`:
- `record DispatchResult(String alertId, String deliveredChannel, boolean isSuccess, String errorDetail)`
- `interface SmsGateway { String sendSms(String phone, String message); }`
- `interface PagerGateway { String sendPager(String pagerCode, String message); }`

## Task 2: Implement Dispatcher Logic
Implement `EmergencyAlertDispatcher(SmsGateway smsGateway, PagerGateway pagerGateway)`:
- `dispatchCriticalAlert(String alertId, String phone, String pagerCode, String alertMessage)`:
  - Validate non-empty fields (else throw `IllegalArgumentException`).
  - Attempt `smsGateway.sendSms(phone, alertMessage)`.
  - On SMS success, return `DispatchResult(alertId, "SMS", true, null)`.
  - On SMS exception, catch and attempt `pagerGateway.sendPager(pagerCode, alertMessage)`.
  - On pager success, return `DispatchResult(alertId, "PAGER_FALLBACK", true, null)`.
  - If both fail, return `DispatchResult(alertId, "NONE", false, combinedErrorMessage)`.

## Task 3: Author Verification Tests with Test Doubles
In `LabTests.java`:
1. Test Primary SMS Success: verify primary SMS called once, secondary pager called ZERO times (`never`).
2. Test Primary SMS Failure Fallback: verify primary SMS called once, secondary pager called once, returns channel `PAGER_FALLBACK`.
3. Test Both Channels Failing: verify both called once, returns channel `NONE` with `isSuccess == false`.
4. Test Input Validation: verify empty alert ID throws `IllegalArgumentException`.
