package task1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;

import static task1.Main.Print;

public class Algorithms {
    public static void SyncAlgo(String dirPath) {
        ArrayList<Path> filePaths = new ArrayList<>();
        try {
            Files.walk(Paths.get(dirPath))
                    .filter(Files::isRegularFile)
                    .forEach(filePaths::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<Integer, Integer> result = new HashMap<>();

        for (Path filePath : filePaths) {
            try {
                String content = Files.readString(filePath);
                String[] words = content.split("\\s+");

                for (String word : words) {
                    int length = word.length();
                    result.put(length, result.getOrDefault(length, 0) + 1);
                }
            } catch (IOException e) {
                System.out.println("ERROR: " + e.toString());
                throw new RuntimeException(e);
            }
        }

        Print(result);
    }
    public static void ParallelAlgo(String dirPath) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        DirectorySizeAnalysis task = new DirectorySizeAnalysis(dirPath);

        var result = pool.invoke(task);

        pool.shutdown();
        Print(result);
    }
}
