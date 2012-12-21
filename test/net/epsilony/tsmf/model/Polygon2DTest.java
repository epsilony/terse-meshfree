/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author epsilon
 */
public class Polygon2DTest {

    public Polygon2DTest() {
    }

    @Test
    public void testDistanceFunc() {
        Polygon2D pg = samplePolygon();
        double[][] testCoords = new double[][]{{0.5, 0.5}, {0.1, 0.1}, {-0.1, -0.1}, {0.5, 0.4},{0.5,0.25}};
        double[] exps = new double[]{0, 0.1, -0.1 * Math.sqrt(2), 0.1,0.25};
        for (int i = 0; i < exps.length; i++) {
            double exp = exps[i];
            double act = pg.distanceFunc(testCoords[i][0], testCoords[i][1]);
            assertEquals(exp, act, 1e-12);
        }
    }

    public Polygon2D samplePolygon() {
        double[][][] coordChains = new double[][][]{{{0, 0}, {1, 0}, {1, 1}, {0.5, 0.5}, {0, 1}}};
        return Polygon2D.byCoordChains(coordChains);
    }
}
