import mpi.*;

public class Main {

    public static void main(String[] args) throws MPIException {   //mpi 4
        MPI.Init(args);
        int size = MPI.COMM_WORLD.Size();
        int rank = MPI.COMM_WORLD.Rank();
        int ArrSize = 100; // Приклад: 100 елементів
        double[] array = new double[ArrSize];
        for (int i = 0; i < ArrSize; i++) {
             //array[i] = Math.random();
            array[i]=i+1*2;
        }
        int chunkSize = ArrSize / size;
        double[] localArray = new double[chunkSize];

        MPI.COMM_WORLD.Scatter(array, 0, chunkSize, MPI.DOUBLE, localArray, 0, chunkSize, MPI.DOUBLE, 0);
        // мін в частині
        double localMin = localArray[0];
        for (int i = 1; i < chunkSize; i++) {
            if (localArray[i] < localMin) {
                localMin = localArray[i];
            }
        }
        // Збираємо мін в майстер процес
        double[] allMinValues = new double[size];

        MPI.COMM_WORLD.Gather(new double[]{localMin}, 0, 1, MPI.DOUBLE, allMinValues, 0, 1, MPI.DOUBLE, 0);

        // мін в усій
        double globalMin = allMinValues[0];
        for (int i = 1; i < size; i++) {
            if (allMinValues[i] < globalMin) {
                globalMin = allMinValues[i];
            }
        }

        if (rank == 0) {
            System.out.println("\nMIN: " + globalMin);
            System.out.println("\nMassive: ");
            for (int i = 0; i < ArrSize; i++) {
                System.out.print(array[i] + " ");
            }
        }
        MPI.Finalize();
    }
}
