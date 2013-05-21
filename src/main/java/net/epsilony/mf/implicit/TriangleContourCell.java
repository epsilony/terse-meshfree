/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit;

import net.epsilony.tb.adaptive.TriangleAdaptiveCell;
import net.epsilony.tb.solid.LinearSegment2D;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.IntIdentityMap;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourCell extends TriangleAdaptiveCell {

    private static final int[] STATUS_CONTOUR_SRC_EDGE_INDEX_MAP = new int[]{-1, 0, 1, 1, 2, 0, 2, -1};
    private static final int[] STATUS_CONTOUR_DEST_EDGE_INDEX_MAP = new int[]{-1, 2, 0, 2, 1, 1, 0, -1};
    boolean visited = false;
    private int status = -1;

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean polygonized) {
        this.visited = polygonized;
    }

    public void updateStatus(double contourLevel, IntIdentityMap<Node, double[]> nodesValuesMap) {
        double w = 1;
        status = 0;
        for (int i = 0; i < getEdges().length; i++) {
            double[] funcValues = nodesValuesMap.get(edges[i].getHead());
            if (funcValues[0] >= contourLevel) {
                status += w;
            }
            w *= 2;
        }
    }

    public LinearSegment2D getContourSourceEdge() {
        if (status < 0) {
            throw new IllegalStateException("status haven't been updated");
        }
        int index = STATUS_CONTOUR_SRC_EDGE_INDEX_MAP[status];
        if (index < 0) {
            return null;
        }
        return getEdges()[index];
    }

    public LinearSegment2D getContourDestinationEdge() {
        if (status < 0) {
            throw new IllegalStateException("status haven't been updated");
        }
        int index = STATUS_CONTOUR_DEST_EDGE_INDEX_MAP[status];
        if (index < 0) {
            return null;
        }
        return getEdges()[index];
    }

    public TriangleContourCell nextContourCell() {
        if (status < 0) {
            throw new IllegalStateException("status haven't been updated");
        }
        int index = STATUS_CONTOUR_DEST_EDGE_INDEX_MAP[status];
        if (index < 0) {
            return null;
        }
        int numOpposites = getEdges()[index].numOpposites();
        if (numOpposites == 0) {
            return null;
        }
        if (numOpposites > 1) {
            throw new IllegalStateException("Unsupported");
        }
        return (TriangleContourCell) getEdges()[index].getOpposite(0).getOwner();
    }

    public Node getNode(int index) {
        return edges[index].getHead();
    }
}
