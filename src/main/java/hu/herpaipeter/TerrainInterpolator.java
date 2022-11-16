package hu.herpaipeter;

public class TerrainInterpolator {
    public void interpolate(double[][] values, int size) {
        if (1 < size && !isPowerOfTwo(size - 1))
            throw new BadSize();
    }

    public boolean isPowerOfTwo(int num) {
        if (num < 2)
            return false;
        while (num % 2 == 0)
            num /= 2;
        return num == 1;
    }

    public static class BadSize extends RuntimeException {
    }
}
