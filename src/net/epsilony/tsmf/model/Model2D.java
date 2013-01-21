/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.util.DoubleArrayComparator;
import static net.epsilony.tsmf.util.Math2D.cross;
import static net.epsilony.tsmf.util.Math2D.distance;
import static net.epsilony.tsmf.util.Math2D.isSegmentsIntersecting;
import net.epsilony.tsmf.util.rangesearch.LayeredRangeTree;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a> St-Pierre</a>
 */
public class Model2D {

    Polygon2D polygon;
    ArrayList<Node> allNodes;
    LayeredRangeTree<double[], Node> nodesLRTree;
    private final int DIM = 2;
    ArrayList<Node> spaceNodes;
    private final SearchMethod searchMethod;

    public void search(double[] center, Segment2D bnd, double radius, List<Node> nodes, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> ndBlockBySeg) {
        searchMethod.search(center, bnd, radius, nodes, segs, blockedNds, ndBlockBySeg);
    }

    public class SearchMethod {

        void initOutput(List<Node> nodes, List<Segment2D> segs,
                List<Node> blockedNds, List<Segment2D> ndBlockBySeg) {
            nodes.clear();
            if (null != blockedNds) {
                blockedNds.clear();
                ndBlockBySeg.clear();
            }
            segs.clear();
        }

        public void search(double[] center, Segment2D bnd, double radius,
                List<Node> nodes, List<Segment2D> segs,
                List<Node> blockedNds, List<Segment2D> ndBlockBySeg) {

            initOutput(nodes, segs, blockedNds, ndBlockBySeg);
            LinkedList<Node> rangeSearchedNds = searchNodesAndSegs(center, radius, segs);
            if (null != bnd) {
                filetByBnd(center, bnd, radius, rangeSearchedNds, segs, blockedNds, ndBlockBySeg);
            }
            filetBySegments(center, bnd, radius, rangeSearchedNds, segs, blockedNds, ndBlockBySeg);
            nodes.addAll(rangeSearchedNds);
        }

        protected LinkedList<Node> searchNodesAndSegs(double[] center, double radius, List<Segment2D> segs) {
            polygon.segmentsIntersectingDisc(center, radius, segs);
            LinkedList<Node> rangeSearchedNds = new LinkedList<>();
            double[] from = new double[]{center[0] - radius, center[1] - radius};
            double[] to = new double[]{center[0] + radius, center[1] + radius};
            nodesLRTree.rangeSearch(rangeSearchedNds, from, to);
            Iterator<Node> rsIter = rangeSearchedNds.iterator();
            while (rsIter.hasNext()) {
                Node nd = rsIter.next();
                if (distance(nd.coord, center) >= radius) {
                    rsIter.remove();
                }
            }
            return rangeSearchedNds;
        }

        protected void filetBySegments(double[] center, Segment2D bnd, double radius, LinkedList<Node> rangeSearchedNds, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> ndBlockBySeg) {
            for (Segment2D seg : segs) {
                if (seg == bnd) {
                    continue;
                }
                Iterator<Node> rsIter = rangeSearchedNds.iterator();
                Node head = seg.getHead();
                Node rear = seg.getRear();
                double[] hCoord = head.coord;
                double[] rCoord = rear.coord;
                while (rsIter.hasNext()) {
                    Node nd = rsIter.next();
                    if (nd == head || nd == rear) {
                        continue;
                    }
                    if (isSegmentsIntersecting(center, nd.coord, hCoord, rCoord)) {
                        rsIter.remove();
                        if (null != blockedNds) {
                            blockedNds.add(nd);
                            ndBlockBySeg.add(seg);
                        }
                    }
                }
            }
        }

        protected void filetByBnd(double[] center, Segment2D bnd, double radius, LinkedList<Node> rangeSearchedNds, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> ndBlockBySeg) {
            if (null != bnd) {
                double[] hc = bnd.getHead().coord;
                double[] rc = bnd.getRear().coord;
                double dx = rc[0] - hc[0];
                double dy = rc[1] - hc[1];
                Iterator<Node> rsIter = rangeSearchedNds.iterator();
                while (rsIter.hasNext()) {
                    Node nd = rsIter.next();
                    double[] nc = nd.coord;
                    if (cross(dx, dy, nc[0] - hc[0], nc[1] - hc[1]) < 0) {
                        rsIter.remove();
                        if (null != blockedNds) {
                            blockedNds.add(nd);
                            ndBlockBySeg.add(bnd);
                        }
                    }
                }
            }
        }
    }
    public static final double DEFAULT_DISTANCE_RATION = 1e-6;

    public class PerturbationSearch extends SearchMethod {

        public double distance_ratio;

        public PerturbationSearch(double distance_ratio) {
            this.distance_ratio = distance_ratio;
        }

        public PerturbationSearch() {
            distance_ratio = DEFAULT_DISTANCE_RATION;
        }

        private double[] perturbCenter(double[] center, Segment2D bnd, List<Segment2D> segs) {
            Node head = bnd.getHead();
            Node rear = bnd.getRear();
            double[] hCoord = head.coord;
            double[] rCoord = rear.coord;

            double[] pertCenter = new double[2];
            double dx = rCoord[0] - hCoord[0];
            double dy = rCoord[1] - hCoord[1];
            pertCenter[0] = -dy * distance_ratio + center[0];
            pertCenter[1] = dx * distance_ratio + center[1];

            for (Segment2D seg : segs) {
                if (seg == bnd) {
                    continue;
                }
                if (isSegmentsIntersecting(center, pertCenter, seg.getHead().coord, seg.getRear().coord)) {
                    throw new IllegalStateException("Center and perturbed center over cross a segment, center:" + Arrays.toString(center) + " perturbed center" + Arrays.toString(pertCenter) + " seg: " + seg);
                }
            }
            return pertCenter;
        }

        @Override
        public void search(double[] center, Segment2D bnd, double radius, List<Node> nodes, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> ndBlockBySeg) {
            initOutput(nodes, segs, blockedNds, ndBlockBySeg);
            LinkedList<Node> rangeSearchedNds = searchNodesAndSegs(center, radius, segs);
            double[] actCenter = (null == bnd) ? center : perturbCenter(center, bnd, segs);
            filetBySegments(actCenter, null, radius, rangeSearchedNds, segs, blockedNds, ndBlockBySeg);
            nodes.addAll(rangeSearchedNds);
        }
    }

    public Model2D(Polygon2D polygon, List<Node> spaceNodes, boolean useDisturbedCenterOnBnd) {
        this.polygon = polygon;
        this.spaceNodes = new ArrayList<>(spaceNodes);
        allNodes = new ArrayList<>(spaceNodes);
        LinkedList<Node> segNds = new LinkedList<>();
        for (Segment2D seg : polygon) {
            segNds.add(seg.getHead());
        }
        allNodes.addAll(segNds);
        nodesLRTree = new LayeredRangeTree<>(allNodes, DoubleArrayComparator.comparatorsForAll(DIM));
        int id = 0;
        for (Node nd : allNodes) {
            nd.setId(id);
            id++;
        }
        if (useDisturbedCenterOnBnd) {
            searchMethod = new PerturbationSearch();
        } else {
            searchMethod = new SearchMethod();
        }
    }

    public Model2D(Polygon2D polygon, List<Node> spaceNodes) {
        this(polygon, spaceNodes, true);
    }
}
