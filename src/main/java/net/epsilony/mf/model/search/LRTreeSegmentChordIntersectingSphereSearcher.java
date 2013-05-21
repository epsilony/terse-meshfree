/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.tb.solid.Segment2DUtils;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LRTreeSegmentChordIntersectingSphereSearcher implements SphereSearcher<Segment2D> {

    public static final int DEMENSION = 2;
    SegmentsMidPointLRTreeRangeSearcher segmentsRangeSearcher;
    double maxSegmentLength;

    @Override
    public List<Segment2D> searchInSphere(double[] center, double radius) {

        if (radius < 0) {
            throw new IllegalArgumentException("Illegal negative Radius!");
        }
        double[] from = new double[]{
            center[0] - radius - maxSegmentLength / 2,
            center[1] - radius - maxSegmentLength / 2};
        double[] to = new double[]{
            center[0] + radius + maxSegmentLength / 2,
            center[1] + radius + maxSegmentLength / 2};

        List<Segment2D> segments = segmentsRangeSearcher.rangeSearch(from, to);
        Iterator<Segment2D> segIter = segments.iterator();
        while (segIter.hasNext()) {
            Segment2D seg = segIter.next();
            if (Segment2DUtils.distanceToChord(seg, center) > radius) {
                segIter.remove();
            }
        }
        return segments;
    }

    @Override
    public void setAll(Collection<? extends Segment2D> datas) {
        segmentsRangeSearcher = new SegmentsMidPointLRTreeRangeSearcher(datas, DEMENSION);
        maxSegmentLength = Segment2DUtils.maxChordLength(datas);
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + '{' + "segment searcher " + segmentsRangeSearcher
                + ", max segment length: " + maxSegmentLength + '}';
    }
}
