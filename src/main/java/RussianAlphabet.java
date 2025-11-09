
public final class RussianAlphabet implements Alphabet {

    private static final String ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    public int getSize(){
        return ALPHABET.length();
    }

    public int indexOf(Character c){
        return ALPHABET.indexOf(c);
    }

    public char charAt(int index){
        return ALPHABET.charAt(index);
    }

    public boolean contains(Character c){
        return ALPHABET.indexOf(c) != -1;
    }
}
