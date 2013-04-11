/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.LinearSegment2D;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.Segment2DUtils;
import net.epsilony.tsmf.util.DoubleArrayComparator;
import net.epsilony.tsmf.util.MiscellaneousUtils;
import net.epsilony.tsmf.util.pair.PairPack;
import net.epsilony.tsmf.util.pair.WithPair;
import net.epsilony.tsmf.util.rangesearch.LayeredRangeTree;
import net.epsilony.tsmf.util.rangesearch.RangeSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SegmentsMidPointLRTreeRangeSearcher implements RangeSearcher<double[], Segment2D> {

    public static final int DEFAULT_DIMENSION = 2;
    LayeredRangeTree<double[], Segment2D> segmentsTree;

    public SegmentsMidPointLRTreeRangeSearcher(Iterable<? extends Segment2D> segments, int dimension) {
        LinkedList<WithPair<double[], Segment2D>> midSegPairs = new LinkedList<>();
        for (Segment2D seg : segments) {
            PairPack<double[], Segment2D> midSegPair = new PairPack<>(Segment2DUtils.chordMidPoint(seg, null), seg);
            midSegPairs.add(midSegPair);
        }
        ArrayList<Comparator<double[]>> comps = new ArrayList<>(2);
        for (int i = 0; i < dimension; i++) {
            comps.add(new DoubleArrayComparator(i));
        }
        segmentsTree = new LayeredRangeTree<>(midSegPairs, comps);
    }

    public SegmentsMidPointLRTreeRangeSearcher(Iterable<? extends LinearSegment2D> segments) {
        this(segments, DEFAULT_DIMENSION);
    }

    @Override
    public List<Segment2D> rangeSearch(double[] from, double[] to) {
        return segmentsTree.rangeSearch(from, to);
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this);
    }
}