import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class LanguageAnalyzer {
    private final Set<String> dictionary;

    public LanguageAnalyzer(String dictionaryPath) {
        dictionary = new HashSet<>();
        try {
            dictionary.addAll(Files.readAllLines(Path.of(dictionaryPath)));
            // System.out.println("Dictionary loaded successfully. " + dictionary.size() + " words.");
        } catch (IOException e) {
            System.err.println("Could not load dictionary file from '" + dictionaryPath + "'");
        }
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
}
