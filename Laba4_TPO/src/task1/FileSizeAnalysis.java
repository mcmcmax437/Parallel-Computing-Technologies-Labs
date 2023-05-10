package task1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static task1.Main.ITTER;

public class FileSizeAnalysis extends RecursiveTask<HashMap<Integer, Integer>> {
    public final String filePath;
    private List<String> wordsList;
    private int start;
    private int end;
    private final boolean splitted;

    public FileSizeAnalysis(String filePath) {
        this.filePath = filePath;
        splitted = false;
    }

    public FileSizeAnalysis(String filePath, List<String> wordsList, int start, int end) {
        this.filePath = filePath;
        this.wordsList = wordsList;
        this.start = start;
        this.end = end;
        splitted = true;
    }

    @Override
    protected HashMap<Integer, Integer> compute() {
        if(!splitted) {
            initWordsList();
        }

        if(end - start < ITTER) {
            return getWordsData();
        }

        int middleIdx = (end + start) / 2;

        FileSizeAnalysis leftTask = new FileSizeAnalysis(filePath, wordsList, start, middleIdx);
        leftTask.fork();

        FileSizeAnalysis rightTask = new FileSizeAnalysis(filePath, wordsList, middleIdx, end);

        var result = rightTask.compute();
        leftTask.join().forEach((lengthKey, count) ->
                result.merge(lengthKey, count, Integer::sum)
        );

        return result;
    }

    private HashMap<Integer, Integer> getWordsData() {
        HashMap<Integer, Integer> lengthsMapper = new HashMap<>();

        wordsList.subList(start, end).forEach(word -> {
            int wordLength = word.length();

            if (lengthsMapper.containsKey(wordLength)) {
                lengthsMapper.put(wordLength, lengthsMapper.get(wordLength) + 1);
            } else {
                lengthsMapper.put(wordLength, 1);
            }
        });

        return lengthsMapper;
    }

    private void initWordsList() {
        try {
            String content = Files.readString(Paths.get(filePath));
            wordsList = List.of(content.split("\\s+"));
            start = 0;
            end = wordsList.size();
        } catch (IOException e) {
            System.out.println("ERROR:  " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
