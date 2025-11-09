
public class CypherKey {

    private int a;
    private int b;
    public CypherKey(int a, int b){
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Keys must be non-negative");
        }
        this.a = a;
        this.b = b;
    }

    public int getA(){
        return a;
    }

    public int getB(){
        return b;
    }

    public void setKeyModulo(int modulo){
        a = a % modulo;
        b = b % modulo;
    }

    public static boolean isKeyValid(int a, int alphabetSize){
        return MathUtils.gcd(a, alphabetSize) == 1;
    }
}
