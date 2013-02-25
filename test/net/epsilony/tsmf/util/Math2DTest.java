/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author epsilon
 */
public class Math2DTest {

    public Math2DTest() {
    }

    /**
     * Test of isSegmentsIntersecting method, of class Math2D.
     */
    @Test
    public void testIsSegmentsIntersecting() {
        double[][][] samples = new double[][][]{
            {{1, 1}, {5, 2}, {2, 1}, {2, 5}},
            {{1, 1}, {5, 2}, {-0.5, 1}, {2, 5}},
            {{1, 1}, {5, 2}, {6.1, 1}, {2, 5}},
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}},
            {{0, 0}, {1, 0}, {0.5, 0}, {3, 0}},
            {{0, 0}, {0, 1}, {0, 2}, {0, 3}},
            {{0, 0}, {0, 1}, {0, 0.5}, {0, 3}},};
        boolean[] exps = new boolean[]{true, false, false, false, true, false, true};
        for (int i = 0; i < exps.length; i++) {
            boolean act = Math2D.isSegmentsIntersecting(samples[i][0], samples[i][1], samples[i][2], samples[i][3]);
            assertEquals(exps[i], act);
        }
    }

    @Test
    public void testIntersectionPointsOfTwoSegments() {
        double[][] inputs = new double[][]{
            {-2, 2}, {3, 3}, {2, 5}, {3, -2}
        };
        double[] exp = new double[]{83 / 36d, 103 / 36d};
        double[] act = Math2D.intersectionPoint(inputs[0], inputs[1], inputs[2], inputs[3], null);
        assertArrayEquals(exp, act, 1e-13);
    }

    @Test
    public void testNormailize() {
        double[] sample = new double[]{-3, 4};
        double[] exp = new double[]{-3 / 5d, 4 / 5d};
        double[] act = Math2D.normalize(sample, null);
        double[] act2 = Math2D.normalize(sample, sample);
        assertArrayEquals(exp, act, 1e-15);
        assertArrayEquals(exp, act2, 1e-15);
    }
}
