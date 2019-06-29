package mypackage.mul;

public class Mul {
    public int mul(int i, int j) {
        if (j == 0) throw new IllegalArgumentException();
        return i * j;

    }
}
