package unit_2_5_advanced_task_execution.exercises;

import java.util.concurrent.*;

public class AdvancedTaskExecutionExercises {

    // Exercise 1: Callable Factorial
    public static class FactorialCalculator implements Callable<Long> {
        private final int n;

        public FactorialCalculator(int n) {
            this.n = n;
        }

        @Override
        public Long call() throws Exception {
            // TODO: Calculate factorial of n. Throw IllegalArgumentException if n < 0.
            return 0L;
        }
    }

    // Exercise 2: Async Transformation Pipeline
    public static class UserTokenPipeline {
        public static CompletableFuture<String> processUserAsync(String rawUsername) {
            // TODO:
            // Stage 1: supplyAsync - trim and lowercase rawUsername
            // Stage 2: thenApply - append "_SALT_2026"
            // Stage 3: thenApply - prefix with "TOKEN::"
            return null;
        }
    }

    // Exercise 3: Service Merging with Error Recovery
    public static class ServiceMerger {
        public static CompletableFuture<String> mergeServices(
                CompletableFuture<String> serviceA, 
                CompletableFuture<String> serviceB) {
            // TODO:
            // Ensure serviceB has a fallback if it fails using .exceptionally(ex -> "DEFAULT_B")
            // Combine serviceA and serviceB with thenCombine: return "serviceA_res + " & " + serviceB_res"
            return null;
        }
    }
}
