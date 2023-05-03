import mpi.*;
import java.util.Random;

public class ArraySum {

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int[] arr = new int[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(10);
        }
       for (int i = 0; i < arr.length; i++) {
           System.out.print(arr[i]+" ");
       }
        int[] sum = new int[size];

        arr[rank] = rank;

        MPI.COMM_WORLD.Allreduce(arr, 0, sum, 0, size, MPI.INT, MPI.SUM);

        System.out.printf("\nProcess %d: sum = %d\n", rank, sum[0]);

        MPI.Finalize();
    }

 }

