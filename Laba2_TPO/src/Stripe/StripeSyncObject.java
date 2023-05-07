package Stripe;

import java.util.ArrayList;

public class StripeSyncObject {
    private final ArrayList<int[]> columns;
    private ArrayList<Integer> blockedColumns = new ArrayList<>();

    StripeSyncObject(int[][] matrix) {
        columns = new ArrayList<>();
        for (int j = 0; j < matrix[0].length; j++) {
            int[] column = new int[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                column[i] = matrix[i][j];
            }
            columns.add(column);
        }
    }

    public synchronized int[] getAndBlockColumn(int index) {
        while(blockedColumns.contains(index)) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }

        blockedColumns.add(index);

        return columns.get(index);
    }

    public synchronized void unblockColumn(int index) {
        blockedColumns.remove((Integer) index);

        notifyAll();
    }
}
