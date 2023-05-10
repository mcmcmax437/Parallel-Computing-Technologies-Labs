package task1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectorySizeAnalysis extends RecursiveTask<HashMap<Integer, Integer>> {
    private final List<String> filePaths;
    public DirectorySizeAnalysis(String dirPath) {
        try (Stream<Path> walk = Files.walk(Paths.get(dirPath))) {
            filePaths = walk.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected HashMap<Integer, Integer> compute() {
        List<FileSizeAnalysis> tasks = new ArrayList<>();

        for(String filePath : filePaths) {
            FileSizeAnalysis task = new FileSizeAnalysis(filePath);
            task.fork();
            tasks.add(task);
        }

        HashMap<Integer, Integer> result = new HashMap<>();

        for(FileSizeAnalysis task : tasks) {
            task.join().forEach((lengthKey, count) ->
                    result.merge(lengthKey, count, Integer::sum)
            );
        }

        return result;
    }
}
