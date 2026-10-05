/**
 * ICU Bed Reservation Gateway - Starter Code
 */
public class StarterCode {

    public static class ICUBedReservationGateway {
        // TODO: Define Semaphore and ReentrantLock

        public ICUBedReservationGateway(int totalBeds) {
            // TODO: Initialize semaphore and lock
        }

        public boolean reserveBed(String patientId, long timeoutMs) throws InterruptedException {
            // TODO: Acquire permit within timeout, record reservation
            return false;
        }

        public boolean releaseBed(String patientId) {
            // TODO: Remove reservation, release permit
            return false;
        }

        public int getOccupiedCount() {
            // TODO: Return count of currently reserved beds
            return 0;
        }
    }
}
