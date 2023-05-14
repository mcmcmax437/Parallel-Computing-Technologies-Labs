package task1;

import java.util.HashMap;

import static task1.Algorithms.ParallelAlgo;
import static task1.Algorithms.SyncAlgo;
public class Main {
    public static int ITTER = 500_000;
    private static final String PATH = "text/text2.txt";
    public static void main(String[] args) {
        measureTime(() ->  SyncAlgo(PATH),"synchronized");
        measureTime(() ->  ParallelAlgo(PATH),"parallel");
    }
    private static void measureTime(Runnable runnable, String type) {
        long startTime = System.currentTimeMillis();
        runnable.run();
        long endTime = System.currentTimeMillis();
        System.out.println("Time for " + type  +  ": " + (endTime - startTime) + " ms\n");
    }
    public static void Print(HashMap<Integer, Integer> map) {
        int wordsAmount = 0;
        double charsAmount = 0;

        for(int lengthKey : map.keySet()) {
            wordsAmount += map.get(lengthKey);
            charsAmount += map.get(lengthKey) * lengthKey;
        }

        double meadWordsLength = charsAmount / wordsAmount;
        double dispersion = 0;

        for(int lengthKey : map.keySet()) {
            for (int i = 0; i < map.get(lengthKey); i++) {
                dispersion += Math.pow(lengthKey - meadWordsLength, 2);
            }
        }
        dispersion /= wordsAmount;
        System.out.println("All words: " + wordsAmount);
        System.out.println("Mean word: " + Math.round(meadWordsLength * 100.0) / 100.0);
        System.out.println("Word length variance: " + Math.round(dispersion * 100.0) / 100.0);
        System.out.println("Standard deviation: " + Math.round(Math.sqrt(dispersion) * 100.0) / 100.0);

    }
}
