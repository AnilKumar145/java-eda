# Lab 1 Tasks: Diagnostic Image Parallel Tile Processor

## Task 1: Tile Statistics Record
- Define `TileStats(long totalIntensity, int hotPixelCount)` record:
  - Add helper method `TileStats merge(TileStats other)` combining both statistics.

## Task 2: Recursive Image Processor Task
- Implement class `ImageTileTask extends RecursiveTask<TileStats>`:
  - Fields: `int[] pixels`, `int start`, `int end`, `int threshold`, `int hotPixelCutoff`.
  - In `compute()`:
    - If `(end - start) <= threshold`: calculate intensity sum and count pixels where `pixel >= hotPixelCutoff`. Return `new TileStats(sum, hotCount)`.
    - Else:
      - Split range at midpoint.
      - Create `ImageTileTask left` and `ImageTileTask right`.
      - Call `left.fork()`.
      - Compute `right.compute()`.
      - Wait for `left.join()`.
      - Return `leftResult.merge(rightResult)`.

## Task 3: Execution and Integration
- Implement `TileStats analyzeScan(int[] pixels, int threshold, int hotPixelCutoff, ForkJoinPool pool)` returning the combined `TileStats`.
