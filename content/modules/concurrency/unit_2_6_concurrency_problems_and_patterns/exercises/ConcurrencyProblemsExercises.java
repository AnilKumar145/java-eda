package unit_2_6_concurrency_problems_and_patterns.exercises;

import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ConcurrencyProblemsExercises {

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
            // TODO: Enforce global lock ordering based on account id
            // Acquire lower id lock first, then higher id lock
            // Transfer amount from 'from' to 'to'
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
            // TODO:
            // If (end - start) <= THRESHOLD, scan array sequentially and return max
            // Else split in half:
            //   create leftTask and rightTask
            //   leftTask.fork()
            //   compute rightTask
            //   join leftTask
            //   return Math.max(leftResult, rightResult)
            return 0;
        }
    }

    // Exercise 3: Safe Parallel Stream Aggregation
    public static class ParallelStreamProcessor {
        public static long sumSquaresParallel(List<Integer> numbers) {
            // TODO: Use parallelStream() to square each number and sum all squares
            return 0L;
        }
    }
}
