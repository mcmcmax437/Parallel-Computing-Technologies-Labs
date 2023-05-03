import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class AvgWordsLength extends RecursiveTask<Double> {
    private final ArrayList<String> list;
    private final int START;
    private final int END;

    public AvgWordsLength(ArrayList<String> list, int start, int end) {
        this.list = list;
        this.START = start;
        this.END = end;
    }

    @Override
    protected Double compute() {
        if (END - START <= 1) {
            int sum = 0;
            for (int i = START; i < END; i++) {
                sum += list.get(i).length();
            }
            return (double) sum / (END - START);
        } else {
            int mid = START + (END - START) / 2;
            AvgWordsLength left = new AvgWordsLength(list, START, mid);
            AvgWordsLength right = new AvgWordsLength(list, mid, END);
            left.fork();
            double rightAns = right.compute();
            double leftAns = left.join();
            return (leftAns + rightAns) / 2;
        }
    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("TPO");
        list.add("Zelensky");
        list.add("Teleportation");
        list.add("all");
        list.add("ELDEN");
        list.add("Relax");
        list.add("Laptop");

        ForkJoinPool pool = new ForkJoinPool(4);
        double result = pool.invoke(new AvgWordsLength(list, 0, list.size()));
        System.out.println("Average length: " + result);
    }
}
