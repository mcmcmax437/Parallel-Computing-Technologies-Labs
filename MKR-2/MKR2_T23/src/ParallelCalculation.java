import mpi.*;

public class ParallelCalculation {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int n = 100;
        int[] A = new int[n*n];
        int[] B = new int[n*n];
        int[] C = new int[n];
        for (int i = 0; i < n*n; i++) {
            //A[i] = (int) (Math.random() * 10);
           // B[i] = (int) (Math.random() * 10);
            A[i] = 1;
            B[i] = 1;
        }
        int[] localA = new int[n/size * n];
        int[] localB = new int[n/size * n];
        MPI.COMM_WORLD.Scatter(A, 0, n/size * n, MPI.INT, localA, 0, n/size * n, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(B, 0, n/size * n, MPI.INT, localB, 0, n/size * n, MPI.INT, 0);
        int[] localC = new int[n/size];
        for (int i = 0; i < n/size; i++) {
            int rowSumA = 0;
            int rowSumB = 0;
            for (int j = 0; j < n; j++) {
                rowSumA += localA[i*n+j];
                rowSumB += localB[i*n+j];
            }
            localC[i] = rowSumA * rowSumB;
        }
        MPI.COMM_WORLD.Gather(localC, 0, n/size, MPI.INT, C, 0, n/size, MPI.INT, 0);
        if (rank == 0) {
            for (int i = 0; i < n; i++) {
                System.out.println(C[i]);
            }
        }
        MPI.Finalize();
    }
}
