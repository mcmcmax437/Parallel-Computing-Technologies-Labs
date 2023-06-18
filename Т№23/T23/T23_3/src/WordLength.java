import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WordLength extends RecursiveTask<Double> {
    private static final int THRESHOLD = 1000;
    private String[] words;
    private int start;
    private int end;

    public WordLength(String[] words, int start, int end) {
        this.words = words;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Double compute(){
        if (end - start <= THRESHOLD) {
            double totalLength = 0;
            for (int i = start; i < end; i++) {
                totalLength += words[i].length();
            }
            return totalLength / (end - start);
        } else {
            // більша за THRESHOLD - розбиваємо навпіл
            int mid = (start + end) / 2;

            WordLength leftTask = new WordLength(words, start, mid);
            WordLength rightTask = new WordLength(words, mid, end);

            leftTask.fork();
            double rightResult = rightTask.compute();

            double leftResult = leftTask.join(); //чеаємо результат лівої частини
            return (leftResult + rightResult) / 2;
        }
    }

    public static void main(String[] args) {
        String filename = "D:\\Labs\\MKR-2\\B23\\T23\\T23_3\\src\\words.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(" ");
            }
            String[] words = sb.toString().trim().split("\\s+");

            ForkJoinPool forkJoinPool = new ForkJoinPool();
            WordLength task = new WordLength(words, 0, words.length);
            double averageLength = forkJoinPool.invoke(task);

            System.out.println("Avarage length: " + averageLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
