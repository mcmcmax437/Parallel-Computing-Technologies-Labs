import mpi.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int root = 0;

        if (rank == root) {
            String[] A = new String[]{"shrink", "cultural", "healthy", "elite", "follow", "adult", "costume", "wake", "critical", "mechanism", "term", "car", "respectable", "addicted", "pause"};

            int fragmentSize = A.length / size;

            for (int dest = 1; dest < size; dest++) {
                int offset = dest * fragmentSize;
                int count = (dest == size - 1) ? A.length - offset : fragmentSize;
                MPI.COMM_WORLD.Send(A, offset, count, MPI.OBJECT, dest, 0);
            }

            Arrays.sort(A, 0, fragmentSize);

            String[] sortedValues = new String[size];
            sortedValues[0] = A[0];
            for (int source = 1; source < size; source++) {
                MPI.COMM_WORLD.Recv(sortedValues, source, 1, MPI.OBJECT, source, 0);
            }

            System.out.println(Arrays.toString(sortedValues));
        } else {
            String[] fragment = new String[MPI.COMM_WORLD.Probe(root, 0).Get_count(MPI.OBJECT)];
            MPI.COMM_WORLD.Recv(fragment, 0, fragment.length, MPI.OBJECT, root, 0);

            Arrays.sort(fragment);

            MPI.COMM_WORLD.Send(fragment, 0, 1, MPI.OBJECT, root, 0);
        }

        MPI.Finalize();
    }
}
