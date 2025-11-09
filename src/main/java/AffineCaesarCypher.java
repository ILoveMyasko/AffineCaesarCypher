

public final class AffineCaesarCypher {

    private final Alphabet alphabet;
    private final CypherKey key;
    public AffineCaesarCypher(Alphabet alphabet, CypherKey key) {

        if (MathUtils.gcd(key.getA(), alphabet.getSize()) != 1) throw new IllegalArgumentException(
                "First key parameter must be a coprime to alphabet size (" + alphabet.getSize() + ")");
        this.alphabet = alphabet;
        this.key = key;
    }

    public String prepareText (String input) {
        StringBuilder cleanTextBuilder = new StringBuilder();
        String lowerCaseInput = input.toLowerCase();
        for (Character charLower: lowerCaseInput.toCharArray()) {
            if (alphabet.contains(charLower)) {
                cleanTextBuilder.append(charLower);
            }
        }
        return cleanTextBuilder.toString();
    }

    public String encrypt(String input) {
        int m = alphabet.getSize();
        int a = key.getA() % m;
        int b = key.getB() % m;
        String cleanText = prepareText(input);
        StringBuilder encryptedTextBuilder = new StringBuilder();

        for (char c : cleanText.toCharArray()) {
            int x = alphabet.indexOf(c);
            int encryptedCharIndex = (a * x + b) % m;
            encryptedTextBuilder.append(alphabet.charAt(encryptedCharIndex));
        }
        return encryptedTextBuilder.toString();
    }

    public String decrypt(String input) {
        StringBuilder decryptedPlainTextBuilder = new StringBuilder();
        int a = key.getA();
        int b = key.getB();
        int m = alphabet.getSize();
        int inverseA = MathUtils.modInverse(a, m);
        //x = a⁻¹ * (y - b) mod m
        for (char c : input.toCharArray()) {
            int y = alphabet.indexOf(c);
            int temp = y - b;
            while (temp < 0) {
                temp += m;
            }
            int decryptedCharIndex = (inverseA * temp) % m;
            decryptedPlainTextBuilder.append(alphabet.charAt(decryptedCharIndex));
        }
        return decryptedPlainTextBuilder.toString();
    }
}
