
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    final private static String inputFileName = "input.txt";
    final private static String encryptedFileName = "encrypted.txt";
    final private static String decryptedFileName = "decrypted.txt";
    final private static String dictionaryFileName = "dictionary.txt";
    final private static String bruteForceFileName = "bruteforce.txt";
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Alphabet alphabet = new RussianAlphabet();
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1: {
                    int a = getKeyA(scanner, alphabet);
                    int b = getKeyB(scanner);
                    CypherKey cypherKey = new CypherKey(a, b);
                    AffineCaesarCypher cypher = new AffineCaesarCypher(alphabet, cypherKey);
                    handleEncryption(cypher);
                    break;
                }
                case 2: {
                    int a = getKeyA(scanner, alphabet);
                    int b = getKeyB(scanner);
                    CypherKey cypherKey = new CypherKey(a, b);
                    AffineCaesarCypher cypher = new AffineCaesarCypher(alphabet, cypherKey);
                    handleDecryption(cypher);
                    break;
                }
                case 3:{
                    handleBruteForceAttack(alphabet);
                    break;
                }
                case 4: {
                    System.out.println("Exiting program...");
                    scanner.close();
                    return;
                }
            }
        }
    }

    private static void handleEncryption(AffineCaesarCypher cypher) {
        try {
            //read
            System.out.println("Reading text from '" + inputFileName + "'...");
            String originalText = Files.readString(Paths.get(inputFileName));
            //encrypt and write to file
            System.out.println("Starting encryption...");
            String encryptedText = cypher.encrypt(originalText);
            Files.writeString(Paths.get(encryptedFileName), encryptedText);
            System.out.println("Encryption complete. Result saved to '" + encryptedFileName + "'.");

        } catch (IOException e) {
            System.err.println("\nAn error occurred during file operation!");
            System.err.println("Error details: " + e.getMessage());
        }
    }

    private static void handleDecryption(AffineCaesarCypher cypher) {
        try {
            System.out.println("Reading text from '" + encryptedFileName + "'...");
            String encryptedText = Files.readString(Paths.get(encryptedFileName));
            String decryptedText = cypher.decrypt(encryptedText);
            Files.writeString(Paths.get(decryptedFileName), decryptedText);
            System.out.println("Decrypted text has been saved to '" + decryptedFileName + "'.");
        } catch (IOException e) {
            System.err.println("\nAn error occurred during file operation!");
            System.err.println("Error details: " + e.getMessage());
        }
    }

    private static void handleBruteForceAttack(Alphabet alphabet) {
        System.out.println("Starting Brute-Force Attack...");

        LanguageAnalyzer analyzer = new LanguageAnalyzer(dictionaryFileName);
        try {
            String encryptedText = Files.readString(Paths.get(encryptedFileName));
            String bestGuessText = "";
            CypherKey bestKey = null;
            int maxScore = -1;
            int m = alphabet.getSize();

            for (int a = 1; a < m; a++) {
                if (MathUtils.gcd(a, m) == 1) {
                    for (int b = 0; b < m; b++) {
                        CypherKey tempKey = new CypherKey(a, b);
                        AffineCaesarCypher tempCypher = new AffineCaesarCypher(alphabet, tempKey);
                        String decryptedAttempt = tempCypher.decrypt(encryptedText);

                        int currentScore = analyzer.calculateScore(decryptedAttempt);
                        System.out.println("a = " + tempKey.getA() + "  b = " + tempKey.getB() + "  score = " + currentScore);
                        if (currentScore > maxScore) {
                            maxScore = currentScore;
                            bestGuessText = decryptedAttempt;
                            bestKey = tempKey;
                        }
                    }
                }
            }

            if (bestKey != null) {
                System.out.println("Best Key Found: a=" + bestKey.getA() + ", b=" + bestKey.getB());
                System.out.println("Confidence Score: " + maxScore);
                Files.writeString(Paths.get(bruteForceFileName), bestGuessText);
                System.out.println("Result saved to '" + bruteForceFileName + "'.");
            } else {
                System.out.println("Attack failed. Perhaps the text is too short.");
            }

        } catch (IOException e) {
            System.err.println("Error during brute-force attack! Details: " + e.getMessage());
        }
    }
    private static void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Encrypt");
        System.out.println("2. Decrypt");
        System.out.println("3. Brute Force Attack");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getKeyA(Scanner scanner, Alphabet alphabet) {
        int a;
        while (true) {
            System.out.print("Input key \"a\" (must be coprime to alphabet size: " + alphabet.getSize() + "):");
            a = scanner.nextInt();
            if (a <= 0) {
                System.out.println("Invalid key! Key 'a' must be greater than 0. Please try again.");
                continue;
            }
            if (MathUtils.gcd(a, alphabet.getSize()) == 1) {
                break;
            } else {
                System.out.println("Invalid key! gcd(" + a + ", " + alphabet.getSize() + ") is not 1. Please try again.");
            }
        }
        return a;
    }

    private static int getKeyB(Scanner scanner) {
        int b;
        while (true) {
            System.out.print("Input key 'b': ");
            b = scanner.nextInt();
            if (b >= 0) {
                break;
            } else {
                System.out.println("Invalid key! Key 'b' must be greater than 0. Please try again.");
            }
        }
        return b;
    }
}
