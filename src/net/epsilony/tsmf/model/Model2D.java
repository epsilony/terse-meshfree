/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.util.DoubleArrayComparator;
import net.epsilony.tsmf.util.Math2D;
import static net.epsilony.tsmf.util.Math2D.cross;
import static net.epsilony.tsmf.util.Math2D.distance;
import static net.epsilony.tsmf.util.Math2D.isSegmentsIntersecting;
import net.epsilony.tsmf.util.rangesearch.LayeredRangeTree;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a> St-Pierre</a>
 */
public class Model2D implements ModelSearcher {

    Polygon2D polygon;
    ArrayList<Node> allNodes;
    private LayeredRangeTree<double[], Node> nodesLRTree;
    public final static int DIM = 2;
    ArrayList<Node> spaceNodes;   //allNode - polygon.getVertes()
    private final SearchMethod searchMethod;
    TDoubleArrayList influenceRads;
    public final static boolean DEFAULT_WHETHER_USE_DISTURB = true;

    @Override
    public void searchModel(double[] center, Segment2D bnd, double radius, boolean filetByInfluence, List<Node> nodes, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> blockNdsSegs) {
        searchMethod.search(center, bnd, radius, filetByInfluence, nodes, segs, blockedNds, blockNdsSegs);
    }

    public double maxInfluenceRad() {
        return influenceRads.max();
    }

    public Polygon2D getPolygon() {
        return polygon;
    }

    public ArrayList<Node> getSpaceNodes() {
        return spaceNodes;
    }

    public ArrayList<Node> getAllNodes() {
        return allNodes;
    }

    public void searchNodesSegments(double[] center, double radius, List<Node> nodes, List<Segment2D> segs) {
        nodes.clear();
        segs.clear();
        polygon.segmentsIntersectingDisc(center, radius, segs);
        double[] from = new double[]{center[0] - radius, center[1] - radius};
        double[] to = new double[]{center[0] + radius, center[1] + radius};
        nodesLRTree.rangeSearch(nodes, from, to);
        Iterator<Node> rsIter = nodes.iterator();
        while (rsIter.hasNext()) {
            Node nd = rsIter.next();
            if (distance(nd.coord, center) >= radius) {
                rsIter.remove();
            }
        }
    }

    public List<Node> filetNodesByInfluenceRad(double[] center, List<Node> nodes, List<Node> result) {
        if (null == result) {
            result = new LinkedList<>();
        } else {
            result.clear();
        }
        for (Node nd : nodes) {
            double rad = getInfluenceRad(nd);
            if (rad > distance(nd.coord, center)) {
                result.add(nd);
            }
        }
        return result;
    }

    public double getInfluenceRad(Node node) {
        if (allNodes.get(node.id) != node) {
            throw new IllegalArgumentException("the input node is not in this model of node index been modified unproperly:" + node);
        }
        return influenceRads.get(node.id);
    }

    public void setInfluenceRad(Node node, double rad) {
        if (allNodes.get(node.id) != node) {
            throw new IllegalArgumentException("the input node is not in this model of node index been modified unproperly:" + node);
        }
        influenceRads.set(node.id, rad);
    }

    public void setInfluenceRadForAll(double rad) {
        influenceRads.fill(rad);
    }

    private class SearchMethod {

        private void initOutput(List<Node> nodes, List<Segment2D> segs,
                List<Node> blockedNds, List<Segment2D> blockNdsSegs) {
            nodes.clear();
            segs.clear();
            if (null != blockedNds) {
                blockedNds.clear();
                blockNdsSegs.clear();
            }
        }

        protected void search(double[] center, Segment2D bnd, double radius, boolean filetByInflucen,
                List<Node> nodes, List<Segment2D> segs,
                List<Node> blockedNds, List<Segment2D> blockNdsSegs) {
            LinkedList<Node> rangeSearchedNds = preSearch(center, radius, filetByInflucen, nodes, segs, blockedNds, blockNdsSegs);
            if (null != bnd) {
                filetByBnd(center, bnd, radius, rangeSearchedNds, segs, blockedNds, blockNdsSegs);
            }
            filetBySegments(center, bnd, radius, rangeSearchedNds, segs, blockedNds, blockNdsSegs);
            nodes.addAll(rangeSearchedNds);
        }

