package hu.herpaipeter;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.CheckedOutputStream;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class TerrainInterpolatorTest {
    private String actions = "";
    private double[][] dummy;
    private TerrainInterpolator interpolator;

    public class SimpleValidations {
        @Before
        public void setUp() throws Exception {
            dummy = new double[1][1];
            interpolator = new TerrainInterpolatorSpy();
        }

        @Test
        public void terminalCondition_sizeOne() throws Exception {
            interpolator.interpolate(dummy, 1);
            assertThat(actions, emptyString());
        }

        @Test(expected = TerrainInterpolator.BadSize.class)
        public void sizeMustBePowerOfTwoPlus1() throws Exception {
            interpolator.interpolate(dummy, 2);
        }

        @Test
        public void Check_isPowerOfTwo() throws Exception {
            assertThat(TerrainInterpolator.isGreaterThanTwoPowerOfTwo(2), is(true));
            assertThat(TerrainInterpolator.isGreaterThanTwoPowerOfTwo(4), is(true));
            assertThat(TerrainInterpolator.isGreaterThanTwoPowerOfTwo(8), is(true));

            assertThat(TerrainInterpolator.isGreaterThanTwoPowerOfTwo(1), is(false));
            assertThat(TerrainInterpolator.isGreaterThanTwoPowerOfTwo(7), is(false));
            assertThat(TerrainInterpolator.isGreaterThanTwoPowerOfTwo(18), is(false));
        }

        public class SquareDiamondCoordinateCalculations {
            @Test
            public void simpleThreeByThree_SquarePass() {
                interpolator.interpolate(dummy, 3);
                assertThat(actions, startsWith(
                        "Square(0,0,3): A([0,0],[2,0],[0,2],[2,2])->[1,1]."));
            }

            @Test
            public void simpleThreeByThree_DiamondPass() {
                interpolator.interpolate(dummy, 3);
                assertThat(actions, endsWith("Diamond(0,0,3): "+
                        "A([0,0],[2,0],[1,1])->[1,0]. " +
                        "A([1,1],[0,0],[0,2])->[0,1]. " +
                        "A([0,2],[2,2],[1,1])->[1,2]. " +
                        "A([1,1],[2,0],[2,2])->[2,1]. "));
            }

            @Test
            public void DiamondSquare_FirstPass() throws Exception {
                interpolator.interpolate(dummy, 5);
                assertThat(actions, startsWith(
                        "Square(0,0,5): A([0,0],[4,0],[0,4],[4,4])->[2,2]. "+
                                "Diamond(0,0,5): " +
                                "A([0,0],[4,0],[2,2])->[2,0]. " +
                                "A([2,2],[0,0],[0,4])->[0,2]. " +
                                "A([0,4],[4,4],[2,2])->[2,4]. " +
                                "A([2,2],[4,0],[4,4])->[4,2]. "));
            }
        }
    }

    public class SquareDiamondRepetition {
        @Before
        public void setup() {
            dummy = new double[1][1];
            interpolator =
                    new TerrainInterpolatorDiamondSquareSpy();
        }

        @Test
        public void FiveByFive() throws Exception {
            interpolator.interpolate(dummy, 5);
            assertThat(actions, is(
                    "" +
                            "Square(0,0,5) Diamond(0,0,5) " +
                            "Square(0,0,3) Square(0,2,3) Square(2,0,3) Square(2,2,3) " +
                            "Diamond(0,0,3) Diamond(0,2,3) Diamond(2,0,3) Diamond(2,2,3) "
            ));
        }
    }

    public class Averages {
        @Before
        public void setup() {
            dummy = new double[3][3];
            interpolator = new TerrainInterpolator();
        }

        @Test
        public void zero() throws Exception {
            interpolator.interpolate(dummy, 3);
            assertThat(dummy, is(new double[][]
                    {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}));
        }

        @Test
        public void allOnes() throws Exception {
            dummy[0][0] = dummy[2][0] =
                    dummy[0][2] = dummy[2][2] = 1;
            interpolator.interpolate(dummy, 3);
            assertThat(dummy, is(new double[][]{
                    {1, 1, 1},
                    {1, 1, 1},
                    {1, 1, 1}}));
        }

        @Test
        public void ramp() throws Exception {
            dummy[0][0] = 0;
            dummy[2][0] = 12;
            dummy[0][2] = 12;
            dummy[2][2] = 24;
            interpolator.interpolate(dummy,3);
            assertThat(dummy, is(new double[][]{
                    {0, 8, 12},
                    {8, 12, 16},
                    {12, 16, 24}}));
        }
    }

    public class RandomsAndOffsets {
        @Before
        public void setup() {
            dummy = new double[5][5];
            interpolator =
                    new TerrainInterpolatorWithFixedRandom();
        }

        @Test
        public void volcano() throws Exception {
            interpolator.interpolate(dummy, 5, 2,4);
            assertThat(dummy, is(new double[][]{
                    {0,8.5,8,8.5,0},
                    {8.5,8.5,10.75,8.5,8.5},
                    {8,10.75,6,10.75,8},
                    {8.5,8.5,10.75,8.5,8.5},
                    {0,8.5,8,8.5,0}
            }));
        }
    }

    public class LargeTerrain {
        @Test
        public void big_one() throws Exception {
            dummy = new double[1025][1025];
            dummy[0][0] = -20;
            dummy[0][1024] = 20;
            dummy[1024][0] = 20;
            dummy[1024][1024] = 50;
            interpolator = new TerrainInterpolator();
            interpolator.interpolate(dummy, 1025, 5, 0);
            writeImage(dummy);
        }
    }


    public void writeImage(double[][] values) {
        String path = "output.png";
        BufferedImage image = new BufferedImage(values.length, values[0].length, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < values.length; x++) {
            for (int y = 0; y < values[x].length; y++) {
                image.setRGB(x, y, (int) (5 * values[x][y]));
            }
        }

        File ImageFile = new File(path);
        try {
            ImageIO.write(image, "png", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TerrainInterpolatorSpy extends TerrainInterpolator {
        @Override
        void doSquare(int x, int y, int size) {
            actions += String.format("Square(%d,%d,%d): ", x, y, size);
            super.doSquare(x, y, size);
        }

        @Override
        void doDiamond(int x, int y, int size) {
            actions += String.format(
                    "Diamond(%d,%d,%d): ", x, y, size);
            super.doDiamond(x, y, size);
        }

        void set(int x, int y, double value) {
            actions += String.format("->[%d,%d]. ", x, y);
        }

        double get(int x, int y) {
            return -1;
        }

        double average(Integer... points) {
            actions += "A(";
            for (int i = 0; i < points.length; i += 2)
                actions += String.format(
                        "[%d,%d],", points[i], points[i + 1]);
            actions = actions.substring(0, actions.length() - 1) + ")";
            return 0;
        }
    }

    private class TerrainInterpolatorDiamondSquareSpy extends TerrainInterpolator {
        void doSquare(int x, int y, int size) {
            actions += String.format("Square(%d,%d,%d) ",x,y,size);
        }

        void doDiamond(int x, int y, int size) {
            actions += String.format("Diamond(%d,%d,%d) ",x,y,size);

        }
    }

    private class TerrainInterpolatorWithFixedRandom extends TerrainInterpolator {

//        void set(int x, int y, double value) {
//            super.set(x, y, value);
//            actions += String.format("->[%d,%d,(%f)]. ", x, y, value);
//        }

        double random() {
            return randomAmplitude;
        }
    }
}
