/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.Node;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author epsilon
 */
public class AdaptiveCellEdgeTest {

    public AdaptiveCellEdgeTest() {
    }

    @Test
    public void testBisectionAndReturnNewSuccessor() {
        testBisectionWithTwoOpposites();
        testOneOneBisection();
        testBisectionOneOppositeToABiggerOne();
    }

    public void testBisectionWithTwoOpposites() {
        double[][] coords = new double[][]{{0, 0}, {-1, 0}, {-2, 0}};
        int[] sampleEdgesHeadsIndes = new int[]{0, 2};
        int[] oppositesEdgesHeadsIndes = new int[]{2, 1, 0};
        int[][] sampleSideOppsitesIndes = new int[][]{{1, 0}};
        int[][] oppositeSideOppositedIndes = new int[][]{{0}, {0}};

        SampleData sampleData = genSample(coords,
                sampleEdgesHeadsIndes, oppositesEdgesHeadsIndes,
                sampleSideOppsitesIndes, oppositeSideOppositedIndes);
        AdaptiveCellEdge sampleEdge = sampleData.sampleSideEdges.get(0);
        AdaptiveCellEdge newSucc = sampleEdge.bisectionAndReturnNewSuccessor();
        assertTrue(newSucc == sampleEdge.succ);
        assertTrue(newSucc.numOpposites() == 1);
        assertTrue(sampleEdge.numOpposites() == 1);
        assertTrue(newSucc.getOpposite(0) == sampleData.oppositeSideEdges.get(0));
        assertTrue(sampleEdge.getOpposite(0) == sampleData.oppositeSideEdges.get(1));
        assertTrue(sampleData.sampleSideEdges.get(1) == newSucc.succ);
        assertTrue(sampleData.sampleSideEdges.get(1).pred == newSucc);
        assertTrue(sampleData.oppositeSideEdges.get(0).numOpposites() == 1);
        assertTrue(sampleData.oppositeSideEdges.get(1).numOpposites() == 1);
        assertTrue(sampleData.oppositeSideEdges.get(0).getOpposite(0) == newSucc);
        assertTrue(sampleData.oppositeSideEdges.get(1).getOpposite(0) == sampleEdge);
        assertTrue(sampleEdge.getRear() == sampleData.oppositeSideEdges.get(1).getHead());
    }

    void testOneOneBisection() {
        double[][] coords = new double[][]{{0, 0}, {-1, 1}, {-2, 0}};
        int[] sampleEdgesHeadsIndes = new int[]{0, 1, 2};
        int[] oppositesEdgesHeadsIndes = new int[]{2, 1, 0};
        int[][] sampleSideOppsitesIndes = new int[][]{{1}, {0}};
        int[][] oppositeSideOppositedIndes = new int[][]{{1}, {0}};

        SampleData sampleData = genSample(coords,
                sampleEdgesHeadsIndes, oppositesEdgesHeadsIndes,
                sampleSideOppsitesIndes, oppositeSideOppositedIndes);
        AdaptiveCellEdge sampleEdge = sampleData.sampleSideEdges.get(0);
        AdaptiveCellEdge newSucc = sampleEdge.bisectionAndReturnNewSuccessor();
        assertTrue(sampleData.oppositeSideEdges.get(1).numOpposites() == 2);
        assertTrue(sampleData.oppositeSideEdges.get(1).getOpposite(0) == newSucc);
        assertTrue(sampleData.oppositeSideEdges.get(1).getOpposite(1) == sampleEdge);
        assertArrayEquals(new double[]{-0.5, 0.5}, sampleEdge.getRear().coord, 1e-14);
        assertTrue(sampleEdge.numOpposites() == 1);
        assertTrue(sampleEdge.getOpposite(0) == sampleData.oppositeSideEdges.get(1));
        assertTrue(newSucc.numOpposites() == 1);
        assertTrue(newSucc.getOpposite(0) == sampleEdge.getOpposite(0));
        assertTrue(newSucc.succ == sampleData.sampleSideEdges.get(1));
        assertTrue(sampleData.sampleSideEdges.get(1).pred == newSucc);
        assertTrue(newSucc.pred == sampleEdge);
        assertTrue(sampleEdge.succ == newSucc);
    }

