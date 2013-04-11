/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.adaptive;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.util.DoubleArrayComparator;
import net.epsilony.tsmf.util.Math2D;
import net.epsilony.tsmf.util.pair.PairPack;
import net.epsilony.tsmf.util.pair.WithPair;
import net.epsilony.tsmf.util.pair.WithPairComparator;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Ignore
public abstract class AbstractAdaptiveCellTest {

    public abstract AdaptiveCell[][] genSampleGrid();

    public List<AdaptiveCell> genSamples() {
        LinkedList<AdaptiveCell> result = new LinkedList<>();
        AdaptiveCell[][] genSampleGrid = genSampleGrid();
        for (AdaptiveCell[] row : genSampleGrid) {
            result.addAll(Arrays.asList(row));
        }
        return result;
    }

    public Rectangle2D getBounds(Collection<AdaptiveCell> cells) {
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        for (AdaptiveCell cell : cells) {
            AdaptiveCellEdge[] edges = cell.getEdges();
            for (AdaptiveCellEdge eg : edges) {
                double[] xy = eg.getHeadCoord();
                if (xy[0] > maxX) {
                    maxX = xy[0];
                }
                if (xy[0] < minX) {
                    minX = xy[0];
                }
                if (xy[1] > maxY) {
                    maxY = xy[1];
                }
                if (xy[1] < minY) {
                    minY = xy[1];
                }
            }
        }
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    public List<AdaptiveCell> randomFission(List<AdaptiveCell> rootFissions, boolean recursively, int times) {
        Rectangle2D bounds = getBounds(rootFissions);
        LinkedList<AdaptiveCell> result = new LinkedList<>();
        result.addAll(rootFissions);
        boolean hasFissioned = false;
        for (int i = 0; i < times; i++) {
            Random random = new Random();
            double rX = random.nextDouble();
            double rY = random.nextDouble();
            rX = bounds.getMaxX() * rX + bounds.getMinX() * (1 - rX);
            rY = bounds.getMaxY() * rY + bounds.getMinY() * (1 - rY);
            for (AdaptiveCell cell : rootFissions) {
                if (cell.getChildren() != null) {
                    continue;
                }
                if (!AdaptiveUtils.isPointRestrictlyInsideCell(cell, rX, rY)) {
                    continue;
                }
                LinkedList<AdaptiveCell> newCells = new LinkedList<>();
                AdaptiveUtils.fission(cell, recursively, newCells);
                result.addAll(newCells);
                hasFissioned = true;
                break;
            }
        }
        assertTrue(hasFissioned);
        return result;
    }

    public void testArea() {
        double expArea = 0;
        List<AdaptiveCell> sampleCells = genSamples();
        for (AdaptiveCell cell : sampleCells) {
            expArea += getCellArea(cell);
        }
        List<AdaptiveCell> fissionedCells = randomFission(sampleCells, true, getTestTime());
        double area = 0;
        for (AdaptiveCell cell : fissionedCells) {
            if (cell.getChildren() != null) {
                continue;
            }
            double cellArea = getCellArea(cell);
            area += cellArea;
        }
        assertEquals(expArea, area, 1e-10);
    }

    public void testDuplicatedNode() {
        HashSet<Node> nodes = new HashSet<>();
        List<AdaptiveCell> sampleCells = genSamples();
        List<AdaptiveCell> fissionedCells = randomFission(sampleCells, true, getTestTime());
        double minEdgeLength = Double.POSITIVE_INFINITY;
        for (AdaptiveCell cell : fissionedCells) {
            AdaptiveCellEdge[] edges = cell.getEdges();
            if (null == edges) {
                continue;
            }
            for (AdaptiveCellEdge eg : edges) {
                nodes.add(eg.getHead());
                nodes.add(eg.getRear());
                double length = eg.length();
                if (length < minEdgeLength) {
                    minEdgeLength = length;
                }
            }
        }
        List<WithPair<double[], Node>> sortedNodes = new ArrayList<>(nodes.size());
        for (Node nd : nodes) {
            sortedNodes.add(new PairPack<>(nd.getCoord(), nd));
        }
        WithPairComparator<double[], Node> nodeComparator = new WithPairComparator<>(new DoubleArrayComparator(0));
        Collections.sort(sortedNodes, nodeComparator);
        boolean noDuplication = true;
        for (int i = 0; i < sortedNodes.size() - 1; i++) {
            Node n1 = sortedNodes.get(i).getValue();
            Node n2 = sortedNodes.get(i + 1).getValue();
            if (Math2D.distance(n1.getCoord(), n2.getCoord()) < minEdgeLength / 2) {
                noDuplication = false;
            }
        }
        assertTrue(noDuplication);
    }

    public void testOppositeLength() {
        List<AdaptiveCell> sampleCells = genSamples();
        List<AdaptiveCell> fissionedCells = randomFission(sampleCells, true, getTestTime());
        for (AdaptiveCell cell : fissionedCells) {
            AdaptiveCellEdge[] edges = cell.getEdges();
            if (null == edges) {
                if (cell.getChildren() == null) {
                    assertTrue(false);
                }
                continue;
            }
            for (AdaptiveCellEdge eg : edges) {
                if (eg.opposites.size() < 1) {
                    continue;
                } else if (eg.opposites.size() == 1 && eg.getOpposite(0).opposites.size() != 1) {
                    continue;
                }
                double length = eg.length();
                double oppLengthSum = 0;
                for (AdaptiveCellEdge oppEdge : eg.opposites) {
                    double oppLength = oppEdge.length();
                    oppLengthSum += oppLength;
                    double ratio = length / oppLength;
                    assertTrue(Math.abs(ratio - Math.round(ratio)) < 0.1);
                    assertTrue(Math.round(ratio) == 1 || (Math.round(ratio)) % 2 == 0);
                }
                assertEquals(length, oppLengthSum, length * 1e-4);
            }
        }
    }

    abstract protected int getTestTime();

    protected abstract double getCellArea(AdaptiveCell cell);

    @Test
    public void testFission() {
        if (Modifier.isAbstract(getClass().getModifiers())) {
            return;
        }
        System.out.println(this.getClass() + "test fission");
        for (int maxRatio : new int[]{2, 4}) {
            AdaptiveCellEdge.MAX_SIZE_RATIO_TO_OPPOSITES = maxRatio;
            testArea();
            testDuplicatedNode();
            testOppositeLength();
        }
    }
}
