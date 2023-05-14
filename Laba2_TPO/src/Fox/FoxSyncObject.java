package Fox;

import java.util.ArrayList;

public class FoxSyncObject {
    private final int[][][][] listOfBlocksLeftMatrix;
    private final int[][][][] listOfBlocksRightMatrix;
    public ArrayList<int[]> blockedBlocksOfRightMatrix = new ArrayList<>();
    public ArrayList<int[]> blockedBlocksOfLeftMatrix = new ArrayList<>();
    FoxSyncObject(int[][][][] listOfBlocksLeftMatrix, int[][][][] listOfBlocksRightMatrix) {
        this.listOfBlocksLeftMatrix = listOfBlocksLeftMatrix;
        this.listOfBlocksRightMatrix = listOfBlocksRightMatrix;
    }
    public synchronized int[][][] getAndBlockBlocksPair(int leftX, int leftY, int rightX, int rightY) {
        while(isBlocksBlocked(leftX, leftY, rightX, rightY)) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }
        blockedBlocksOfLeftMatrix.add(new int[] {leftX, leftY});
        blockedBlocksOfRightMatrix.add(new int[] {rightX, rightY});
        return new int[][][]{ listOfBlocksLeftMatrix[leftY][leftX], listOfBlocksRightMatrix[rightY][rightX] };
    }

    public synchronized void unblockBlocksPair(int leftX, int leftY, int rightX, int rightY) {
        for (int[] blockIndexes : blockedBlocksOfLeftMatrix) {
            if(blockIndexes[0] == leftX || blockIndexes[1] == leftY) {
                blockedBlocksOfLeftMatrix.remove(blockIndexes);
                break;
            }
        }

        for (int[] blockIndexes: blockedBlocksOfRightMatrix) {
            if(blockIndexes[0] == rightX || blockIndexes[1] == rightY) {
                blockedBlocksOfRightMatrix.remove(blockIndexes);
                break;
            }
        }

        notifyAll();
    }
    public synchronized boolean isBlocksBlocked(int leftX, int leftY, int rightX, int rightY) {
        for (int[] blockIndexes: blockedBlocksOfLeftMatrix) {
            if(blockIndexes[0] == leftX || blockIndexes[1] == leftY) {
                return true;
            }
        }
        for (int[] blockIndexes: blockedBlocksOfRightMatrix) {
            if(blockIndexes[0] == rightX || blockIndexes[1] == rightY) {
                return true;
            }
        }

        return false;
    }
}
