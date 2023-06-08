package Task1_2;

import com.google.common.base.Stopwatch;
import mpi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        Start(rank, size);
        MPI.Finalize();
    }
    private static void Blocking(int rank, int size, int n, double[] A, double[] B, double[] C) { //blocking
        int rowsPerProcess = n / size;
        if (rank == 0) {
            for (int dest = 1; dest < size; dest++) {
                MPI.COMM_WORLD.Send(A, dest*rowsPerProcess* n, rowsPerProcess* n, MPI.DOUBLE, dest, 0);
            }
        } else {
            MPI.COMM_WORLD.Recv(A, rank *rowsPerProcess* n, rowsPerProcess* n, MPI.DOUBLE, 0, 0);
        }
        for (int source = 0; source < size; source++) {
            if (rank == source) {
                MPI.COMM_WORLD.Send(B, 0, n * n, MPI.DOUBLE, (rank +1)% size, 0);
            } else if (rank == (source+1)% size) {
                MPI.COMM_WORLD.Recv(B, 0, n * n, MPI.DOUBLE, source, 0);
            }
            MPI.COMM_WORLD.Barrier();
        }
        for (int i = rank *rowsPerProcess; i < (rank +1)*rowsPerProcess; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    C[i* n +j] += A[i* n +k] * B[k* n +j];
                }
            }
        }
        if (rank == 0) {
            for (int source = 1; source < size; source++) {
                MPI.COMM_WORLD.Recv(C, source*rowsPerProcess* n, rowsPerProcess* n, MPI.DOUBLE, source, 0);
            }
        } else {
            MPI.COMM_WORLD.Send(C, rank *rowsPerProcess* n, rowsPerProcess* n, MPI.DOUBLE, 0, 0);
        }
    }
    private static void NonBlocking(int rank, int size, int n, double[] A, double[] B, double[] C) { //non blocking
        int rowsPerProcess = n / size;
        Request[] reqs = new Request[5];

        if (rank == 0) {
            for (int dest = 1; dest < size; dest++) {
                reqs[0] = MPI.COMM_WORLD.Isend(A, dest * rowsPerProcess * n, rowsPerProcess * n, MPI.DOUBLE, dest, 0);
            }
        } else {
            MPI.COMM_WORLD.Irecv(A, rank *rowsPerProcess* n, rowsPerProcess* n, MPI.DOUBLE, 0, 0).Wait();
        }
        for (int source = 0; source < size; source++) {
            if (rank == source) {
                MPI.COMM_WORLD.Isend(B, 0, n * n, MPI.DOUBLE, (rank +1)% size, 0);
            } else if (rank == (source+1)% size) {
                MPI.COMM_WORLD.Irecv(B, 0, n * n, MPI.DOUBLE, source, 0).Wait();
            }
        }
        for (int i = rank *rowsPerProcess; i < (rank +1)*rowsPerProcess; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    C[i* n +j] += A[i* n +k] * B[k* n +j];
                }
            }
        }
        if (rank == 0) {
            for (int source = 1; source < size; source++) {
                MPI.COMM_WORLD.Recv(C, source*rowsPerProcess* n, rowsPerProcess* n, MPI.DOUBLE, source, 0);
            }
        } else {
            MPI.COMM_WORLD.Isend(C, rank *rowsPerProcess* n, rowsPerProcess* n, MPI.DOUBLE, 0, 0);
        }
    }
    public static double[] RandomMatrix(int numRows, int numCols, int minValue, int maxValue) {
        double[] matrix = new double[numRows * numCols];
        Random rand = new Random();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                matrix[i * numCols + j] = (double)rand.nextInt(maxValue - minValue + 1) + minValue;
            }
        }
        return matrix;
    }
    public static void Start(int rank, int size)
    {
        if(rank == 0)
            System.out.println("Results for " + size + " processors");
        int experimentAmount = 2;
        List<Integer> massive = new ArrayList();
        massive.add(500);
        massive.add(1000);
        massive.add(2000);

        for(int n: massive) {

            double[] A = RandomMatrix(n, n, 1, 101);
            double[] B = RandomMatrix(n, n, 1, 101);
            double[] C = new double[n*n];


            Stopwatch s = Stopwatch.createStarted();
            for(int i = 0; i < experimentAmount; i++)
            {
                // Blocking(rank, size, n, A, B, C);
                 NonBlocking(rank, size, n, A, B, C);
            }
            MPI.COMM_WORLD.Barrier();
            if(rank == 0)
                System.out.println(s.elapsed().toMillis()/experimentAmount + " : Result for " + n + " elements");
        }
    }
}