        protected void filetBySegments(double[] center, Segment2D bnd, double radius, LinkedList<Node> rangeSearchedNds, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> blockNdsSegs) {
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
                            blockNdsSegs.add(seg);
                        }
                    }
                }
            }
        }

        protected void filetByBnd(double[] center, Segment2D bnd, double radius, LinkedList<Node> rangeSearchedNds, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> blockNdsSegs) {
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
                            blockNdsSegs.add(bnd);
                        }
                    }
                }
            }
        }

        protected LinkedList<Node> preSearch(double[] center, double radius, boolean filetByInflucen, List<Node> nodes, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> ndBlockBySeg) {
            initOutput(nodes, segs, blockedNds, ndBlockBySeg);
            LinkedList<Node> searchedNds = new LinkedList<>();
            searchNodesSegments(center, radius, searchedNds, segs);
            if (filetByInflucen) {
                LinkedList<Node> bak = searchedNds;
                searchedNds = new LinkedList<>();
                filetNodesByInfluenceRad(center, bak, searchedNds);
            }
            return searchedNds;
        }
    }
    private static double DEFAULT_PERTURB_DISTANCE_RATION = 1e-6;  //perturb distance vs segment length
    // The mininum angle of adjacency segments of polygon. If no angle is less
    // than below, PertubtionSearchMethod works well.
    // Note that the angle of a crack tip is nearly 2pi which is very large.
    private static double DEFAULT_ALLOWABLE_ANGLE = Math.PI / 1800 * 0.95;

    private class PerturbationSearchMethod extends SearchMethod {

        private double perterbDistanceRatio;
        private double minVertexDistanceRatio;

//        private PerturbationSearchMethod(double distance_ratio) {
//            this.perterbDistanceRatio = distance_ratio;
//        }
        private PerturbationSearchMethod() {
            perterbDistanceRatio = DEFAULT_PERTURB_DISTANCE_RATION;
            double minAngle = DEFAULT_ALLOWABLE_ANGLE;
            minVertexDistanceRatio = perterbDistanceRatio / Math.tan(minAngle);
        }

        private double[] perturbCenter(double[] center, Segment2D bnd, List<Segment2D> segs) {
            Node head = bnd.getHead();
            Node rear = bnd.getRear();
            double[] hCoord = head.coord;
            double[] rCoord = rear.coord;

            double[] pertCenter = new double[2];
            double dx = rCoord[0] - hCoord[0];
            double dy = rCoord[1] - hCoord[1];

            double headDistRatio = Math2D.distance(hCoord, center) / bnd.length();
            double[] pertOri = center;
            if (headDistRatio <= minVertexDistanceRatio) {
                pertOri = Math2D.pointOnSegment(hCoord, rCoord, minVertexDistanceRatio, null);
            } else if (headDistRatio >= 1 - minVertexDistanceRatio) {
                pertOri = Math2D.pointOnSegment(hCoord, rCoord, 1 - minVertexDistanceRatio, null);
            }

            pertCenter[0] = -dy * perterbDistanceRatio + pertOri[0];
            pertCenter[1] = dx * perterbDistanceRatio + pertOri[1];
            checkPerturbCenter(center, pertCenter, bnd, segs);
            return pertCenter;
        }

        void checkPerturbCenter(double[] center, double[] perturbedCenter, Segment2D bnd, Collection<? extends Segment2D> segs) {
            Segment2D bndNeighbor = null;
            double[] bndNeighborFurtherPoint = null;
            if (center == bnd.getHead().coord) {
                bndNeighbor = (Segment2D) bnd.pred;
                bndNeighborFurtherPoint = bndNeighbor.getHead().coord;
            } else if (center == bnd.getRear().coord) {
                bndNeighbor = (Segment2D) bnd.succ;
                bndNeighborFurtherPoint = bndNeighbor.getRear().coord;
            }

            if (null != bndNeighbor && bnd.isStrictlyAtLeft(bndNeighborFurtherPoint)) {
                if (!bndNeighbor.isStrictlyAtLeft(perturbedCenter)) {
                    throw new IllegalStateException("perturbed center over cross neighbor of bnd\n\t"
                            + "center :" + Arrays.toString(center) + "\n\t"
                            + "perturbed center :" + Arrays.toString(perturbedCenter) + "\n\t"
                            + "bnd: " + bnd + "\n\t"
                            + "neighbor of bnd: " + bndNeighbor);
                }
            }

            for (Segment2D seg : segs) {
                if (seg == bnd || seg == bndNeighbor) {
                    continue;
                }
                if (isSegmentsIntersecting(center, perturbedCenter, seg.getHead().coord, seg.getRear().coord)) {
                    throw new IllegalStateException("Center and perturbed center over cross a segment\n\t"
                            + "center: " + Arrays.toString(center) + "\n\tperturbed center"
                            + Arrays.toString(perturbedCenter) + "\n\tseg: " + seg);
                }
            }
        }

        @Override
        protected void search(double[] center, Segment2D bnd, double radius, boolean filetByInfluence, List<Node> nodes, List<Segment2D> segs, List<Node> blockedNds, List<Segment2D> ndBlockBySeg) {
            LinkedList<Node> searchedNds = preSearch(center, radius, filetByInfluence, nodes, segs, blockedNds, ndBlockBySeg);
            double[] actCenter = (null == bnd) ? center : perturbCenter(center, bnd, segs);
            filetBySegments(actCenter, null, radius, searchedNds, segs, blockedNds, ndBlockBySeg);
            nodes.addAll(searchedNds);
        }
    }

    public Model2D(Polygon2D polygon, List<Node> spaceNodes, boolean useDisturbSearch) {
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
        if (useDisturbSearch) {
            searchMethod = new PerturbationSearchMethod();
        } else {
            searchMethod = new SearchMethod();
        }
        double[] t = new double[allNodes.size()];
        Arrays.fill(t, Double.POSITIVE_INFINITY);
        influenceRads = new TDoubleArrayList(t);
    }

    public Model2D(Polygon2D polygon, List<Node> spaceNodes) {
        this(polygon, spaceNodes, DEFAULT_WHETHER_USE_DISTURB);
    }
}
