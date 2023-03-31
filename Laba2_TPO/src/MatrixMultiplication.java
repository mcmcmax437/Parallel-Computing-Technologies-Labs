import java.util.Scanner;

public class MatrixMultiplication {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Get user input for number of threads and size of matrix
        System.out.print("Enter the number of threads: ");
        int numThreads = sc.nextInt();
        System.out.print("Enter the size of the matrix: ");
        int size = sc.nextInt();

        // Create the matrices
        int[][] matrix1 = new int[size][size];
        int[][] matrix2 = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix1[i][j] = (int) (Math.random() * 10);
                matrix2[i][j] = (int) (Math.random() * 10);
            }
        }

        // Multiply matrices using basic method
        long start = System.currentTimeMillis();
        int[][] result1 = multiplyBasic(matrix1, matrix2);
        long end = System.currentTimeMillis();
        System.out.println("Time taken for basic method: " + (end - start) + " ms");

        // Multiply matrices using Fox algorithm
        start = System.currentTimeMillis();
        int[][] result2 = multiplyFox(matrix1, matrix2, numThreads);
        end = System.currentTimeMillis();
        System.out.println("Time taken for Fox algorithm: " + (end - start) + " ms");

        sc.close();
    }

    // Basic matrix multiplication method
    public static int[][] multiplyBasic(int[][] matrix1, int[][] matrix2) {
        int size = matrix1.length;
        int[][] result = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return result;
    }

    // Fox algorithm matrix multiplication method
    public static int[][] multiplyFox(int[][] matrix1, int[][] matrix2, int numThreads) {
        int size = matrix1.length;
        int[][] result = new int[size][size];
        int blockSize = size / numThreads;

        // Create threads
        Thread[] threads = new Thread[numThreads];
        for (int t = 0; t < numThreads; t++) {
            threads[t] = new Thread(new FoxTask(matrix1, matrix2, result, blockSize, t));
            threads[t].start();
        }

        // Wait for threads to finish
        try {
            for (int t = 0; t < numThreads; t++) {
                threads[t].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Fox algorithm task to be run by each thread
    public static class FoxTask implements Runnable {
        private int[][] matrix1;
        private int[][] matrix2;
        private int[][] result;
        private int blockSize;
        private int threadId;

        public FoxTask(int[][] matrix1, int[][] matrix2, int[][] result, int blockSize, int threadId) {
            this.matrix1 = matrix1;
            this.matrix2 = matrix2;
            this.result = result;
            this.blockSize = blockSize;
            this.threadId = threadId;
        }

        public void run() {
            int size = matrix1.length;
            int[][] temp = new int[size][size];
            int rowStart = threadId * blockSize;
            int rowEnd = rowStart + blockSize;

            for (int r = 0; r < size; r++) {
                for (int i = rowStart; i < rowEnd; i++) {
                    for (int j = 0; j < size; j++) {
                        temp[i][j] += matrix1[i][r] * matrix2[r][j];
                    }
                }
                // Rotate rows of matrix1
                int[] tempRow = matrix1[rowStart];
                for (int i = rowStart; i < rowEnd - 1; i++) {
                    matrix1[i] = matrix1[i + 1];
                }
                matrix1[rowEnd - 1] = tempRow;
            }

            // Combine results
            for (int i = rowStart; i < rowEnd; i++) {
                for (int j = 0; j < size; j++) {
                    result[i][j] += temp[i][j];
                }
            }
        }
    }
}