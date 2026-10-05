import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ICU Bed Reservation Gateway - Solution
 */
public class Solution {

    public static class ICUBedReservationGateway {
        private final Semaphore availableBeds;
        private final Set<String> activeReservations = new HashSet<>();
        private final ReentrantLock registryLock = new ReentrantLock();

        public ICUBedReservationGateway(int totalBeds) {
            this.availableBeds = new Semaphore(totalBeds, true);
        }

        public boolean reserveBed(String patientId, long timeoutMs) throws InterruptedException {
            if (availableBeds.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
                registryLock.lock();
                try {
                    activeReservations.add(patientId);
                    return true;
                } finally {
                    registryLock.unlock();
                }
            }
            return false;
        }

        public boolean releaseBed(String patientId) {
            registryLock.lock();
            try {
                if (activeReservations.remove(patientId)) {
                    availableBeds.release();
                    return true;
                }
                return false;
            } finally {
                registryLock.unlock();
            }
        }

        public int getOccupiedCount() {
            registryLock.lock();
            try {
                return activeReservations.size();
            } finally {
                registryLock.unlock();
            }
        }
    }
}
