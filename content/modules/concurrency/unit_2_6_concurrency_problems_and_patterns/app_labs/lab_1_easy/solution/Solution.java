package unit_2_6_concurrency_problems_and_patterns.app_labs.lab_1_easy.solution;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Solution {

    public record TileStats(long totalIntensity, int hotPixelCount) {
        public TileStats merge(TileStats other) {
            return new TileStats(
                    this.totalIntensity + other.totalIntensity,
                    this.hotPixelCount + other.hotPixelCount
            );
        }
    }

    public static class ImageTileTask extends RecursiveTask<TileStats> {
        private final int[] pixels;
        private final int start;
        private final int end;
        private final int threshold;
        private final int hotPixelCutoff;

        public ImageTileTask(int[] pixels, int start, int end, int threshold, int hotPixelCutoff) {
            this.pixels = pixels;
            this.start = start;
            this.end = end;
            this.threshold = threshold;
            this.hotPixelCutoff = hotPixelCutoff;
        }

        @Override
        protected TileStats compute() {
            int length = end - start;
            if (length <= threshold) {
                long sum = 0;
                int hot = 0;
                for (int i = start; i < end; i++) {
                    int val = pixels[i];
                    sum += val;
                    if (val >= hotPixelCutoff) {
                        hot++;
                    }
                }
                return new TileStats(sum, hot);
            }

            int mid = start + length / 2;
            ImageTileTask left = new ImageTileTask(pixels, start, mid, threshold, hotPixelCutoff);
            ImageTileTask right = new ImageTileTask(pixels, mid, end, threshold, hotPixelCutoff);

            left.fork();
            TileStats rightStats = right.compute();
            TileStats leftStats = left.join();

            return leftStats.merge(rightStats);
        }
    }

    public static TileStats analyzeScan(int[] pixels, int threshold, int hotPixelCutoff, ForkJoinPool pool) {
        return pool.invoke(new ImageTileTask(pixels, 0, pixels.length, threshold, hotPixelCutoff));
    }
}
