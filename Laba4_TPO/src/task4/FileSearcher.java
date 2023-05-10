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

    // Задаємо слова, які шукаємо
    private static final List<String> WORDS = Arrays.asList("Bob", "GG");

    // Створюємо пул потоків
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public static void main(String[] args) {
        // Задаємо шлях до папки
        String folderPath = "D:\\Labs\\Parallel-Computing-Technologies-Labs\\Laba4_TPO\\text";
        // Створюємо об'єкт File для папки
        File folder = new File(folderPath);
        // Викликаємо рекурсивну дію для пошуку файлів
        POOL.invoke(new FileSearchAction(folder));
    }

    // Внутрішній клас, який наслідує RecursiveAction
    private static class FileSearchAction extends RecursiveAction {

        // Поле для збереження файлу або папки
        private final File file;

        // Конструктор, який приймає файл або папку
        public FileSearchAction(File file) {
            this.file = file;
        }

        @Override
        protected void compute() {
            // Якщо файл є папкою, то розбиваємо задачу на підзадачі для кожного файлу в папці
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        invokeAll(new FileSearchAction(f));
                    }
                }
            } else {
                // Якщо файл не є папкою, то перевіряємо чи він містить задані слова
                try {
                    // Зчитуємо вміст файлу у рядок
                    String content = Files.readString(Path.of(file.getAbsolutePath()));
                    // Перевіряємо чи рядок містить хоча б одне з заданих слів
                    boolean containsWord = false;
                    for (String word : WORDS) {
                        if (content.contains(word)) {
                            containsWord = true;
                            break;
                        }
                    }
                    // Якщо рядок містить хоча б одне з заданих слів, то виводимо назву файлу в консоль
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