    public void testBisectionOneOppositeToABiggerOne() {
        double[][] coords = new double[][]{{0, 0}, {-1, 1}, {-2, 0}};
        int[] sampleEdgesHeadsIndes = new int[]{0, 1, 2};
        int[] oppositesEdgesHeadsIndes = new int[]{2, 0};
        int[][] sampleSideOppsitesIndes = new int[][]{{0}, {0}};
        int[][] oppositeSideOppositedIndes = new int[][]{{1, 0}};

        SampleData sampleData = genSample(coords,
                sampleEdgesHeadsIndes, oppositesEdgesHeadsIndes,
                sampleSideOppsitesIndes, oppositeSideOppositedIndes);
        AdaptiveCellEdge sampleEdge = sampleData.sampleSideEdges.get(0);
        assertTrue(!sampleEdge.isAbleToBisection());
        boolean catched = false;
        try {
            AdaptiveCellEdge newSucc = sampleEdge.bisectionAndReturnNewSuccessor();
        } catch (IllegalStateException e) {
            catched = true;
        }
        assertTrue(catched);
    }

    @Test
    public void testMergeWithGivenSuccessor() {
        testMergeOneOne();
        testMergeEdgesWithSameOpposite();
        testMergeEdgesWithTwoOpposites();
    }

    public void testMergeOneOne() {
        double[][] coords = new double[][]{
            {-1, 0}, {0, 0}, {1, 0}, {0, 1}, {0, -1}
        };
        int[] sampleEdgesHeadsIndes = new int[]{2, 1, 4, 1, 0};
        int[] oppositesEdgesHeadsIndes = new int[]{0, 1, 3, 1, 2};
        int[][] sampleSideOppsitesIndes = new int[][]{{3}, {}, {}, {0}};
        int[][] oppositeSideOppositedIndes = new int[][]{{3}, {}, {}, {0}};

        SampleData sampleData = genSample(coords,
                sampleEdgesHeadsIndes, oppositesEdgesHeadsIndes,
                sampleSideOppsitesIndes, oppositeSideOppositedIndes);

        AdaptiveCellEdge sample = sampleData.sampleSideEdges.get(0);
        AdaptiveCellEdge successor = sampleData.sampleSideEdges.get(3);
        sample.mergeWithGivenSuccessor(successor);
        assertTrue(sample.numOpposites() == 2);
        assertTrue(sample.getOpposite(0) == sampleData.oppositeSideEdges.get(3));
        assertTrue(sample.getOpposite(1) == sampleData.oppositeSideEdges.get(0));
        assertTrue(sampleData.oppositeSideEdges.get(0).numOpposites() == 1);
        assertTrue(sampleData.oppositeSideEdges.get(0).getOpposite(0) == sample);
        assertTrue(sampleData.oppositeSideEdges.get(3).numOpposites() == 1);
        assertTrue(sampleData.oppositeSideEdges.get(3).getOpposite(0) == sample);
        assertTrue(sample.getHead().coord == sampleData.oppositeSideEdges.get(3).getRear().coord);
        assertTrue(sample.getRear().coord == sampleData.oppositeSideEdges.get(0).getHead().coord);
    }

    public void testMergeEdgesWithTwoOpposites() {
        double[][] coords = new double[][]{
            {0, 0}, {-1, 0}, {-2, 0}, {-3, 0}, {-4, 0}, {-2, -2}, {-1, 1}, {-2, 1}, {-3, 1}
        };
        int[] sampleEdgesHeadsIndes = new int[]{0, 2, 5, 2, 4};
        int[] oppositesEdgesHeadsIndes = new int[]{4, 3, 8, 3, 2, 7, 2, 1, 6, 1, 0};
        int[][] sampleSideOppsitesIndes = new int[][]{{9, 6}, {}, {}, {3, 0}};
        int[][] oppositeSideOppositedIndes = new int[][]{{3}, {}, {}, {3}, {}, {}, {0}, {}, {}, {0}};

        SampleData sampleData = genSample(coords,
                sampleEdgesHeadsIndes, oppositesEdgesHeadsIndes,
                sampleSideOppsitesIndes, oppositeSideOppositedIndes);

        AdaptiveCellEdge sample = sampleData.sampleSideEdges.get(0);
        AdaptiveCellEdge successor = sampleData.sampleSideEdges.get(3);
        assertTrue(!sample.isAbleToMerge(successor));

        List<AdaptiveCellEdge> backOpposites = successor.opposites;
        successor.opposites = new LinkedList<>();
        assertTrue(!sample.isAbleToMerge(successor));
        successor.opposites = backOpposites;

        backOpposites = sample.opposites;
        sample.opposites = new LinkedList<>();
        assertTrue(!sample.isAbleToMerge(successor));
        sample.opposites = backOpposites;
    }

