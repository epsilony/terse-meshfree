/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.influence;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.mf.model.Model2D;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.LinearSegment2D;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.mf.model.search.LRTreeNodesSphereSearcher;
import net.epsilony.mf.model.search.LRTreeSegmentChordIntersectingSphereSearcher;
import net.epsilony.mf.model.search.SphereSearcher;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.mf.model.support_domain.SupportDomainSearcherFactory;
import net.epsilony.tb.Math2D;
import net.epsilony.tb.TestTool;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class EnsureNodesNumTest {

    public EnsureNodesNumTest() {
    }

    /**
     * Test of calcInflucenceRadius method, of class EnsureNodesNum.
     */
    @Test
    public void testInflucenceRadius() {
        EnsureNodesNum calc = new EnsureNodesNum(5, 10);
        Model2D sampleModel = sampleModel();
        LinearSegment2D sampleBnd = sampleModel.getPolygon().getChainsHeads().get(0);
        Node sampleNode = sampleBnd.getHead();
        int[] numLowerBounds = new int[]{2, 4, 8, 20};
        SphereSearcher<Node> nodesSearcher = new LRTreeNodesSphereSearcher<>();
        nodesSearcher.setAll(sampleModel.getAllNodes());
        SphereSearcher<Segment2D> segmentsSearcher = new LRTreeSegmentChordIntersectingSphereSearcher();
        segmentsSearcher.setAll(sampleModel.getPolygon().getSegments());
//        SupportDomainSearcherFactory factory =
//                SupportDomainSearcherFactory.layeredRangeTreeBasedFactory(
//                sampleModel.getAllNodes(), sampleModel.getPolygon());

        SupportDomainSearcherFactory factory = new SupportDomainSearcherFactory(nodesSearcher, segmentsSearcher);
        SupportDomainSearcher searcher = factory.produce();
        calc.setSupportDomainSearcher(searcher);
        for (boolean onlySpaceNodes : new boolean[]{false, true}) {
            LinkedList<Double> enlargedDistances = new LinkedList<>();
            List<Node> nodes = onlySpaceNodes ? sampleModel.getSpaceNodes() : sampleModel.getAllNodes();
            calc.setOnlyCountSpaceNodes(onlySpaceNodes);
            for (Node nd : nodes) {
                double distance = Math2D.distance(nd.getCoord(), sampleTranslateVector);
                double enlarged = calc.getResultEnlargeRatio() * distance;
                enlargedDistances.add(enlarged);
            }
            Collections.sort(enlargedDistances);
            System.out.println("enlargedDistances = " + enlargedDistances);
            boolean getHere = false;
            for (int i = 0; i < numLowerBounds.length; i++) {
                calc.setNodesNumLowerBound(numLowerBounds[i]);
                double act = calc.calcInflucenceRadius(sampleNode, sampleBnd);
                double exp = enlargedDistances.get(numLowerBounds[i] - 1);
                assertEquals(exp, act, 1e-10);
                getHere = true;
            }
            assertTrue(getHere);
        }
    }

    private Model2D sampleModel() {
        Polygon2D triPolygon = sampleTrianglePolygon();
        List<Node> spaceNodes = sampleSpaceNodesInTriangle();
        return new Model2D(triPolygon, spaceNodes);
    }

    private Polygon2D sampleTrianglePolygon() {
        double[][] vertes = threeSampleTriangleVertes();
        int[] numByEdge = new int[]{12, 20, 10};
        LinkedList<Node> triangleVertes = genAllTriangleVertesNodes(vertes, numByEdge);
        Polygon2D triangle = new Polygon2D(Arrays.asList(triangleVertes));
        return triangle;
    }
    private double[] sampleTranslateVector = new double[]{-1, 2};

    private double[] translate(double[] vec) {
        return new double[]{vec[0] + sampleTranslateVector[0], vec[1] + sampleTranslateVector[1]};
    }

    private LinkedList<Node> sampleSpaceNodesInTriangle() {
        double[][] starts = new double[][]{{3, 2}, {2, 6}};
        double[][] ends = new double[][]{{40, 30}, {10, 45}};
        int[] numPts = new int[]{10, 10};
        LinkedList<Node> result = new LinkedList<>();
        for (int i = 0; i < numPts.length; i++) {
            double[] start = translate(starts[i]);
            double[] end = translate(ends[i]);
            int numPt = numPts[i];
            result.addAll(linSpaceNodes(start, end, numPt));
        }
        return result;
    }

    private LinkedList<Node> linSpaceNodes(double[] start, double[] end, int numNds) {
        LinkedList<Node> result = new LinkedList<>();
        LinkedList<double[]> coords = TestTool.linSpace2D(start, end, numNds);
        for (double[] coord : coords) {
            result.add(new Node(coord));
        }
        return result;
    }

    private LinkedList<Node> genAllTriangleVertesNodes(double[][] threeVertes, int[] numByEdge) {
        LinkedList<Node> result = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            double[] start = threeVertes[i];
            double[] end = threeVertes[(i + 1) % 3];
            int numPt = numByEdge[i];
            LinkedList<double[]> coords = TestTool.linSpace2D(start, end, numPt);
            for (double[] coord : coords.subList(0, coords.size() - 1)) {
                result.add(new Node(coord));
            }
        }
        return result;
    }

    private double[][] threeSampleTriangleVertes() {
        double[] a = new double[]{0, 0};
        double[] b = new double[]{60, 26};
        double[] c = new double[]{-13, 60};

        double[][] result = new double[3][];
        int i = 0;
        for (double[] pt : new double[][]{a, b, c}) {
            result[i] = translate(pt);
            i++;
        }
        return result;
    }
}
