package unit_2_6_concurrency_problems_and_patterns.exercises.solutions;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ConcurrencyProblemsSolutions {

    // Exercise 1: Deadlock-Free Resource Transfer
    public static class BankAccount {
        private final int id;
        private int balance;

        public BankAccount(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }

        public int getId() { return id; }
        public int getBalance() { return balance; }

        public static void transfer(BankAccount from, BankAccount to, int amount) {
            if (from.getId() == to.getId()) return; // Cannot transfer to same account

            BankAccount first = from.getId() < to.getId() ? from : to;
            BankAccount second = from.getId() < to.getId() ? to : from;

            synchronized (first) {
                synchronized (second) {
                    from.balance -= amount;
                    to.balance += amount;
                }
            }
        }
    }

    // Exercise 2: Fork/Join Array Maximum
    public static class MaxValueTask extends RecursiveTask<Integer> {
        private static final int THRESHOLD = 100;
        private final int[] array;
        private final int start;
        private final int end;

        public MaxValueTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            int length = end - start;
            if (length <= THRESHOLD) {
                int max = Integer.MIN_VALUE;
                for (int i = start; i < end; i++) {
                    if (array[i] > max) {
                        max = array[i];
                    }
                }
                return max;
            }

            int mid = start + length / 2;
            MaxValueTask left = new MaxValueTask(array, start, mid);
            MaxValueTask right = new MaxValueTask(array, mid, end);

            left.fork();
            int rightRes = right.compute();
            int leftRes = left.join();

            return Math.max(leftRes, rightRes);
        }
    }

    // Exercise 3: Safe Parallel Stream Aggregation
    public static class ParallelStreamProcessor {
        public static long sumSquaresParallel(List<Integer> numbers) {
            return numbers.parallelStream()
                    .mapToLong(n -> (long) n * n)
                    .sum();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running Unit 2.6 Solutions Tests...");

        // Test 1: Deadlock-free Transfer
        BankAccount b1 = new BankAccount(101, 10000);
        BankAccount b2 = new BankAccount(202, 10000);
        int transfers = 1000;
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(transfers);

        for (int i = 0; i < transfers; i++) {
            final boolean direction = (i % 2 == 0);
            pool.submit(() -> {
                try {
                    if (direction) {
                        BankAccount.transfer(b1, b2, 10);
                    } else {
                        BankAccount.transfer(b2, b1, 10);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(5, TimeUnit.SECONDS);
        pool.shutdown();

        assert (b1.getBalance() + b2.getBalance()) == 20000 : "Total funds must remain constant";

        // Test 2: ForkJoin Max
        int[] data = new int[1000];
        for (int i = 0; i < 1000; i++) data[i] = i * 2;
        data[483] = 99999; // Set one high spike

        ForkJoinPool fjp = new ForkJoinPool();
        int max = fjp.invoke(new MaxValueTask(data, 0, data.length));
        assert max == 99999 : "Max should be 99999, got " + max;
        fjp.shutdown();

        // Test 3: Parallel Stream
        List<Integer> nums = IntStream.rangeClosed(1, 100).boxed().toList();
        long sumSq = ParallelStreamProcessor.sumSquaresParallel(nums);
        // Formula: n(n+1)(2n+1)/6 for n=100 -> 100*101*201/6 = 338350
        assert sumSq == 338350L : "Expected 338350, got " + sumSq;

        System.out.println("All Unit 2.6 Solutions Tests PASSED!");
    }
}
