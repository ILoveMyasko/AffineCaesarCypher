import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LanguageAnalyzer {
    private final Set<String> dictionary;
    private final Map<String, SegmentationResult> memoCache;

    private static final int DICTIONARY_WORD_PENALTY = 1;
    private static final int UNKNOWN_CHAR_PENALTY = 100;

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
        memoCache.clear();

        SegmentationResult result = segment(text);
        return String.join(" ", result.words);
    }

    /**
     * Основной рекурсивный метод, выполняющий разбиение текста.
     * @param text Оставшаяся часть текста для разбиения.
     * @return Список слов или null, если разбиение невозможно.
     */
    private SegmentationResult segment(String text) {
        if (text.isEmpty()) {
            return new SegmentationResult(new ArrayList<>(), 0);
        }
        if (memoCache.containsKey(text)) {
            return memoCache.get(text);
        }

        SegmentationResult bestResult = null;
        // Backtracking
        // Идем от самого длинного возможного слова к самому короткому.
        for (int i = 1; i <= text.length(); i++) {
            String prefix = text.substring(0, i);
            if (dictionary.contains(prefix)) {
                String suffix = text.substring(i);
                SegmentationResult suffixResult = segment(suffix);

                bestResult = getBetterResult(bestResult, prefix, suffixResult, DICTIONARY_WORD_PENALTY);
            }
        }

        //Рассчитываем вариант с пропуском одного символа
        String unknownChar = text.substring(0, 1);
        String restOfText = text.substring(1);
        SegmentationResult restResult = segment(restOfText);
        // Сравниваем лучший результат из цикла (если он был) с результатом пропуска символа.
        bestResult = getBetterResult(bestResult, unknownChar, restResult, UNKNOWN_CHAR_PENALTY);

        memoCache.put(text, bestResult);
        return bestResult;
    }

    /**
     * Сравнивает текущий лучший результат с новым кандидатом и возвращает лучший из них.
     * Этот метод помогает избежать дублирования кода.
     *
     * @param currentBest Текущий лучший результат (может быть null).
     * @param newFirstWord Первое слово нового варианта разбиения.
     * @param suffixResult Результат разбиения для остальной части строки.
     * @param wordPenalty Штраф за newFirstWord.
     * @return Новый лучший результат (либо currentBest, либо новый созданный).
     */
    private SegmentationResult getBetterResult(SegmentationResult currentBest,
                                               String newFirstWord,
                                               SegmentationResult suffixResult,
                                               int wordPenalty) {

        int newPenalty = wordPenalty + suffixResult.penalty;

        // Если текущего лучшего результата еще нет, или новый вариант "дешевле",
        // то новый вариант становится лучшим.
        if (currentBest == null || newPenalty < currentBest.penalty) {
            List<String> newWords = new ArrayList<>();
            newWords.add(newFirstWord);
            newWords.addAll(suffixResult.words);
            return new SegmentationResult(newWords, newPenalty);
        }

        // В противном случае, оставляем прежний лучший результат.
        return currentBest;
    }


}
