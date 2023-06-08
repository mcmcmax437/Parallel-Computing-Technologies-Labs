package Task1;

import mpi.MPI;
import Additional.Array;

public class Main {
    static final int SIZE = 1500;
    static final int MASTER = 0;

    public static void main(String[] args) {  // один-до-багатьох
        {
            MPI.Init(args);

            int currentProcess = MPI.COMM_WORLD.Rank();
            int processesCount = MPI.COMM_WORLD.Size();

            if(SIZE % processesCount != 0) {
                if(currentProcess == MASTER){
                    System.out.println("It is impossible to allocate for " + processesCount + " processors!");
                }

                MPI.Finalize();
                return;
            }
            int rowsPerProcess = SIZE / processesCount;

            double[][] matrixA = new double[SIZE][SIZE];
            double[] sentMatrixA1D = new double[SIZE * SIZE];
            double[] recvMatrixA1D = new double[SIZE * rowsPerProcess];

            double[][] matrixB = new double[SIZE][SIZE];
            double[] matrixB1D = new double[SIZE * SIZE];

            double[][] matrixC = new double[SIZE][SIZE];
            double[] matrixC1D = new double[SIZE * SIZE];

            long startTime = System.currentTimeMillis();

            if(currentProcess == MASTER){
                Array.arrayFill(matrixA, 1);
                Array.arrayFill(matrixB, 1);

                sentMatrixA1D = Array.convert2DTo1D(matrixA);
                matrixB1D = Array.convert2DTo1D(matrixB);
            }
            MPI.COMM_WORLD.Scatter(sentMatrixA1D, 0, SIZE * rowsPerProcess, MPI.DOUBLE,
                    recvMatrixA1D, 0, SIZE * rowsPerProcess, MPI.DOUBLE, MASTER);
            MPI.COMM_WORLD.Bcast(matrixB1D,0,SIZE * SIZE, MPI.DOUBLE, MASTER);
            double[][] subMatrixA = Array.convert1DTo2D(recvMatrixA1D, rowsPerProcess, SIZE);
            matrixB = Array.convert1DTo2D(matrixB1D, SIZE, SIZE);
            double[][] resultMatrix = new double[rowsPerProcess][SIZE];
            for (int i = 0; i < rowsPerProcess; i++) {
                for (int j = 0; j < SIZE; j++) {
                    for (int k = 0; k < SIZE; k++) {
                        resultMatrix[i][j] += subMatrixA[i][k] * matrixB[k][j];
                    }
                }
            }
            MPI.COMM_WORLD.Gather(Array.convert2DTo1D(resultMatrix), 0, rowsPerProcess * SIZE, MPI.DOUBLE,
                    matrixC1D, 0, rowsPerProcess * SIZE, MPI.DOUBLE, MASTER);
            if(currentProcess == MASTER) {
                matrixC = Array.convert1DTo2D(matrixC1D, SIZE, SIZE);

               /* System.out.println("MatrixA: ");
                Array.printMatrix(matrixA);

                System.out.println("MatrixB: ");
                Array.printMatrix(matrixB);
*/
                System.out.println("MatrixC: ");
           //     Array.printMatrix(matrixC);

                System.out.println("Size: " + (SIZE));
                System.out.println("Execution time of blocking: " + (System.currentTimeMillis() - startTime) + " ms");
            }

            MPI.Finalize();
        }
    }
}