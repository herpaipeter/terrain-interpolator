package hu.herpaipeter;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
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
    }
}
