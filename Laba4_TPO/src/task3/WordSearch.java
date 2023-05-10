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

    // A task that finds all the words in a file and returns a set of words
    static class FileWordTask extends RecursiveTask<Set<String>> {
        private File file; // The file to search

        public FileWordTask(File file) {
            this.file = file;
        }

        @Override
        protected Set<String> compute() {
            Set<String> result = new HashSet<>(); // The set to return
            try {
                // Read all lines from the file
                List<String> lines = Files.readAllLines(file.toPath());
                // For each line, split it into words and add them to the set
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

    // A task that finds the common words in multiple files and prints them to the console
    static class MultiFileWordTask extends RecursiveTask<Void> {
        private List<File> files; // The files to search

        public MultiFileWordTask(List<File> files) {
            this.files = files;
        }

        @Override
        protected Void compute() {
            // If the list size is less than or equal to 10, execute sequentially
            if (files.size() <= 10) {
                // Create a list of sets of words from each file
                List<Set<String>> wordSets = new ArrayList<>();
                for (File file : files) {
                    FileWordTask task = new FileWordTask(file);
                    wordSets.add(task.compute());
                }
                // Find the intersection of all sets of words
                Set<String> commonWords = new HashSet<>(wordSets.get(0));
                for (int i = 1; i < wordSets.size(); i++) {
                    commonWords.retainAll(wordSets.get(i));
                }
                // Print the common words to the console
                System.out.println("The common words in " + files + " are: " + commonWords);
            } else {
                // Otherwise, split the list into two sublists and fork two subtasks
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
        // Create a list of files to search
        List<File> files = Arrays.asList(
                new File("text/text.txt"),
                new File("text/LordOfTheRings.txt")
        );
        // Create a fork/join pool and execute the task
        ForkJoinPool pool = ForkJoinPool.commonPool();
        MultiFileWordTask task = new MultiFileWordTask(files);
        pool.invoke(task);
    }
}