    public void testMergeEdgesWithSameOpposite() {
        double[][] coords = new double[][]{
            {-1, 0}, {0, 0}, {1, 0}, {0, 1}, {0, -1}
        };
        int[] sampleEdgesHeadsIndes = new int[]{0, 1, 4, 1, 2};
        int[] oppositesEdgesHeadsIndes = new int[]{2, 0};
        int[][] sampleSideOppsitesIndes = new int[][]{{0}, {}, {}, {0}};
        int[][] oppositeSideOppositedIndes = new int[][]{{3, 0}};

        SampleData sampleData = genSample(coords,
                sampleEdgesHeadsIndes, oppositesEdgesHeadsIndes,
                sampleSideOppsitesIndes, oppositeSideOppositedIndes);

        AdaptiveCellEdge sample = sampleData.sampleSideEdges.get(0);
        AdaptiveCellEdge successor = sampleData.sampleSideEdges.get(3);

        sample.mergeWithGivenSuccessor(successor);

        assertTrue(sample.numOpposites() == 1);
        AdaptiveCellEdge commonOpposite = sampleData.oppositeSideEdges.get(0);
        assertTrue(sample.getOpposite(0) == commonOpposite);
        assertTrue(commonOpposite.numOpposites() == 1);
        assertTrue(commonOpposite.getOpposite(0) == sample);
        assertTrue(sample.getHead().coord == commonOpposite.getRear().coord);
        assertTrue(sample.getRear().coord == commonOpposite.getHead().coord);
    }

    public SampleData genSample(
            double[][] coords,
            int[] sampleEdgesNodesIndes,
            int[] oppositesEdgesNodesIndes,
            int[][] sampleSideOppsitesIndes,
            int[][] oppositeSideOppositedIndes) {
        Node[] nodes = new Node[coords.length];
        for (int i = 0; i < coords.length; i++) {
            nodes[i] = new Node(coords[i]);
        }
        List<AdaptiveCellEdge> sampleSideEdges = genSequentEdgesFromNodesAndHeadNodesIndes(nodes, sampleEdgesNodesIndes);
        List<AdaptiveCellEdge> oppositeSideEdges = genSequentEdgesFromNodesAndHeadNodesIndes(nodes, oppositesEdgesNodesIndes);
        fillOpposites(sampleSideEdges, oppositeSideEdges, sampleSideOppsitesIndes);
        fillOpposites(oppositeSideEdges, sampleSideEdges, oppositeSideOppositedIndes);
        SampleData result = new SampleData();
        result.sampleSideEdges = sampleSideEdges;
        result.oppositeSideEdges = oppositeSideEdges;
        return result;
    }

    public List<AdaptiveCellEdge> genSequentEdgesFromNodesAndHeadNodesIndes(Node[] nodes, int[] headIndes) {
        ArrayList<AdaptiveCellEdge> edges = new ArrayList<>(headIndes.length);
        for (int i = 0; i < headIndes.length; i++) {
            edges.add(new AdaptiveCellEdge(nodes[headIndes[i]]));
        }
        for (int i = 0; i < headIndes.length - 1; i++) {
            edges.get(i).succ = edges.get(i + 1);
            edges.get(i + 1).pred = edges.get(i);
        }
        return edges;
    }

    private void fillOpposites(List<AdaptiveCellEdge> thisSideEdges, List<AdaptiveCellEdge> oppositeSideEdges, int[][] oppsiteSideEdgesIndes) {
        for (int i = 0; i < oppsiteSideEdgesIndes.length; i++) {
            int[] oppsiteIndes = oppsiteSideEdgesIndes[i];
            for (int j = 0; j < oppsiteIndes.length; j++) {
                thisSideEdges.get(i).opposites.add(oppositeSideEdges.get(oppsiteIndes[j]));


            }
        }
    }

    public static class SampleData {

        List<AdaptiveCellEdge> sampleSideEdges;
        List<AdaptiveCellEdge> oppositeSideEdges;
    }
}