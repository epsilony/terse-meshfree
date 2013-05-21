/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.DoubleArrayComparator;
import net.epsilony.tb.Math2D;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.pair.PairPack;
import net.epsilony.tb.pair.WithPair;
import net.epsilony.tb.rangesearch.LayeredRangeTree;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LRTreeNodesSphereSearcher<T extends Node> implements SphereSearcher<T> {

    public static final int DEFAULT_DIMENSION = 2;
    int dimension = DEFAULT_DIMENSION;
    LayeredRangeTree<double[], T> nodesTree;

    @Override
    public List<T> searchInSphere(double[] center, double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius should be non-negative 0");
        }
        double[] from = new double[]{center[0] - radius, center[1] - radius};
        double[] to = new double[]{center[0] + radius, center[1] + radius};
        List<T> results = nodesTree.rangeSearch(from, to);
        Iterator<T> rsIter = results.iterator();
        while (rsIter.hasNext()) {
            Node nd = rsIter.next();
            if (Math2D.distance(nd.getCoord(), center) >= radius) {
                rsIter.remove();
            }
        }
        return results;
    }

    @Override
    public void setAll(Collection<? extends T> allNodes) {
        LinkedList<WithPair<double[], T>> pairs = new LinkedList<>();
        for (T node : allNodes) {
            pairs.add(new PairPack<>(node.getCoord(), node));
        }
        nodesTree = new LayeredRangeTree<>(pairs, DoubleArrayComparator.comparatorsForAll(dimension));
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this);
    }
}
