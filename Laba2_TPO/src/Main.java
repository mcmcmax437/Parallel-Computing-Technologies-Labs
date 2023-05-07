import Fox.FoxAlgorithm;
import Stripe.StripeAlgorithm;
import java.util.Random;

public class Main {

    public static int[][] setUpRandomMatrix(int n) {
        return setUpRandomMatrix(n,n);
    }
    public static int[][] setUpRandomMatrix(int m, int n) {
        int[][] matrix = new int[m][n];
        Random rand = new Random();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = rand.nextInt(1,9);
            }
        }
        return matrix;
    }
    public static void main(String[] args) throws InterruptedException {
        TestCalculation();
    }

    public static void TestCalculation() {
        int[] sizes = {500, 1000, 2000};
        int[] numThreads = {50, 100, 250};
        for (int size : sizes) {
            System.out.println("Matrix size: " + size);

            for (int threads : numThreads) {
                System.out.println("Threads: " + threads);

                int[][] matrixA = setUpRandomMatrix(size);
                int[][] matrixB = setUpRandomMatrix(size);

                timer(() -> Basic.multiply(matrixA, matrixB), "Basic");
                timer(() -> StripeAlgorithm.multiply(matrixA, matrixB, threads), "Stripe");
                timer(() -> FoxAlgorithm.multiply(matrixA, matrixB, threads), "Fox");

                System.out.println();
            }
            System.out.println("------------------------------------------\n");
        }
    }

    private static void timer(Runnable runnable, String algorithhm) {
        long start = System.currentTimeMillis();
        runnable.run();
        long end = System.currentTimeMillis();
        System.out.println("Time " + algorithhm + ": " + (end - start) + " ms");
    }

}
