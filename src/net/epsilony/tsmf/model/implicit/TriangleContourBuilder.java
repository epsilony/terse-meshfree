/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.implicit;

import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.adaptive.AdaptiveCellEdge;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.GenericFunction;
import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourBuilder {

    public List<TriangleContourCell> cells;
    public double contourLevel;
    public GenericFunction<double[], double[]> levelSetFunction;
    public LinkedList<Segment2D> contourHeads;

    public LinkedList<Segment2D> getContourHeads() {
        return contourHeads;
    }

    public void genCountour() {
        prepareCellsAndChainHeads();
        for (TriangleContourCell headCell : cells) {
            if (headCell.isVisited()) {
                continue;
            }
            setupFunctionData(headCell);
            headCell.updateStatus(contourLevel);
            Segment2D sourceEdge = headCell.getContourSourceEdge();
            if (sourceEdge == null) {
                headCell.setVisited(true);
                continue;
            }
            Segment2D chainHead = new Segment2D(genContourNode(sourceEdge));
            contourHeads.add(chainHead);
            Segment2D segment = chainHead;
            headCell.setVisited(true);
            TriangleContourCell contourCell = headCell;
            while (true) {
                contourCell = contourCell.nextContourCell();
                if (contourCell == headCell) {
                    segment.setSucc(chainHead);
                    chainHead.setPred(segment);
                    break;
                }
                if (contourCell.isVisited()) {
                    throw new IllegalStateException();
                }
                contourCell.setVisited(true);
                setupFunctionData(contourCell);
                contourCell.updateStatus(contourLevel);

                Segment2D newSucc = new Segment2D(genContourNode(contourCell.getContourSourceEdge()));
                newSucc.setPred(segment);
                segment.setSucc(newSucc);
                segment = newSucc;
            }
        }
    }

    private void setupFunctionData(TriangleContourCell headCell) {
        AdaptiveCellEdge[] edges = headCell.getEdges();
        for (int i = 0; i < edges.length; i++) {
            double[] nodeValue = headCell.getNodeValue(i);
            if (null == nodeValue) {
                headCell.setNodeValue(i, levelSetFunction.value(edges[i].getHeadCoord(), null));
            }
        }
    }

    private Node genContourNode(Segment2D contourSourceEdge) {
        double[] headCoord = contourSourceEdge.getHeadCoord();
        double[] rearCoord = contourSourceEdge.getRearCoord();
        double headValue = ((double[]) contourSourceEdge.getHead().getData())[0];
        double rearValue = ((double[]) contourSourceEdge.getRear().getData())[0];
        double t = headValue / (headValue - rearValue);
        double[] resultCoord = Math2D.pointOnSegment(headCoord, rearCoord, t, null);
        return new Node(resultCoord);
    }

    public void prepareCellsAndChainHeads() {
        for (TriangleContourCell cell : cells) {
            cell.setVisited(false);
        }
        contourHeads = new LinkedList<>();
    }
}
