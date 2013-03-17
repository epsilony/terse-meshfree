/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.LinearSegment2D;
import net.epsilony.tsmf.util.DoubleArrayComparator;
import net.epsilony.tsmf.util.pair.PairPack;
import net.epsilony.tsmf.util.pair.WithPair;
import net.epsilony.tsmf.util.rangesearch.LayeredRangeTree;
import net.epsilony.tsmf.util.rangesearch.RangeSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SegmentsMidPointLRTreeRangeSearcher implements RangeSearcher<double[], LinearSegment2D> {

    public static final int DEFAULT_DIMENSION = 2;
    LayeredRangeTree<double[], LinearSegment2D> segmentsTree;

    public SegmentsMidPointLRTreeRangeSearcher(Iterable<? extends LinearSegment2D> segments, int dimension) {
        LinkedList<WithPair<double[], LinearSegment2D>> midSegPairs = new LinkedList<>();
        for (LinearSegment2D seg : segments) {
            PairPack<double[], LinearSegment2D> midSegPair = new PairPack<>(seg.midPoint(), seg);
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
    public List<LinearSegment2D> rangeSearch(double[] from, double[] to) {
        return segmentsTree.rangeSearch(from, to);
    }
}
