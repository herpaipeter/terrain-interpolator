package hu.herpaipeter;

public class TerrainInterpolator {

    double[][] values;

    public void interpolate(double[][] values, int size) {
        if (size <= 1)
            return;
        if (1 < size && !isPowerOfTwo(size - 1))
            throw new BadSize();
        this.values = values;
        doSquare(0,0, size);
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
    }

    double average(Integer... points) {
        return (get(points[0], points[1]) + get(points[2], points[3]) + get(points[4], points[5]) + get(points[6], points[7])) / 4.0;
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
