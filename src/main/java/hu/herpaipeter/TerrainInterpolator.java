package hu.herpaipeter;

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
        int x1 = x;
        int y1 = y;
        int x2 = x + size - 1;
        int y2 = y;
        int x3 = x;
        int y3 = y + size - 1;
        int x4 = x + size - 1;
        int y4 = y + size - 1;
        int xMiddle = x + (size - 1) / 2;
        int yMiddle = y + (size - 1) / 2;
        double average = average(x1, y1, x2, y2, x3, y3, x4, y4);
        set(xMiddle, yMiddle, average);
    }

    void doDiamond(int x, int y, int size) {
        int middle = (size - 1) / 2;
        int xMiddle = x + middle;
        int yMiddle = y + middle;
        doDiamondPart(xMiddle, yMiddle - middle, middle, size);
        doDiamondPart(xMiddle - middle, yMiddle, middle, size);
        doDiamondPart(xMiddle, yMiddle + middle, middle, size);
        doDiamondPart(xMiddle + middle, yMiddle, middle, size);
    }

    void doDiamondPart(int xMiddle, int yMiddle, int middle, int size) {
        List<Integer> ints = new ArrayList<>();
        int x11 = xMiddle - middle;
        int y11 = yMiddle;
        if (isValidPair(x11, y11, size))
            ints.addAll(Arrays.asList(x11, y11));
        int x12 = xMiddle + middle;
        int y12 = yMiddle;
        if (isValidPair(x12, y12, size))
            ints.addAll(Arrays.asList(x12, y12));
        int x13 = xMiddle;
        int y13 = yMiddle - middle;
        if (isValidPair(x13, y13, size))
            ints.addAll(Arrays.asList(x13, y13));
        int x14 = xMiddle;
        int y14 = yMiddle + middle;
        if (isValidPair(x14, y14, size))
            ints.addAll(Arrays.asList(x14, y14));
        double average1 = average(ints.toArray(new Integer[ints.size()]));
        set(xMiddle, yMiddle, average1);
    }

    boolean isValidPair(int x, int y, int size) {
        return 0 <= x && x < size && 0 <= y && y < size;
    }

    double average(Integer... points) {
        return (get(points[0], points[1]) + get(points[2], points[3]) + get(points[4], points[5]) + get(points[6], points[7])) / (points.length / 2.0);
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
