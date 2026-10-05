package unit_2_5_advanced_task_execution.exercises.solutions;

import java.util.concurrent.*;

public class AdvancedTaskExecutionSolutions {

    // Exercise 1: Callable Factorial
    public static class FactorialCalculator implements Callable<Long> {
        private final int n;

        public FactorialCalculator(int n) {
            this.n = n;
        }

        @Override
        public Long call() throws Exception {
            if (n < 0) {
                throw new IllegalArgumentException("Factorial cannot be negative: " + n);
            }
            long fact = 1;
            for (int i = 2; i <= n; i++) {
                fact *= i;
            }
            return fact;
        }
    }

    // Exercise 2: Async Transformation Pipeline
    public static class UserTokenPipeline {
        public static CompletableFuture<String> processUserAsync(String rawUsername) {
            return CompletableFuture.supplyAsync(() -> rawUsername.trim().toLowerCase())
                    .thenApply(user -> user + "_SALT_2026")
                    .thenApply(salted -> "TOKEN::" + salted);
        }
    }

    // Exercise 3: Service Merging with Error Recovery
    public static class ServiceMerger {
        public static CompletableFuture<String> mergeServices(
                CompletableFuture<String> serviceA, 
                CompletableFuture<String> serviceB) {
            CompletableFuture<String> safeB = serviceB.exceptionally(ex -> "DEFAULT_B");
            return serviceA.thenCombine(safeB, (a, b) -> a + " & " + b);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running Unit 2.5 Solutions Tests...");

        // Test 1: Callable Factorial
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Future<Long> f1 = pool.submit(new FactorialCalculator(5));
        assert f1.get(1, TimeUnit.SECONDS) == 120L : "5! should be 120";

        Future<Long> f2 = pool.submit(new FactorialCalculator(-1));
        try {
            f2.get(1, TimeUnit.SECONDS);
            assert false : "Should have thrown ExecutionException";
        } catch (ExecutionException e) {
            assert e.getCause() instanceof IllegalArgumentException;
        }
        pool.shutdown();

        // Test 2: UserTokenPipeline
        CompletableFuture<String> tokenFuture = UserTokenPipeline.processUserAsync("  Alice_Wonderland  ");
        String token = tokenFuture.get(2, TimeUnit.SECONDS);
        assert token.equals("TOKEN::alice_wonderland_SALT_2026") : "Got unexpected token: " + token;

        // Test 3: ServiceMerger normal and fallback
        CompletableFuture<String> sa = CompletableFuture.completedFuture("Alpha");
        CompletableFuture<String> sb = CompletableFuture.completedFuture("Beta");
        String normal = ServiceMerger.mergeServices(sa, sb).get(2, TimeUnit.SECONDS);
        assert normal.equals("Alpha & Beta") : "Got: " + normal;

        CompletableFuture<String> failedB = CompletableFuture.failedFuture(new RuntimeException("Down"));
        String recovered = ServiceMerger.mergeServices(sa, failedB).get(2, TimeUnit.SECONDS);
        assert recovered.equals("Alpha & DEFAULT_B") : "Got: " + recovered;

        System.out.println("All Unit 2.5 Solutions Tests PASSED!");
    }
}
