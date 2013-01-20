/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import net.epsilony.tsmf.util.TestTool;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
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

    @Test
    public void testIterable() {
        ArrayList<double[][][]> coords = new ArrayList<>(1);
        Polygon2D pg = TestTool.samplePolygon(coords);
        int i = 0, j = 0;
        for (Segment2D seg : pg) {
            double[][][] coordChains = coords.get(0);
            double[] coord = coordChains[i][j];
            assertArrayEquals(coord, seg.getHead().coord, 1e-14);
            j++;
            if (j >= coordChains[i].length) {
                i++;
                j = 0;
            }
        }
    }

    @Test
    public void testPolygonSegmentSearch() {
        ArrayList<double[][][]> coords = new ArrayList<>(1);
        Polygon2D pg = TestTool.samplePolygon(coords);
        int testTime = 1000;
        double radiusMin = 0.3;
        double radiusRange = 3;
        double centerMargin = 1;

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Segment2D seg : pg) {
            Node nd = seg.getHead();
            double[] coord = nd.coord;
            double x = coord[0];
            double y = coord[1];
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        Random rand = new Random();
        double[] centerFrom = new double[]{minX - centerMargin, minY - centerMargin};
        double[] centerRange = new double[]{
            maxX - minX + 2 * centerMargin,
            maxY - minY + 2 * centerMargin};
        for (int i = 0; i < testTime; i++) {

            double[] center = new double[]{
                rand.nextDouble() * centerRange[0] + centerFrom[0],
                rand.nextDouble() * centerRange[1] + centerFrom[1]};
            double radius = rand.nextDouble() * radiusRange + radiusMin;

            List<Segment2D> acts = pg.segmentsIntersectingDisc(center, radius, null);
            LinkedList<Segment2D> exps = new LinkedList<>();
            for (Segment2D seg : pg) {
                if (seg.distanceTo(center[0], center[1]) <= radius) {
                    exps.add(seg);
                }
            }
            try {
                assertEquals(exps.size(), acts.size());
                for (Segment2D seg : acts) {
                    assertTrue(exps.contains(seg));
                }
            } catch (Throwable e) {
                acts = pg.segmentsIntersectingDisc(center, radius, null);
                throw e;
            }
        }
    }

    @Test
    public void testPolygonSegmentPredLink() {
        Polygon2D pg = TestTool.samplePolygon(null);
        ArrayList<LinkedList<Node>> vertes = pg.getVertes();
        Iterator<LinkedList<Node>> vIter = vertes.iterator();
        for (Segment2D cHead : pg.chainsHeads) {
            Segment2D seg = cHead;
            LinkedList<Node> cs = vIter.next();
            ListIterator<Node> csIter = cs.listIterator(cs.size());
            boolean getHere = false;
            do {
                Node actNd = seg.pred.getHead();
                Node expNd = csIter.previous();
                assertArrayEquals(expNd.coord, actNd.coord, 1e-14);
                seg = (Segment2D) seg.pred;
                getHere = true;
            } while (seg != cHead);
            assertTrue(getHere);
        }
    }

    @Test
    public void testFractionize() {
        double[][][] coordChains = new double[][][]{
            {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}},
            {{-0.5, -0.5}, {-0.5, 0.5}, {0.5, 0.5}, {0.5, -0.5}}
        };

        Polygon2D pg = Polygon2D.byCoordChains(coordChains);
        Polygon2D fPg = pg.fractionize(0.67);
        ArrayList<LinkedList<Node>> pVertes = fPg.getVertes();
        double[][][] exps = new double[][][]{
            {{-1, -1}, {-0.5, -1}, {0, -1}, {0.5, -1},
                {1, -1}, {1, -0.5}, {1, 0}, {1, 0.5},
                {1, 1}, {0.5, 1}, {0, 1}, {-0.5, 1},
                {-1, 1}, {-1, 0.5}, {-1, 0}, {-1, -0.5},},
            {{-0.5, -0.5}, {-0.5, 0},
                {-0.5, 0.5}, {0, 0.5},
                {0.5, 0.5}, {0.5, 0},
                {0.5, -0.5}, {0, -0.5}}
        };
        int i = 0;
        for (LinkedList<Node> cs : pVertes) {
            int j = 0;
            for (Node nd : cs) {
                double[] act = nd.coord;
                double[] exp = exps[i][j];
                assertArrayEquals(exp, act, 1e-12);
                j++;
            }
            i++;
        }
    }

    public Polygon2D samplePolygon() {
        double[][][] coordChains = new double[][][]{{{0, 0}, {1, 0}, {1, 1}, {0.5, 0.5}, {0, 1}}};
        return Polygon2D.byCoordChains(coordChains);
    }
}
