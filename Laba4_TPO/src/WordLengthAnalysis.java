import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordLengthAnalysis {

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();

    public static void main(String[] args) throws IOException {
        File inputFile = new File("text/text.txt");
        List<String> words = readWords(inputFile);
        List<Integer> wordLengths = calculateWordLengths(words);
        double mean = calculateMean(wordLengths);
        double variance = calculateVariance(wordLengths, mean);
        double stdDeviation = calculateStandardDeviation(variance);
        int uniqueWordsCount = countUniqueWords(words);
        System.out.println("Mean word length: " + mean);
        System.out.println("Variance of word length: " + variance);
        System.out.println("Standard deviation of word length: " + stdDeviation);
        System.out.println("Number of unique words: " + uniqueWordsCount);
    }

    private static List<String> readWords(File file) throws IOException {
        try (Stream<String> stream = Files.lines(file.toPath())) {
            return stream.flatMap(line -> Stream.of(line.split("\\s+")))
                    .map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase())
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    private static List<Integer> calculateWordLengths(List<String> words) {
        return FORK_JOIN_POOL.invoke(new WordLengthCalculator(words));
    }

    private static double calculateMean(List<Integer> values) {
        return (double) values.stream().mapToInt(Integer::intValue).sum() / values.size();
    }

    private static double calculateVariance(List<Integer> values, double mean) {
        return values.stream().mapToDouble(value -> Math.pow(value - mean, 2)).sum() / values.size();
    }

    private static double calculateStandardDeviation(double variance) {
        return Math.sqrt(variance);
    }

    private static int countUniqueWords(List<String> words) {
        Set<String> uniqueWords = new HashSet<>(words);
        return uniqueWords.size();
    }

    private static class WordLengthCalculator extends RecursiveTask<List<Integer>> {

        private static final int THRESHOLD = 1000;

        private final List<String> words;

        public WordLengthCalculator(List<String> words) {
            this.words = words;
        }

        @Override
        protected List<Integer> compute() {
            if (words.size() <= THRESHOLD) {
                return words.stream().map(String::length).collect(Collectors.toList());
            }
            int mid = words.size() / 2;
            WordLengthCalculator left = new WordLengthCalculator(words.subList(0, mid));
            WordLengthCalculator right = new WordLengthCalculator(words.subList(mid, words.size()));
            invokeAll(left, right);
            return merge(left.join(), right.join());
        }

        private List<Integer> merge(List<Integer> left, List<Integer> right) {
            List<Integer> result = new ArrayList<>(left.size() + right.size());
            int i = 0;
            int j = 0;
            while (i < left.size() && j < right.size()) {
                if (left.get(i) <= right.get(j)) {
                    result.add(left.get(i));
                    i++;
                } else {
                    result.add(right.get(j));
                    j++;
                }
            }
            while (i < left.size()) {
                result.add(left.get(i));
                i++;
            }
            while (j < right.size()) {
                result.add(right.get(j));
                j++;
            }
            return result;
        }
    }
}