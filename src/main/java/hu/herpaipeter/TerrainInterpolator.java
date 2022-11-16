package hu.herpaipeter;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerrainInterpolator {

    double[][] values;

    public void interpolate(double[][] values, int size) {
        if (size <= 1)
            return;
        if (1 < size && !isPowerOfTwo(size - 1))
            throw new BadSize();
        this.values = values;
        doSquare(0,0, size);
        doDiamond(0, 0, size);
    }

    public boolean isPowerOfTwo(int num) {
        if (num < 2)
            return false;
        while (num % 2 == 0)
            num /= 2;
        return num == 1;
    }

    void doSquare(int x, int y, int size) {
        int xLeft = x;
        int xRight = x + size - 1;
        int yBottom = y;
        int yTop = y + size - 1;
        double average = average(xLeft, yBottom, xRight, yBottom, xLeft, yTop, xRight, yTop);

        int xMiddle = x + (size - 1) / 2;
        int yMiddle = y + (size - 1) / 2;
        set(xMiddle, yMiddle, average);
    }

    void doDiamond(int x, int y, int size) {
        int middle = (size - 1) / 2;
        doDiamondPart(x + middle, y, middle, size);
        doDiamondPart(x, y + middle, middle, size);
        doDiamondPart(x + middle, y + 2 * middle, middle, size);
        doDiamondPart(x + 2 * middle, y + middle, middle, size);
    }

    void doDiamondPart(int xMiddle, int yMiddle, int middle, int size) {
        List<Integer> ints = new ArrayList<>();
        int xLeft = xMiddle - middle;
        int xRight = xMiddle + middle;
        int yBottom = yMiddle - middle;
        int yTop = yMiddle + middle;

        if (isValidPair(xLeft, yMiddle, size))
            ints.addAll(Arrays.asList(xLeft, yMiddle));
        if (isValidPair(xRight, yMiddle, size))
            ints.addAll(Arrays.asList(xRight, yMiddle));
        if (isValidPair(xMiddle, yBottom, size))
            ints.addAll(Arrays.asList(xMiddle, yBottom));
        if (isValidPair(xMiddle, yTop, size))
            ints.addAll(Arrays.asList(xMiddle, yTop));

        double average1 = average(ints.toArray(new Integer[ints.size()]));
        set(xMiddle, yMiddle, average1);
    }

    boolean isValidPair(int x, int y, int size) {
        return 0 <= x && x < size && 0 <= y && y < size;
    }

    double average(Integer... points) {
        double sum = 0.0;
        for (int i = 0; i < points.length; i+=2) {
            sum += get(points[i], points[i + 1]);
        }
        return sum / (points.length / 2.0);
    }

    void set(int x, int y, double value) {
        this.values[x][y] = value;
    }
    double get(int x, int y) {
        return this.values[x][y];
    }


    public static class BadSize extends RuntimeException {
    }
}
