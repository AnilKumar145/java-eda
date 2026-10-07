public class StarterCode {

    public record DispatchResult(String alertId, String deliveredChannel, boolean isSuccess, String errorDetail) {}

    public interface SmsGateway {
        String sendSms(String phone, String message);
    }

    public interface PagerGateway {
        String sendPager(String pagerCode, String message);
    }

    public static class EmergencyAlertDispatcher {
        public EmergencyAlertDispatcher(SmsGateway smsGateway, PagerGateway pagerGateway) {
            // TODO: Store gateway instances.
        }

        public DispatchResult dispatchCriticalAlert(
            String alertId,
            String phone,
            String pagerCode,
            String alertMessage
        ) {
            // TODO: Validate arguments.
            // Attempt sendSms. If succeeds -> return DispatchResult("SMS", true).
            // If sendSms fails -> attempt sendPager. If succeeds -> return DispatchResult("PAGER_FALLBACK", true).
            // If both fail -> return DispatchResult("NONE", false).
            throw new UnsupportedOperationException("TODO: Implement dispatchCriticalAlert");
        }
    }
}
