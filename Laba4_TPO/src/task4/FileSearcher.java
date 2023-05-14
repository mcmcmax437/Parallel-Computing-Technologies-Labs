package task4;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
public class FileSearcher {
    private static final List<String> WORDS = Arrays.asList("Bob", "GG");
    private static final ForkJoinPool POOL = new ForkJoinPool();
    public static void main(String[] args) {
        String folderPath = "D:\\Labs\\Parallel-Computing-Technologies-Labs\\Laba4_TPO\\text";
        File folder = new File(folderPath);
        POOL.invoke(new FileSearchAction(folder));
    }
    private static class FileSearchAction extends RecursiveAction {
        private final File file;
        public FileSearchAction(File file) {
            this.file = file;
        }
        @Override
        protected void compute() {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        invokeAll(new FileSearchAction(f));
                    }
                }
            } else {
                try {
                    String content = Files.readString(Path.of(file.getAbsolutePath()));
                    boolean containsWord = false;
                    for (String word : WORDS) {
                        if (content.contains(word)) {
                            containsWord = true;
                            break;
                        }
                    }
                    if (containsWord) {
                        System.out.println(file.getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
