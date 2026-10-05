package unit_2_7_choosing_the_right_concurrency_model.app_labs.lab_1_easy;

import unit_2_7_choosing_the_right_concurrency_model.app_labs.lab_1_easy.solution.Solution.*;

public class LabTests {

    public static void main(String[] args) throws Exception {
        System.out.println("Running Unit 2.7 Lab Tests...");

        testCounterIntegrityAcrossStrategies();

        System.out.println("All Unit 2.7 Lab tests PASSED!");
    }

    private static void testCounterIntegrityAcrossStrategies() throws Exception {
        int threads = 10;
        int perThread = 1000;
        long expectedTotal = (long) threads * perThread;

        TelemetryCounter syncCounter = new SynchronizedCounter();
        TelemetryCounter lockCounter = new ReentrantLockCounter();
        TelemetryCounter atomicCounter = new AtomicCounter();

        BenchmarkResult rSync = BenchmarkRunner.runBenchmark(syncCounter, "Synchronized", threads, perThread);
        BenchmarkResult rLock = BenchmarkRunner.runBenchmark(lockCounter, "ReentrantLock", threads, perThread);
        BenchmarkResult rAtomic = BenchmarkRunner.runBenchmark(atomicCounter, "Atomic", threads, perThread);

        assert rSync.totalCount() == expectedTotal : "Sync counter failed: " + rSync.totalCount();
        assert rLock.totalCount() == expectedTotal : "Lock counter failed: " + rLock.totalCount();
        assert rAtomic.totalCount() == expectedTotal : "Atomic counter failed: " + rAtomic.totalCount();

        assert rSync.elapsedMs() >= 0;
        assert rLock.elapsedMs() >= 0;
        assert rAtomic.elapsedMs() >= 0;

        System.out.printf("Benchmark Summary (%d threads, %d ops/thread):%n", threads, perThread);
        System.out.printf(" - %s: %d ms%n", rSync.strategyName(), rSync.elapsedMs());
        System.out.printf(" - %s: %d ms%n", rLock.strategyName(), rLock.elapsedMs());
        System.out.printf(" - %s: %d ms%n", rAtomic.strategyName(), rAtomic.elapsedMs());
    }
}
