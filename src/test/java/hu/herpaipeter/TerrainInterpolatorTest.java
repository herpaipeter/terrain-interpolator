package hu.herpaipeter;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.text.IsEmptyString.emptyString;
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
            assertThat(interpolator.isPowerOfTwo(2), is(true));
            assertThat(interpolator.isPowerOfTwo(4), is(true));
            assertThat(interpolator.isPowerOfTwo(8), is(true));

            assertThat(interpolator.isPowerOfTwo(1), is(false));
            assertThat(interpolator.isPowerOfTwo(7), is(false));
            assertThat(interpolator.isPowerOfTwo(18), is(false));
        }

        public class SquareDiamondCoordinateCalculations {
            @Test
            public void simpleThreeByThree_SquarePass() {
                interpolator.interpolate(dummy, 3);
                assertThat(actions, startsWith(
                        "Square(0,0,3): A([0,0],[2,0],[0,2],[2,2])->[1,1]."));
            }
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
}
