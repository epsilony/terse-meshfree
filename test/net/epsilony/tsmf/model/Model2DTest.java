/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.util.IntIdentityComparator;
import net.epsilony.tsmf.util.Math2D;
import net.epsilony.tsmf.util.pair.PairPack;
import net.epsilony.tsmf.util.pair.WithPair;
import net.epsilony.tsmf.util.pair.WithPairComparator;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author epsilon
 */
public class Model2DTest {

    public Model2DTest() {
    }

    @Test
    public void testSearchSimp() {
        double[][][] vertesCoords = new double[][][]{
            {{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2}, {0, 1}},
            {{0.5, 0.5}, {0.5, 0.75}, {1.5, 0.75}, {1.5, 0.5}},};

        double[] center = new double[]{1, 0.45};
        double radius = 1.5;
        double[][] spaceNodeCoords = new double[][]{
            {1, 1},};

        Polygon2D pg = Polygon2D.byCoordChains(vertesCoords);
        LinkedList<Node> spaceNodes = new LinkedList<>();
        for (double[] crd : spaceNodeCoords) {
            spaceNodes.add(new Node(crd));
        }
        Model2D sampleModel2D = new Model2D(pg, spaceNodes);

        LinkedList<Segment2D> segs = new LinkedList<>();
        LinkedList<Segment2D> ndsBlockedBySeg = new LinkedList<>();
        LinkedList<Node> nodes = new LinkedList<>();
        LinkedList<Node> blockedNds = new LinkedList<>();
        sampleModel2D.search(center, null, radius,false, nodes, segs, blockedNds, ndsBlockedBySeg);
        Collections.sort(nodes, new IntIdentityComparator<>());
        List<WithPair<Node, Segment2D>> blockPair = PairPack.pack(blockedNds, ndsBlockedBySeg, new LinkedList<WithPair<Node, Segment2D>>());
        Collections.sort(blockPair, new WithPairComparator<Node, Segment2D>(new IntIdentityComparator<Node>()));
        Collections.sort(segs, new IntIdentityComparator<>());

        int[] ndsIdsExp = new int[]{1, 2, 3, 9, 12};
        int[] segsIdsExp = new int[]{0, 1, 2, 3, 6, 7, 8, 9, 10, 11};
        int idx = 0;
        for (Node nd : nodes) {
            assertEquals(ndsIdsExp[idx], nd.id);
            idx++;
        }
        idx = 0;
        for (Segment2D seg : segs) {
            assertEquals(segsIdsExp[idx], seg.id);
            idx++;
        }
        int[] blockedNdsIds = new int[]{0, 4, 8, 10, 11,};
        idx = 0;
        boolean getHere = false;
        for (WithPair<Node, Segment2D> p : blockPair) {

            assertEquals(blockedNdsIds[idx], p.getKey().id);
            Node exp_nd = p.getKey();
            Segment2D seg = p.getValue();
            assertTrue(
                    Math2D.isSegmentsIntersecting(seg.head.coord, seg.getRear().coord, center, exp_nd.coord));
            idx++;
            getHere = true;
        }
        assertTrue(getHere);
    }

    @Test
    public void testSearchOnAHorizontalBnd() {
        double[][][] vertesCoords = new double[][][]{
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}, {8, 0}, {9, 0}, {9, -1}, {5, -1}, {4, -1}, {4, -2}, {10, -2}, {10, 3}, {0, 3}},
            {{4, 0.5}, {4, 1}, {4.5, 1}, {5, 1}, {5, 0.5}, {4.5, 0.5}}};

        double[] center = new double[]{4.5, 0};
        int bndId = 4;
        double radius = 100;
        double[][] spaceNodeCoords = new double[][]{
            {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2}, {8, 2}};

        Polygon2D pg = Polygon2D.byCoordChains(vertesCoords);
        LinkedList<Segment2D> pgSegs = new LinkedList<>();
        for (Segment2D seg : pg) {
            pgSegs.add(seg);
        }
        Segment2D bnd = pgSegs.get(bndId);
        LinkedList<Node> spaceNodes = new LinkedList<>();
        for (double[] crd : spaceNodeCoords) {
            spaceNodes.add(new Node(crd));
        }
        boolean[] withPerturb = new boolean[]{false, true};
        int[] expSpackNdIdx = new int[]{0, 1, 6, 7};
        int[] expPgNdIdxNoPerb = new int[]{3, 4, 5, 6, 15, 16, 17, 21, 22};
        int[] expPgNdIdxWithPerb = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 15, 16, 17, 21, 22};
        for (boolean wp : withPerturb) {
            Model2D sampleModel2D = new Model2D(pg, spaceNodes, wp);

            LinkedList<Segment2D> segs = new LinkedList<>();
            LinkedList<Segment2D> ndsBlockedBySeg = new LinkedList<>();
            LinkedList<Node> nodes = new LinkedList<>();
            LinkedList<Node> blockedNds = new LinkedList<>();
            sampleModel2D.search(center, bnd, radius,false, nodes, segs, blockedNds, ndsBlockedBySeg);
            Collections.sort(nodes, new IntIdentityComparator<>());
            int[] expPgNdIdx = wp ? expPgNdIdxWithPerb : expPgNdIdxNoPerb;

            int idx = 0;
            boolean getHere = false;
            for (Node nd : nodes) {
                if (idx < expSpackNdIdx.length) {
                    assertEquals(expSpackNdIdx[idx], nd.id);
                } else {
                    assertEquals(expPgNdIdx[idx - expSpackNdIdx.length] + spaceNodeCoords.length, nd.id);
                }
                idx++;
                getHere = true;
            }
            assertTrue(getHere);
        }
    }
}