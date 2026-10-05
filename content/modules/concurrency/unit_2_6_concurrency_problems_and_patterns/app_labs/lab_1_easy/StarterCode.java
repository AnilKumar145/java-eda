package unit_2_6_concurrency_problems_and_patterns.app_labs.lab_1_easy;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class StarterCode {

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
            // TODO:
            // 1. If (end - start) <= threshold:
            //    Loop from start to end, summing intensity and counting hot pixels (>= hotPixelCutoff)
            // 2. Else:
            //    Split in half, leftTask.fork(), rightTask.compute(), leftTask.join(), return left.merge(right)
            return null;
        }
    }

    public static TileStats analyzeScan(int[] pixels, int threshold, int hotPixelCutoff, ForkJoinPool pool) {
        return pool.invoke(new ImageTileTask(pixels, 0, pixels.length, threshold, hotPixelCutoff));
    }
}
