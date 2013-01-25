/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.util.DoubleArrayComparator;
import net.epsilony.tsmf.util.pair.PairPack;
import net.epsilony.tsmf.util.pair.WithPair;
import net.epsilony.tsmf.util.rangesearch.LayeredRangeTree;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Polygon2D implements Iterable<Segment2D> {

    public static final int DIM = 2;
    ArrayList<Segment2D> chainsHeads;
    LayeredRangeTree<double[], Segment2D> lrTree;
    double maxSegLen;
    double minSegLen;

    public double getMinSegmentLength() {
        return minSegLen;
    }

    public Polygon2D(List<? extends List<? extends Node>> nodeChains) {
        if (nodeChains.isEmpty()) {
            throw new IllegalArgumentException("There is at least 1 chain in a Polygon");
        }
        chainsHeads = new ArrayList<>(nodeChains.size());
        for (List< ? extends Node> nds : nodeChains) {
            if (nds.size() < 3) {
                throw new IllegalArgumentException(String.format("Each chain in a polygon must contain at least 3 nodes as vertes!%n nodesChain[%d] has only %d nodes", nodeChains.indexOf(nds), nds.size()));
            }
            Segment2D chainHead = new Segment2D();
            Segment2D seg = chainHead;
            for (Node nd : nds) {
                seg.head = nd;
                Segment2D succ = new Segment2D();
                seg.succ = succ;
                succ.pred = seg;
                seg = succ;
            }
            chainHead.pred = seg.pred;
            chainHead.pred.succ = chainHead;
            chainsHeads.add(chainHead);
        }
        prepareLRTree();
        genSegIds();
    }

    public void refresh() {
        prepareLRTree();
        genSegIds();
    }

    private void genSegIds() {
        int id = 0;
        for (Segment2D seg : this) {
            seg.setId(id);
            id++;
        }
    }

    private void prepareLRTree() {
        double maxLen = 0;
        double minLen = Double.POSITIVE_INFINITY;
        LinkedList<WithPair<double[], Segment2D>> midSegPairs = new LinkedList<>();
        for (Segment2D seg : this) {
            PairPack<double[], Segment2D> midSegPair = new PairPack<>(seg.midPoint(), seg);
            double len = seg.length();
            if (len > maxLen) {
                maxLen = len;
            }
            if (len < minLen) {
                minLen = len;
            }
            midSegPairs.add(midSegPair);
        }
        ArrayList<Comparator<double[]>> comps = new ArrayList<>(2);
        for (int i = 0; i < DIM; i++) {
            comps.add(new DoubleArrayComparator(i));
        }
        lrTree = new LayeredRangeTree<>(midSegPairs, comps);
        maxSegLen = maxLen;
        minSegLen = minLen;
    }

    public static Polygon2D byCoordChains(double[][][] coordChains) {
        ArrayList<ArrayList<Node>> nodeChains = new ArrayList<>(coordChains.length);
        for (double[][] coords : coordChains) {
            ArrayList<Node> nodes = new ArrayList<>(coords.length);
            nodeChains.add(nodes);
            for (double[] coord : coords) {
                nodes.add(new Node(coord));
            }
        }
        return new Polygon2D(nodeChains);
    }

    /**
     * Originate from:<\br> Joseph O'Rourke, Computational Geometry in C,2ed.
     * Page 244, Code 7.13
     *
     * @param x
     * @param y
     * @return 'i' : inside , 'o' : outside, 'e' on an edge, 'v' on a vertex
     */
    public char rayCrossing(double x, double y) {
        int rCross = 0, lCross = 0;
        for (Segment2D seg : this) {
            Node head = seg.getHead();
            double x1 = head.coord[0];
            double y1 = head.coord[1];
            if (x1 == x && y1 == y) {
                return 'v';
            }
            Node rear = seg.getRear();
            double x2 = rear.coord[0];
            double y2 = rear.coord[1];

            boolean rStrad = (y1 > y) != (y2 > y);
            boolean lStrad = (y1 < y) != (y2 < y);

            if (rStrad || lStrad) {
                if (rStrad && x1 > x && x2 > x) {
                    rCross += 1;
                } else if (lStrad && x1 < x && x2 < x) {
                    lCross += 1;
                } else {
                    double xCross = (x1 * y - x1 * y2 - x2 * y + x2 * y1) / (y1 - y2);
                    if (rStrad && xCross > x) {
                        rCross++;
                    }
                    if (lStrad && xCross < x) {
                        lCross++;
                    }
                }
            }
        }
        rCross %= 2;
        lCross %= 2;
        if (rCross != lCross) {
            return 'e';
        }
        if (rCross == 1) {
            return 'i';
        } else {
            return 'o';
        }
    }

    public double distanceFunc(double x, double y) {
        char rayCrs = rayCrossing(x, y);

        if (rayCrs == 'e' || rayCrs == 'v') {
            return 0;
        }

        double inf = Double.POSITIVE_INFINITY;
        for (Segment2D seg : this) {
            double dst = seg.distanceTo(x, y);
            if (dst < inf) {
                inf = dst;
            }
        }
        return rayCrs == 'i' ? inf : -inf;
    }

    public void rangeSearch(Collection<? super Segment2D> results, double[] from, double[] to) {
        lrTree.rangeSearch(results, from, to);
    }

    public List<Segment2D> segmentsIntersectingDisc(double[] center, double radius, List<Segment2D> output) {
        if (null == output) {
            output = new LinkedList<>();
        } else {
            output.clear();
        }
        if (radius < 0) {
            throw new IllegalArgumentException("Radius is negative!");
        }
        double[] from = new double[]{center[0] - radius - maxSegLen / 2, center[1] - radius - maxSegLen / 2};
        double[] to = new double[]{center[0] + radius + maxSegLen / 2, center[1] + radius + maxSegLen / 2};

        LinkedList<Segment2D> segs = new LinkedList<>();
        rangeSearch(segs, from, to);
        for (Segment2D seg : segs) {
            if (seg.distanceTo(center[0], center[1]) <= radius) {
                output.add(seg);
            }
        }
        return output;
    }

    @Override
    public Iterator<Segment2D> iterator() {
        return new SegmentIterator();
    }

    class SegmentIterator implements Iterator<Segment2D> {

        int chainId = 0;
        Segment2D seg = chainsHeads.isEmpty() ? null : chainsHeads.get(0);
        Segment2D last;

        @Override
        public boolean hasNext() {
            return chainId < chainsHeads.size();
        }

        @Override
        public Segment2D next() {
            Segment2D res = seg;
            seg = (Segment2D) seg.succ;
            if (seg == chainsHeads.get(chainId)) {
                chainId++;
                if (chainId < chainsHeads.size()) {
                    seg = chainsHeads.get(chainId);
                }
            }
            last = res;
            return res;
        }

        @Override
        public void remove() {
            if (last.pred.pred == last.succ) {
                throw new IllegalStateException("The chain is only a triangle, and no segments can be removed!");
            }
            last.pred.succ = last.succ;
            last.succ.pred = last.pred;
        }
    }

    public double getMaxSegmentLength() {
        return maxSegLen;
    }

    public Polygon2D fractionize(double lenUpBnd) {
        if (lenUpBnd <= 0) {
            throw new IllegalArgumentException("maxLength should be greater than 0 :" + lenUpBnd);
        }
        Polygon2D res = copy(false);
        for (Segment2D cHead : res.chainsHeads) {
            Segment2D seg = cHead;
            do {
                while (seg.length() > lenUpBnd) {
                    seg.bisection();
                }
                seg = (Segment2D) seg.succ;
            } while (seg != cHead);
        }
        res.refresh();
        return res;
    }

    public Polygon2D copy(boolean deep) {
        ArrayList<LinkedList<Node>> vertes = getVertes();
        if (deep) {
            ArrayList<LinkedList<Node>> vertesCopy = new ArrayList<>(vertes.size());
            for (LinkedList<Node> chain : vertes) {
                LinkedList<Node> cs = new LinkedList<>();
                vertesCopy.add(cs);
                for (Node nd : chain) {
                    cs.add(new Node(nd.coord, true));
                }
            }
            return new Polygon2D(vertesCopy);
        } else {
            return new Polygon2D(vertes);
        }
    }

    public ArrayList<LinkedList<Node>> getVertes() {
        ArrayList<LinkedList<Node>> res = new ArrayList<>(chainsHeads.size());
        for (Segment2D cHead : chainsHeads) {
            LinkedList<Node> vs = new LinkedList<>();
            res.add(vs);
            Segment2D seg = cHead;
            do {
                vs.add(seg.getHead());
                seg = (Segment2D) seg.succ;
            } while (seg != cHead);
        }
        return res;
    }
}
