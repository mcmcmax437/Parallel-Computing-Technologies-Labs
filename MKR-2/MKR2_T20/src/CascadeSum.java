import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.Random;

public class CascadeSum extends RecursiveTask<Double> {

    private static final int NUM = 1000;

    private final double[] numbers;
    private final int START;
    private final int END;

    public CascadeSum(double[] numbers, int start, int end) {
        this.numbers = numbers;
        this.START = start;
        this.END = end;
    }

    @Override
    protected Double compute() {
        if (END - START <= NUM) {
            double sum = 0;
            for (int i = START; i < END; i++) {
                sum += numbers[i];
            }
            return sum;
        } else {
            int mid = START + (END - START) / 2;
            CascadeSum left = new CascadeSum(numbers, START, mid);
            CascadeSum right = new CascadeSum(numbers, mid, START);
            left.fork();
            double rightSum = right.compute();
            double leftSum = left.join();
            return leftSum + rightSum;
        }
    }

    public static void main(String[] args) {
        double[] numbers = new double[10000];
        Random rand = new Random();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = rand.nextInt(10);
        }
        ForkJoinPool pool = new ForkJoinPool(10);
        double sum = pool.invoke(new CascadeSum(numbers, 0, numbers.length));
        System.out.println("SUM = " + sum);
    }
}
