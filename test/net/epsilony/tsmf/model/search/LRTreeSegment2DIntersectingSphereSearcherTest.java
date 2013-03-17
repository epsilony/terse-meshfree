/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Polygon2D;
import net.epsilony.tsmf.model.LinearSegment2D;
import net.epsilony.tsmf.util.TestTool;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LRTreeSegment2DIntersectingSphereSearcherTest {

    public LRTreeSegment2DIntersectingSphereSearcherTest() {
    }

    /**
     * Test of searchInSphere method, of class
     * LRTreeSegment2DIntersectingSphereSearcher.
     */
    @Test
    public void testSearchInSphere() {
        ArrayList<double[][][]> coords = new ArrayList<>(1);
        Polygon2D pg = TestTool.samplePolygon(coords);
        LRTreeSegment2DIntersectingSphereSearcher polygonSearcher = new LRTreeSegment2DIntersectingSphereSearcher(pg);
        int testTime = 1000;
        double radiusMin = 0.3;
        double radiusRange = 3;
        double centerMargin = 1;

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (LinearSegment2D seg : pg) {
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

            List<LinearSegment2D> acts = polygonSearcher.searchInSphere(center, radius);
            LinkedList<LinearSegment2D> exps = new LinkedList<>();
            for (LinearSegment2D seg : pg) {
                if (seg.distanceTo(center[0], center[1]) <= radius) {
                    exps.add(seg);
                }
            }
            try {
                assertEquals(exps.size(), acts.size());
                for (LinearSegment2D seg : acts) {
                    assertTrue(exps.contains(seg));
                }
            } catch (Throwable e) {
                polygonSearcher.searchInSphere(center, radius);
                throw e;
            }
        }
    }
}
