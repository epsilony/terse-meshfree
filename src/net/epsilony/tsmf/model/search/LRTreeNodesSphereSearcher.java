/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.util.DoubleArrayComparator;
import net.epsilony.tsmf.util.Math2D;
import net.epsilony.tsmf.util.rangesearch.LayeredRangeTree;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LRTreeNodesSphereSearcher implements SphereSearcher<Node> {

    public static final int DEFAULT_DIMENSION = 2;
    LayeredRangeTree<double[], Node> nodesTree;

    public LRTreeNodesSphereSearcher(Collection<? extends Node> allNodes, int dimension) {
        nodesTree = new LayeredRangeTree<>(allNodes, DoubleArrayComparator.comparatorsForAll(dimension));
    }

    public LRTreeNodesSphereSearcher(Collection<? extends Node> allNodes) {
        this(allNodes, DEFAULT_DIMENSION);
    }

    @Override
    public List<Node> searchInSphere(double[] center, double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius should be non-negative 0");
        }
        double[] from = new double[]{center[0] - radius, center[1] - radius};
        double[] to = new double[]{center[0] + radius, center[1] + radius};
        LinkedList<Node> results = nodesTree.rangeSearch(from, to);
        Iterator<Node> rsIter = results.iterator();
        while (rsIter.hasNext()) {
            Node nd = rsIter.next();
            if (Math2D.distance(nd.coord, center) >= radius) {
                rsIter.remove();
            }
        }
        return results;
    }
}
