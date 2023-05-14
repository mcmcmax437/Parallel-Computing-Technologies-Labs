package task3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WordSearch {

    static class FileWordTask extends RecursiveTask<Set<String>> {
        private File file;
        public FileWordTask(File file) {
            this.file = file;
        }
        @Override
        protected Set<String> compute() {
            Set<String> result = new HashSet<>();
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                for (String line : lines) {
                    String[] tokens = line.split("\\s+");
                    result.addAll(Arrays.asList(tokens));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    static class MultiFileWordTask extends RecursiveTask<Void> {
        private List<File> files;
        public MultiFileWordTask(List<File> files) {
            this.files = files;
        }
        @Override
        protected Void compute() {
            if (files.size() <= 10) {
                List<Set<String>> wordSets = new ArrayList<>();
                for (File file : files) {
                    FileWordTask task = new FileWordTask(file);
                    wordSets.add(task.compute());
                }
                Set<String> commonWords = new HashSet<>(wordSets.get(0));
                for (int i = 1; i < wordSets.size(); i++) {
                    commonWords.retainAll(wordSets.get(i));
                }
                System.out.println("The common words in " + files + " are: " + commonWords);
            } else {
                int middle = files.size() / 2;
                List<File> firstHalf = files.subList(0, middle);
                List<File> secondHalf = files.subList(middle, files.size());
                MultiFileWordTask firstTask = new MultiFileWordTask(firstHalf);
                MultiFileWordTask secondTask = new MultiFileWordTask(secondHalf);
                invokeAll(firstTask, secondTask);
            }
            return null;
        }
    }

    public static void main(String[] args) {
        List<File> files = Arrays.asList(
                new File("text/text.txt"),
                new File("text/LordOfTheRings.txt")
        );
        ForkJoinPool pool = ForkJoinPool.commonPool();
        MultiFileWordTask task = new MultiFileWordTask(files);
        pool.invoke(task);
    }
}