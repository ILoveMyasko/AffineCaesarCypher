import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LanguageAnalyzer {
    private final Set<String> dictionary;
    private final Map<String, List<String>> memoCache;

    public LanguageAnalyzer(String dictionaryPath) {
        dictionary = new HashSet<>();
        try {
            dictionary.addAll(Files.readAllLines(Path.of(dictionaryPath)));
            // System.out.println("Dictionary loaded successfully. " + dictionary.size() + " words.");
        } catch (IOException e) {
            System.err.println("Could not load dictionary file from '" + dictionaryPath + "'");
        }

        this.memoCache = new HashMap<>();
    }

    public int calculateScore(String text) {
        if (dictionary.isEmpty()) {
            return 0;
        }

        int textLength = text.length();
        int score = 0;
        System.out.print("Found words: ");
        for (int i = 0; i < textLength;) {
            boolean wordFound = false;
            for (int j = Math.min(i+20,textLength); j>= i + 4; j--) { // Ищем слова длиной от 3 символов
                String sub = text.substring(i, j);
                if (dictionary.contains(sub)) {
                    score+= sub.length() * sub.length();
                    System.out.print(sub + " ");
                    i = j;
                    wordFound = true;
                    break;
                }
            }
            if (!wordFound) {
                i++;
            }
        }
        System.out.println();
        return score;
    }

    /**
     * Публичный метод для расстановки пробелов в тексте.
     * @param text Сплошной текст без пробелов.
     * @return Текст с расставленными пробелами или исходный текст, если разбить не удалось.
     */
    public String addSpaces(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // Очищаем кэш перед каждым новым вызовом, если объект используется многократно
        memoCache.clear();

        List<String> words = segment(text);

        if (words != null) {
            return String.join(" ", words);
        } else {
            System.out.println("Couldn't manage to add spaces");
            return text;
        }
    }

    /**
     * Основной рекурсивный метод, выполняющий разбиение текста.
     * @param text Оставшаяся часть текста для разбиения.
     * @return Список слов или null, если разбиение невозможно.
     */
    private List<String> segment(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>(); // Возвращаем пустой список как маркер успеха.
        }

        // Перед выполнением дорогостоящих вычислений проверяем, не решали ли мы уже эту подзадачу.
        if (memoCache.containsKey(text)) {
            return memoCache.get(text);
        }

        // Backtracking
        // Идем от самого длинного возможного слова к самому короткому.
        for (int i = text.length(); i >= 1; i--) {
            String prefix = text.substring(0, i);
            if (dictionary.contains(prefix)) {
                String suffix = text.substring(i);
                List<String> suffixResult = segment(suffix);
                if (suffixResult != null) {
                    List<String> solution = new ArrayList<>();
                    solution.add(prefix);
                    solution.addAll(suffixResult);
                    memoCache.put(text, solution);
                    return solution;
                }
            }
        }

        String unknownChar = text.substring(0, 1);
        String restOfText = text.substring(1);

        List<String> restResult = segment(restOfText);

        List<String> solution = new ArrayList<>();
        solution.add(unknownChar); // Добавляем наш "неизвестный" символ
        solution.addAll(restResult);

        memoCache.put(text, solution);
        return solution;
    }



}
