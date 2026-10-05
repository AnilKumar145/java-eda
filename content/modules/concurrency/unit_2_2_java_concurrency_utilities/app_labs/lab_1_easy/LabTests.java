/**
 * Verification Tests for Lab 1 Easy: ICU Bed Reservation Gateway (Java)
 */
public class LabTests {

    public static void main(String[] args) throws Exception {
        testBoundedBedReservation();
        testTimeoutWhenFull();
        testReleaseAndReacquire();
        System.out.println("All Lab 1 Easy Tests Passed!");
    }

    public static void testBoundedBedReservation() throws Exception {
        Solution.ICUBedReservationGateway gateway = new Solution.ICUBedReservationGateway(3);

        assert gateway.reserveBed("PAT-1", 100) : "Reservation 1 should succeed";
        assert gateway.reserveBed("PAT-2", 100) : "Reservation 2 should succeed";
        assert gateway.reserveBed("PAT-3", 100) : "Reservation 3 should succeed";

        assert gateway.getOccupiedCount() == 3 : "Expected 3 occupied beds";
        System.out.println("Test 1 passed! Exactly 3 beds reserved.");
    }

    public static void testTimeoutWhenFull() throws Exception {
        Solution.ICUBedReservationGateway gateway = new Solution.ICUBedReservationGateway(2);
        gateway.reserveBed("PAT-A", 50);
        gateway.reserveBed("PAT-B", 50);

        // 3rd request should fail on timeout
        boolean success = gateway.reserveBed("PAT-C", 40);
        assert !success : "3rd request should have timed out";
        assert gateway.getOccupiedCount() == 2;
        System.out.println("Test 2 passed! Overflow request timed out cleanly.");
    }

    public static void testReleaseAndReacquire() throws Exception {
        Solution.ICUBedReservationGateway gateway = new Solution.ICUBedReservationGateway(1);
        assert gateway.reserveBed("PAT-ONLY", 50);

        // Releasing bed
        assert gateway.releaseBed("PAT-ONLY");
        assert gateway.getOccupiedCount() == 0;

        // Another patient acquires the freed bed
        assert gateway.reserveBed("PAT-NEXT", 50);
        assert gateway.getOccupiedCount() == 1;
        System.out.println("Test 3 passed! Bed released and reacquired.");
    }
}
