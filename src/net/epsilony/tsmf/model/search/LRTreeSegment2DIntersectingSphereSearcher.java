/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.search;

import java.util.Iterator;
import java.util.List;
import net.epsilony.tsmf.model.Polygon2D;
import net.epsilony.tsmf.model.LinearSegment2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LRTreeSegment2DIntersectingSphereSearcher implements SphereSearcher<LinearSegment2D> {

    public static final int DEMENSION = 2;
    SegmentsMidPointLRTreeRangeSearcher segmentsRangeSearcher;
    double maxSegmentLength;

    @Override
    public List<LinearSegment2D> searchInSphere(double[] center, double radius) {

        if (radius < 0) {
            throw new IllegalArgumentException("Illegal negative Radius!");
        }
        double[] from = new double[]{center[0] - radius - maxSegmentLength / 2, center[1] - radius - maxSegmentLength / 2};
        double[] to = new double[]{center[0] + radius + maxSegmentLength / 2, center[1] + radius + maxSegmentLength / 2};

        List<LinearSegment2D> segments = segmentsRangeSearcher.rangeSearch(from, to);
        Iterator<LinearSegment2D> segIter = segments.iterator();
        while (segIter.hasNext()) {
            LinearSegment2D seg = segIter.next();
            if (seg.distanceTo(center[0], center[1]) > radius) {
                segIter.remove();
            }
        }
        return segments;
    }

    public LRTreeSegment2DIntersectingSphereSearcher(Polygon2D polygon) {
        segmentsRangeSearcher = new SegmentsMidPointLRTreeRangeSearcher(polygon, DEMENSION);
        maxSegmentLength = polygon.getMaxSegmentLength();
    }
}
