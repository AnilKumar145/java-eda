import java.util.*;

public class LabTests {

    public static class MockSmsGateway implements Solution.SmsGateway {
        int callCount = 0;
        boolean shouldFail = false;

        @Override
        public String sendSms(String phone, String message) {
            callCount++;
            if (shouldFail) {
                throw new RuntimeException("SMS Carrier 503 Service Unavailable");
            }
            return "SMS-OK-101";
        }
    }

    public static class MockPagerGateway implements Solution.PagerGateway {
        int callCount = 0;
        boolean shouldFail = false;

        @Override
        public String sendPager(String pagerCode, String message) {
            callCount++;
            if (shouldFail) {
                throw new RuntimeException("Pager Antenna RF Link Down");
            }
            return "PAGER-OK-202";
        }
    }

    public static void main(String[] args) {
        testPrimarySmsSuccessNeverCallsSecondaryPager();
        testPrimarySmsFailureTriggersSecondaryPagerFallback();
        testBothChannelsFailingReportsFailure();
        testInputValidationRejectsEmptyAlertId();
        System.out.println("All Unit 5.4 Lab 1 Tests Passed Successfully!");
    }

    public static void testPrimarySmsSuccessNeverCallsSecondaryPager() {
        MockSmsGateway smsMock = new MockSmsGateway();
        MockPagerGateway pagerMock = new MockPagerGateway();

        Solution.EmergencyAlertDispatcher dispatcher =
            new Solution.EmergencyAlertDispatcher(smsMock, pagerMock);

        Solution.DispatchResult result = dispatcher.dispatchCriticalAlert(
            "ALERT-001", "+15551234567", "PAGER-ICU-1", "Code Blue Bed 12"
        );

        assert result.isSuccess() : "Expected success on primary channel";
        assert "SMS".equals(result.deliveredChannel()) : "Expected SMS channel, got " + result.deliveredChannel();
        assert smsMock.callCount == 1 : "Expected SMS gateway called exactly once";
        assert pagerMock.callCount == 0 : "Secondary pager must NEVER be called when SMS succeeds";
        System.out.println("Lab 1 Test 1 Passed: Primary SMS success without fallback verified.");
    }

    public static void testPrimarySmsFailureTriggersSecondaryPagerFallback() {
        MockSmsGateway smsMock = new MockSmsGateway();
        smsMock.shouldFail = true;
        MockPagerGateway pagerMock = new MockPagerGateway();

        Solution.EmergencyAlertDispatcher dispatcher =
            new Solution.EmergencyAlertDispatcher(smsMock, pagerMock);

        Solution.DispatchResult result = dispatcher.dispatchCriticalAlert(
            "ALERT-002", "+15551234567", "PAGER-ICU-1", "Sepsis Alert ICU"
        );

        assert result.isSuccess() : "Expected success via fallback";
        assert "PAGER_FALLBACK".equals(result.deliveredChannel()) : "Expected PAGER_FALLBACK, got " + result.deliveredChannel();
        assert smsMock.callCount == 1 : "SMS should have been attempted once";
        assert pagerMock.callCount == 1 : "Pager must be called once on SMS failure";
        System.out.println("Lab 1 Test 2 Passed: Primary SMS failure fallback to Pager verified.");
    }

    public static void testBothChannelsFailingReportsFailure() {
        MockSmsGateway smsMock = new MockSmsGateway();
        smsMock.shouldFail = true;
        MockPagerGateway pagerMock = new MockPagerGateway();
        pagerMock.shouldFail = true;

        Solution.EmergencyAlertDispatcher dispatcher =
            new Solution.EmergencyAlertDispatcher(smsMock, pagerMock);

        Solution.DispatchResult result = dispatcher.dispatchCriticalAlert(
            "ALERT-003", "+15551234567", "PAGER-ICU-1", "STEMI Alert Cath Lab"
        );

        assert !result.isSuccess() : "Expected failure when both channels fail";
        assert "NONE".equals(result.deliveredChannel()) : "Expected NONE channel";
        assert smsMock.callCount == 1 : "SMS attempted once";
        assert pagerMock.callCount == 1 : "Pager attempted once";
        assert result.errorDetail().contains("Primary SMS failed") : "Error detail should mention SMS";
        assert result.errorDetail().contains("Fallback Pager failed") : "Error detail should mention Pager";
        System.out.println("Lab 1 Test 3 Passed: Both channels failing handled cleanly.");
    }

    public static void testInputValidationRejectsEmptyAlertId() {
        MockSmsGateway smsMock = new MockSmsGateway();
        MockPagerGateway pagerMock = new MockPagerGateway();
        Solution.EmergencyAlertDispatcher dispatcher =
            new Solution.EmergencyAlertDispatcher(smsMock, pagerMock);

        boolean caught = false;
        try {
            dispatcher.dispatchCriticalAlert("", "+15551234567", "PAGER-1", "Message");
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assert caught : "Should reject empty alert id";
        assert smsMock.callCount == 0 : "Should not call SMS if input invalid";
        System.out.println("Lab 1 Test 4 Passed: Input validation rejection verified.");
    }
}
