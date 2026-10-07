public class Solution {

    public record DispatchResult(String alertId, String deliveredChannel, boolean isSuccess, String errorDetail) {}

    public interface SmsGateway {
        String sendSms(String phone, String message);
    }

    public interface PagerGateway {
        String sendPager(String pagerCode, String message);
    }

    public static class EmergencyAlertDispatcher {
        private final SmsGateway smsGateway;
        private final PagerGateway pagerGateway;

        public EmergencyAlertDispatcher(SmsGateway smsGateway, PagerGateway pagerGateway) {
            this.smsGateway = smsGateway;
            this.pagerGateway = pagerGateway;
        }

        public DispatchResult dispatchCriticalAlert(
            String alertId,
            String phone,
            String pagerCode,
            String alertMessage
        ) {
            if (alertId == null || alertId.isBlank()) {
                throw new IllegalArgumentException("Alert ID cannot be empty");
            }
            if (phone == null || phone.isBlank()) {
                throw new IllegalArgumentException("Phone number cannot be empty");
            }
            if (pagerCode == null || pagerCode.isBlank()) {
                throw new IllegalArgumentException("Pager code cannot be empty");
            }
            if (alertMessage == null || alertMessage.isBlank()) {
                throw new IllegalArgumentException("Alert message cannot be empty");
            }

            try {
                smsGateway.sendSms(phone, alertMessage);
                return new DispatchResult(alertId, "SMS", true, null);
            } catch (Exception smsEx) {
                // SMS failed, attempt secondary RF pager
                try {
                    pagerGateway.sendPager(pagerCode, alertMessage);
                    return new DispatchResult(alertId, "PAGER_FALLBACK", true, null);
                } catch (Exception pagerEx) {
                    String err = "Primary SMS failed: " + smsEx.getMessage() + "; Fallback Pager failed: " + pagerEx.getMessage();
                    return new DispatchResult(alertId, "NONE", false, err);
                }
            }
        }
    }
}
