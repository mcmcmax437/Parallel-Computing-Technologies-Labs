package Stripe;
import Task1.Result;
import java.util.ArrayList;

public class StripeAlgorithm {
    public static Result multiply(int[][] matrixA, int[][] matrixB, int numOfThreads) {
        int numOfRowsA = matrixA.length;
        int numOfColsA = matrixA[0].length;
        int numOfRowsB = matrixB.length;
        int numOfColsB = matrixB[0].length;
        if (numOfColsA != numOfRowsB) {
            throw new IllegalArgumentException("Matrices cannot be multiplied");
        }
        int[][] result = new int[numOfRowsA][numOfColsB];
        StripeSyncObject syncObject = new StripeSyncObject(matrixB);

        var threads = new StripeThread[numOfThreads];
        int rowsPerThread = Math.max(result.length / numOfThreads, 1);
        int threadIndex = 0;
        ArrayList<int[]> listOfRows = new ArrayList<>();
        ArrayList<Integer> listOfIndexes = new ArrayList<>();
        for (int i = 0; i < numOfRowsA; i++){
            listOfRows.add(matrixA[i]);
            listOfIndexes.add(i);
            if(i % rowsPerThread == rowsPerThread - 1) {
                threads[threadIndex] = new StripeThread(new ArrayList<>(listOfRows), new ArrayList<>(listOfIndexes), syncObject, threadIndex ,result);
                listOfRows.clear();
                listOfIndexes.clear();
                threadIndex++;
            }
        }
        for(var thread : threads) {
            thread.start();
        }
        for(var thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new Result(result);
    }
}
