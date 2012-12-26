/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.ArrayList;
import net.epsilony.tsmf.util.TestTool;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Man YUAN <epsilonyuan@gmail.com>
 */
public class Polygon2DTest {

    public Polygon2DTest() {
    }

    @Test
    public void testDistanceFuncSimp() {
        Polygon2D pg = samplePolygon();
        double[][] testCoords = new double[][]{{0.5, 0.5}, {0.1, 0.1}, {-0.1, -0.1}, {0.5, 0.4}, {0.5, 0.25}};
        double[] exps = new double[]{0, 0.1, -0.1 * Math.sqrt(2), 0.1, 0.25};
        for (int i = 0; i < exps.length; i++) {
            double exp = exps[i];
            double act = pg.distanceFunc(testCoords[i][0], testCoords[i][1]);
            assertEquals(exp, act, 1e-12);
        }
    }

    @Test
    public void testDistanceFuncComp() {
        ArrayList<double[][][]> coords = new ArrayList<>(1);
        Polygon2D pg = TestTool.samplePolygon(coords);
        double[][] testCoordsExps = new double[][]{{
                3, 6.5, Math.sqrt(2) / 4},
            {7, 5.5, -Math.sqrt(5) / 10},
            {-1, -1, -Math.sqrt(2)},
            {1, 2.5, 0},
            {6.25, 6, 0.25},
            {2.9, 3, -0.1}
        };
        for (double[] xy_exp : testCoordsExps) {
            double exp = xy_exp[2];
            double x = xy_exp[0];
            double y = xy_exp[1];
            double act = pg.distanceFunc(x, y);
            assertEquals(exp, act, 1e-12);
        }
    }

    public Polygon2D samplePolygon() {
        double[][][] coordChains = new double[][][]{{{0, 0}, {1, 0}, {1, 1}, {0.5, 0.5}, {0, 1}}};
        return Polygon2D.byCoordChains(coordChains);
    }
}
