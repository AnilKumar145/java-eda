package unit_2_6_concurrency_problems_and_patterns.app_labs.lab_1_easy;

import unit_2_6_concurrency_problems_and_patterns.app_labs.lab_1_easy.solution.Solution;
import unit_2_6_concurrency_problems_and_patterns.app_labs.lab_1_easy.solution.Solution.TileStats;

import java.util.concurrent.ForkJoinPool;

public class LabTests {

    public static void main(String[] args) throws Exception {
        System.out.println("Running Unit 2.6 Lab Tests...");

        testImageTileProcessing();
        testSingleTileBoundary();

        System.out.println("All Unit 2.6 Lab tests PASSED!");
    }

    private static void testImageTileProcessing() {
        int size = 10000;
        int[] pixels = new int[size];
        long expectedSum = 0;
        int expectedHot = 0;

        for (int i = 0; i < size; i++) {
            // Values between 0 and 255
            pixels[i] = i % 256;
            expectedSum += pixels[i];
            if (pixels[i] >= 200) {
                expectedHot++;
            }
        }

        ForkJoinPool pool = new ForkJoinPool();
        TileStats stats = Solution.analyzeScan(pixels, 250, 200, pool);

        assert stats.totalIntensity() == expectedSum : "Expected intensity " + expectedSum + ", got " + stats.totalIntensity();
        assert stats.hotPixelCount() == expectedHot : "Expected hot pixels " + expectedHot + ", got " + stats.hotPixelCount();

        pool.shutdown();
    }

    private static void testSingleTileBoundary() {
        int[] pixels = {10, 50, 220, 255, 30};
        ForkJoinPool pool = new ForkJoinPool(1);
        TileStats stats = Solution.analyzeScan(pixels, 10, 200, pool);

        assert stats.totalIntensity() == (10 + 50 + 220 + 255 + 30);
        assert stats.hotPixelCount() == 2;

        pool.shutdown();
    }
}
