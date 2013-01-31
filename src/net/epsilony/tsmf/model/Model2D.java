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
    private boolean onlySearchingInfluentialNodes;
    private boolean onlyCareVisibleNodes;

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

    public ModelSearchResult searchNodesSegments(double[] center, double radius) {
        ModelSearchResult searchResult = initSearchResult();
        polygon.segmentsIntersectingDisc(center, radius, searchResult.segments);
        double[] from = new double[]{center[0] - radius, center[1] - radius};
        double[] to = new double[]{center[0] + radius, center[1] + radius};
        nodesLRTree.rangeSearch(searchResult.allNodes, from, to);
        Iterator<Node> rsIter = searchResult.allNodes.iterator();
        while (rsIter.hasNext()) {
            Node nd = rsIter.next();
            if (distance(nd.coord, center) >= radius) {
                rsIter.remove();
            }
        }
        return searchResult;
    }

    public void filetNodesByInfluence(double[] center, ModelSearchResult filetAim) {
        Iterator<Node> nodesIter = filetAim.allNodes.iterator();
        while (nodesIter.hasNext()) {
            Node node = nodesIter.next();
            double rad = getInfluenceRad(node);
            if (rad <= distance(node.coord, center)) {
                nodesIter.remove();
            }
        }
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

    @Override
    public boolean isOnlySearchingInfluentialNodes() {
        return onlySearchingInfluentialNodes;
    }

    @Override
    public void setOnlySearchingInfluentialNodes(boolean onlySearchingInfluentialNodes) {
        this.onlySearchingInfluentialNodes = onlySearchingInfluentialNodes;
    }

    @Override
    public boolean isOnlyCareVisibleNodes() {
        return onlyCareVisibleNodes;
    }

    @Override
    public void setOnlyCareVisbleNodes(boolean onlyCareVisibleNodes) {
        this.onlyCareVisibleNodes = onlyCareVisibleNodes;
    }

    @Override
    public ModelSearchResult searchModel(double[] center, Segment2D bndOfCenter, double radius) {
        return searchMethod.search(center, bndOfCenter, radius);
    }

    private ModelSearchResult initSearchResult() {
        ModelSearchResult result = new ModelSearchResult();
        result.allNodes = new LinkedList<>();
        result.segments = new LinkedList<>();
        result.visibleNodes = new LinkedList<>();
        if (isOnlyCareVisibleNodes()) {
            result.invisibleNodes = null;
            result.invisibleNodesBlockedBy = null;
        } else {
            result.invisibleNodes = new LinkedList<>();
            result.invisibleNodesBlockedBy = new LinkedList<>();
        }
        return result;
    }

    private class SearchMethod {

        protected ModelSearchResult search(double[] center, Segment2D bndOfCenter, double radius) {
            ModelSearchResult searchResult = preSearch(center, radius);


            filetAllNodesToVisibleNodesByBndOfCenter(bndOfCenter, searchResult);

            filetVisibleNodeBySegments(center, bndOfCenter, searchResult);
            return searchResult;
        }

        protected void filetVisibleNodeBySegments(double[] center, Segment2D bndOfCenter, ModelSearchResult result) {
            for (Segment2D seg : result.segments) {
                if (seg == bndOfCenter) {
                    continue;
                }
                Iterator<Node> rsIter = result.visibleNodes.iterator();
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
                        if (!isOnlyCareVisibleNodes()) {
                            result.invisibleNodes.add(nd);
                            result.invisibleNodesBlockedBy.add(seg);
                        }
                    }
                }
            }
        }

        protected void filetAllNodesToVisibleNodesByBndOfCenter(Segment2D bndOfCenter, ModelSearchResult result) {
            if (null == bndOfCenter) {
                result.visibleNodes.addAll(result.allNodes);
            } else {
                double[] hc = bndOfCenter.getHead().coord;
                double[] rc = bndOfCenter.getRear().coord;
                double dx = rc[0] - hc[0];
                double dy = rc[1] - hc[1];
                Iterator<Node> rsIter = result.allNodes.iterator();
                while (rsIter.hasNext()) {
                    Node nd = rsIter.next();
                    double[] nc = nd.coord;
                    if (cross(dx, dy, nc[0] - hc[0], nc[1] - hc[1]) < 0) {
                        rsIter.remove();
                        if (!isOnlyCareVisibleNodes()) {
                            result.invisibleNodes.add(nd);
                            result.invisibleNodesBlockedBy.add(bndOfCenter);
                        }
                    } else {
                        result.visibleNodes.add(nd);
                    }
                }
            }
        }

        protected ModelSearchResult preSearch(double[] center, double radius) {
            ModelSearchResult searchResult = searchNodesSegments(center, radius);
            if (isOnlySearchingInfluentialNodes()) {
                filetNodesByInfluence(center, searchResult);
            }
            return searchResult;
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

        private double[] perturbCenter(double[] center, Segment2D bndOfCenter, List<Segment2D> segs) {
            Node head = bndOfCenter.getHead();
            Node rear = bndOfCenter.getRear();
            double[] hCoord = head.coord;
            double[] rCoord = rear.coord;

            double[] pertCenter = new double[2];
            double dx = rCoord[0] - hCoord[0];
            double dy = rCoord[1] - hCoord[1];

            double headDistRatio = Math2D.distance(hCoord, center) / bndOfCenter.length();
            double[] pertOri = center;
            if (headDistRatio <= minVertexDistanceRatio) {
                pertOri = Math2D.pointOnSegment(hCoord, rCoord, minVertexDistanceRatio, null);
            } else if (headDistRatio >= 1 - minVertexDistanceRatio) {
                pertOri = Math2D.pointOnSegment(hCoord, rCoord, 1 - minVertexDistanceRatio, null);
            }

            pertCenter[0] = -dy * perterbDistanceRatio + pertOri[0];
            pertCenter[1] = dx * perterbDistanceRatio + pertOri[1];
            checkPerturbCenter(center, pertCenter, bndOfCenter, segs);
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
        protected ModelSearchResult search(double[] center, Segment2D bndOfCenter, double radius) {
            ModelSearchResult searchResult = preSearch(center, radius);
            double[] searchCenter = (null == bndOfCenter) ? center : perturbCenter(center, bndOfCenter, searchResult.segments);
            filetAllNodesToVisibleNodesByBndOfCenter(null, searchResult);
            filetVisibleNodeBySegments(searchCenter, null, searchResult);
            return searchResult;
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
