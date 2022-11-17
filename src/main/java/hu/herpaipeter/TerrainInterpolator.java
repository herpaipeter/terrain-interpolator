package hu.herpaipeter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TerrainInterpolator {

    double[][] values;
    int size;
    double randomAmplitude;
    double offset;
    Random random = new Random();

    public void interpolate(double[][] values, int size) {
        interpolate(values, size,0,0);

    }
    public void interpolate(double[][] values, int size, double randomAmplitude, double offset) {
        if (1 < size && !isGreaterThanTwoPowerOfTwo(size - 1))
            throw new BadSize();
        this.values = values;
        this.size = size;
        this.randomAmplitude = randomAmplitude;
        this.offset = offset;
        for (int step = (size - 1); 1 < step; step /= 2) {
            for (int i = 0; i < size - 1; i += step) {
                for (int j = 0; j < size - 1; j += step) {
                    doSquare(i, j, step + 1);
                }
            }
            for (int i = 0; i < size - 1; i += step) {
                for (int j = 0; j < size - 1; j += step) {
                    doDiamond(i, j, step + 1);
                }
            }
            this.offset /= 4;
        }
    }

    public static boolean isGreaterThanTwoPowerOfTwo(int num) {
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
        set(xMiddle, yMiddle, average + random() + offset);
    }

    void doDiamond(int x, int y, int size) {
        int middle = (size - 1) / 2;
        doDiamondPart(x + middle, y, middle);
        doDiamondPart(x, y + middle, middle);
        doDiamondPart(x + middle, y + 2 * middle, middle);
        doDiamondPart(x + 2 * middle, y + middle, middle);
    }

    void doDiamondPart(int xMiddle, int yMiddle, int middle) {
        List<Integer> ints = new ArrayList<>();
        int xLeft = xMiddle - middle;
        int xRight = xMiddle + middle;
        int yBottom = yMiddle - middle;
        int yTop = yMiddle + middle;

        if (isValidPair(xLeft, yMiddle, this.size))
            ints.addAll(Arrays.asList(xLeft, yMiddle));
        if (isValidPair(xRight, yMiddle, this.size))
            ints.addAll(Arrays.asList(xRight, yMiddle));
        if (isValidPair(xMiddle, yBottom, this.size))
            ints.addAll(Arrays.asList(xMiddle, yBottom));
        if (isValidPair(xMiddle, yTop, this.size))
            ints.addAll(Arrays.asList(xMiddle, yTop));

        double average = average(ints.toArray(new Integer[ints.size()]));
        set(xMiddle, yMiddle, average + random() + offset);
    }

    static boolean isValidPair(int x, int y, int size) {
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

    double random() {
        return 0 < randomAmplitude ? random.nextDouble(2 * randomAmplitude) - randomAmplitude : 0;
    }

    public static class BadSize extends RuntimeException {
    }
}
