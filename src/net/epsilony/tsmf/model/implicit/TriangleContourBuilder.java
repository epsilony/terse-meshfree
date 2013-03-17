/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.implicit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.adaptive.AdaptiveCellEdge;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.LinearSegment2D;
import net.epsilony.tsmf.util.GenericFunction;
import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourBuilder {

    public static double DEFAULT_CONTOUR_LEVEL = 0;
    protected List<TriangleContourCell> cells;
    protected double contourLevel = DEFAULT_CONTOUR_LEVEL;
    protected GenericFunction<double[], double[]> levelSetFunction;
    protected List<LinearSegment2D> contourHeads;
    protected LinkedList<TriangleContourCell> openRingHeadCells;
    protected LinkedList<LinearSegment2D> openRingHeadSegments;
    protected Iterator<TriangleContourCell> cellsIterator;

    public void setCells(List<TriangleContourCell> cells) {
        this.cells = cells;
    }

    public void setContourLevel(double contourLevel) {
        this.contourLevel = contourLevel;
    }

    public void setLevelSetFunction(GenericFunction<double[], double[]> levelSetFunction) {
        this.levelSetFunction = levelSetFunction;
    }

    public void genContour() {
        prepareGenContour();
        while (true) {
            TriangleContourCell headCell = nextHeadCell();
            if (null == headCell) {
                break;
            }
            genContourFromHeadCell(headCell);
        }
    }

    public List<LinearSegment2D> getContourHeads() {
        return contourHeads;
    }

    private void prepareGenContour() {
        for (TriangleContourCell cell : cells) {
            cell.setVisited(false);
            AdaptiveCellEdge[] edges = cell.getEdges();
            for (AdaptiveCellEdge edge : edges) {
                edge.getHead().setData(null);
            }
        }
        contourHeads = new LinkedList<>();
        openRingHeadCells = new LinkedList<>();
        openRingHeadSegments = new LinkedList<>();
        cellsIterator = cells.iterator();
    }

    private TriangleContourCell nextHeadCell() {
        TriangleContourCell result = null;
        while (cellsIterator.hasNext()) {
            TriangleContourCell cell = cellsIterator.next();
            if (cell.isVisited()) {
                continue;
            }
            setupFunctionData(cell);

            LinearSegment2D sourceEdge = cell.getContourSourceEdge();
            if (sourceEdge == null) {
                cell.setVisited(true);
                continue;
            }
            result = cell;
            break;
        }
        return result;
    }

    private void genContourFromHeadCell(TriangleContourCell headCell) {
        headCell.setVisited(true);
        LinearSegment2D chainHead = new LinearSegment2D(genContourNode(headCell.getContourSourceEdge()));
        contourHeads.add(chainHead);

        openRingHeadCells.add(headCell);
        openRingHeadSegments.add(chainHead);
        TriangleContourCell contourCell = headCell;

        LinearSegment2D segment = chainHead;
        while (true) {
            TriangleContourCell nextContourCell = contourCell.nextContourCell();
            if (null == nextContourCell) {
                LinearSegment2D newSucc = new LinearSegment2D(genContourNode(contourCell.getContourDestinationEdge()));
                LinearSegment2D.link(segment, newSucc);
                break;
            } else {
                contourCell = nextContourCell;
            }

            if (contourCell == headCell) {
                LinearSegment2D.link(segment, chainHead);
                openRingHeadCells.remove(headCell);
                openRingHeadSegments.remove(chainHead);
                break;
            }

            if (contourCell.isVisited()) {
                boolean merged = tryMergeWithOpenRingHeads(contourCell, segment);
                if (merged) {
                    break;
                }
                throw new IllegalStateException();
            }

            contourCell.setVisited(true);
            setupFunctionData(contourCell);

            LinearSegment2D newSucc = new LinearSegment2D(genContourNode(contourCell.getContourSourceEdge()));
            LinearSegment2D.link(segment, newSucc);
            segment = newSucc;

        }
    }

    private void setupFunctionData(TriangleContourCell cell) {
        AdaptiveCellEdge[] edges = cell.getEdges();
        for (int i = 0; i < edges.length; i++) {
            double[] nodeValue = cell.getNodeValue(i);
            if (null == nodeValue) {
                cell.setNodeValue(i, levelSetFunction.value(edges[i].getHeadCoord(), null));
            }
        }
        cell.updateStatus(contourLevel);
    }

    private Node genContourNode(LinearSegment2D contourSourceEdge) {
        double[] headCoord = contourSourceEdge.getHeadCoord();
        double[] rearCoord = contourSourceEdge.getRearCoord();
        double headValue = ((double[]) contourSourceEdge.getHead().getData())[0];
        double rearValue = ((double[]) contourSourceEdge.getRear().getData())[0];
        double t = headValue / (headValue - rearValue);
        double[] resultCoord = Math2D.pointOnSegment(headCoord, rearCoord, t, null);
        return new Node(resultCoord);
    }

    private boolean tryMergeWithOpenRingHeads(TriangleContourCell contourCell, LinearSegment2D segment) {
        Iterator<TriangleContourCell> openHeadCellIter = openRingHeadCells.descendingIterator();
        Iterator<LinearSegment2D> openHeadSegIter = openRingHeadSegments.descendingIterator();
        boolean findAndRemove = false;
        while (openHeadCellIter.hasNext()) {
            TriangleContourCell cell = openHeadCellIter.next();
            LinearSegment2D openRingHead = openHeadSegIter.next();
            if (cell == contourCell) {
                openHeadCellIter.remove();
                openHeadSegIter.remove();
                LinearSegment2D.link(segment, openRingHead);

                contourHeads.remove(openRingHead);

                findAndRemove = true;
                break;
            }
        }
        return findAndRemove;
    }

    public List<TriangleContourCell> getCells() {
        return cells;
    }

    public double getContourLevel() {
        return contourLevel;
    }

    public GenericFunction<double[], double[]> getLevelSetFunction() {
        return levelSetFunction;
    }
}
