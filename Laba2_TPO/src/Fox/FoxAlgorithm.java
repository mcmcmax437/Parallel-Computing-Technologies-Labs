package Fox;

import Task1.Result;
import java.util.ArrayList;

public class FoxAlgorithm {
    public static Result multiply(int[][] matrixA, int[][] matrixB, int numOfThreads) {
        if(matrixA.length != matrixA[0].length
            || matrixB.length != matrixB[0].length
            || matrixA.length != matrixB.length) {
                throw new RuntimeException("Matrix must be squared!");
        }
        int size = matrixA.length;
        if(size % numOfThreads != 0) {
            throw new RuntimeException("Block size is invalid for this matrix!");
        }
        int[][] resultMatrix = new int[size][size];
        var threads = new ArrayList<Thread>();
        int gridSize = (int) Math.sqrt(numOfThreads);
        int[][][][] leftBlocks = divideMatrix(matrixA, gridSize);
        int[][][][] rightBlocks = divideMatrix(matrixB, gridSize);
        FoxSyncObject syncObject = new FoxSyncObject(leftBlocks, rightBlocks);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Thread thread = new FoxThread(i, j, gridSize, syncObject, resultMatrix);

                threads.add(thread);
                thread.start();
            }
        }
        for (Thread th : threads) {
            try{
                th.join();
            } catch (InterruptedException ignored) {}
        }
        return new Result(resultMatrix);
    }
    private static int[][][][] divideMatrix(int[][] matrix, int gridSize) {
        int blockSize = matrix.length / gridSize;
        int[][][][] blocks = new int[gridSize][gridSize][blockSize][blockSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                for (int k = 0; k < blockSize; k++) {
                    System.arraycopy(matrix[i * blockSize + k], j * blockSize, blocks[i][j][k], 0, blockSize);
                }
            }
        }
        return blocks;
    }
}
