import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Verification Tests for Lab 1 Easy: Hospital Pharmacy Inventory Dispenser (Java)
 */
public class LabTests {

    public static void main(String[] args) throws Exception {
        testConcurrentFullDispensation();
        testConcurrentContentionWithShortage();
        System.out.println("All Lab 1 Easy Tests Passed!");
    }

    public static void testConcurrentFullDispensation() throws Exception {
        Solution.PharmacyInventoryDispenser dispenser = new Solution.PharmacyInventoryDispenser();
        dispenser.restock("AMOXICILLIN-500MG", 100);

        List<Thread> nurses = new ArrayList<>();
        AtomicInteger successfulDispenses = new AtomicInteger(0);

        for (int i = 0; i < 10; i++) {
            nurses.add(new Thread(() -> {
                if (dispenser.dispense("AMOXICILLIN-500MG", 10)) {
                    successfulDispenses.incrementAndGet();
                }
            }));
        }

        for (Thread t : nurses) t.start();
        for (Thread t : nurses) t.join();

        assert successfulDispenses.get() == 10 : "Expected 10 successful dispenses, got " + successfulDispenses.get();
        assert dispenser.getStock("AMOXICILLIN-500MG") == 0 : "Remaining stock should be 0, got " + dispenser.getStock("AMOXICILLIN-500MG");
        System.out.println("Test 1 passed! All 100 units dispensed accurately across 10 concurrent nurses.");
    }

    public static void testConcurrentContentionWithShortage() throws Exception {
        Solution.PharmacyInventoryDispenser dispenser = new Solution.PharmacyInventoryDispenser();
        dispenser.restock("MORPHINE-10MG", 4);

        List<Thread> nurses = new ArrayList<>();
        AtomicInteger approvals = new AtomicInteger(0);
        AtomicInteger rejections = new AtomicInteger(0);

        // 10 nurses competing for 4 doses
        for (int i = 0; i < 10; i++) {
            nurses.add(new Thread(() -> {
                if (dispenser.dispense("MORPHINE-10MG", 1)) {
                    approvals.incrementAndGet();
                } else {
                    rejections.incrementAndGet();
                }
            }));
        }

        for (Thread t : nurses) t.start();
        for (Thread t : nurses) t.join();

        assert approvals.get() == 4 : "Expected exactly 4 approvals, got " + approvals.get();
        assert rejections.get() == 6 : "Expected exactly 6 rejections, got " + rejections.get();
        assert dispenser.getStock("MORPHINE-10MG") == 0 : "Stock should be exactly 0, got " + dispenser.getStock("MORPHINE-10MG");
        System.out.println("Test 2 passed! Controlled contention: 4 approved, 6 rejected, 0 deficit.");
    }
}
