package Fox;

public class FoxThread extends Thread {
    private final FoxSyncObject syncObject;
    private final int i;
    private final int j;
    private final int blocksGridSize;
    private final int[][] result;
    private final int blockSize;
    FoxThread(int i, int j, int blocksGridSize, FoxSyncObject syncObject, int[][] result) {
        this.syncObject = syncObject;
        this.i = i;
        this.j = j;
        this.blocksGridSize = blocksGridSize;
        this.result = result;
        this.blockSize = result.length / blocksGridSize;
    }

    @Override
    public void run() {
        for (int k = 0; k < blocksGridSize; k++) {
            var blocks = syncObject.getAndBlockBlocksPair((i + k) % blocksGridSize, i, j, (i + k) % blocksGridSize);

            multiplyMatrices(blocks[0], blocks[1]);

            syncObject.unblockBlocksPair((i + k) % blocksGridSize, i, j, (i + k) % blocksGridSize);
        }
    }

    private void multiplyMatrices(int[][] matrixA, int[][] matrixB) {
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                for (int k = 0; k < matrixB.length; k++) {
                    result[this.i * blockSize + i][this.j * blockSize + j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
    }
}
