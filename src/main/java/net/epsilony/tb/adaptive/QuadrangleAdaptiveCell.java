/* (c) Copyright by Man YUAN */
package net.epsilony.tb.adaptive;

import net.epsilony.tb.solid.Node;
import net.epsilony.tb.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class QuadrangleAdaptiveCell extends AdaptiveCellAdapter<QuadrangleAdaptiveCell> {

    public static final int NUM_OF_EDGES = 4;
    public static final int NUM_OF_CHILDREN = 4;

    public QuadrangleAdaptiveCell() {
    }

    @Override
    protected void createChildrenFromBisectionedEdges() {
        Node centerNode = centerNodeForFission(edges);
        children = new QuadrangleAdaptiveCell[NUM_OF_CHILDREN];
        for (int i = 0; i < children.length; i++) {
            AdaptiveCellEdge[] newChildEdges = new AdaptiveCellEdge[NUM_OF_EDGES];
            newChildEdges[i] = edges[i];
            newChildEdges[(i + 3) % NUM_OF_EDGES] = edges[i].getPred();
            newChildEdges[(i + 1) % NUM_OF_EDGES] = new AdaptiveCellEdge(edges[i].getRear());
            newChildEdges[(i + 2) % NUM_OF_EDGES] = new AdaptiveCellEdge(centerNode);
            children[i] = new QuadrangleAdaptiveCell();
            children[i].setEdges(newChildEdges);
        }
    }

    private static Node centerNodeForFission(AdaptiveCellEdge[] bisectionedEdges) {
        double[] centerCoord = Math2D.intersectionPoint(
                bisectionedEdges[0].getRearCoord(),
                bisectionedEdges[2].getRearCoord(),
                bisectionedEdges[1].getRearCoord(),
                bisectionedEdges[3].getRearCoord(), null);
        return new Node(centerCoord);
    }

    @Override
    protected void fillInnerOppositeForNewChildren() {
        for (int i = 0; i < edges.length; i++) {
            children[i].edges[(i + 1) % NUM_OF_EDGES].opposites.add(
                    children[(i + 1) % NUM_OF_EDGES].edges[(i + 3) % NUM_OF_EDGES]);
            children[(i + 1) % NUM_OF_EDGES].edges[(i + 3) % NUM_OF_EDGES].opposites.add(
                    children[i].edges[(i + 1) % NUM_OF_EDGES]);
        }
    }

    @Override
    public void fusionFromChildren() {
        if (!isAbleToFusionFromChildren()) {
            throw new IllegalStateException();
        }
        edges = new AdaptiveCellEdge[NUM_OF_EDGES];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = children[i].edges[i];
            edges[i].mergeWithGivenSuccessor(children[(i + 1) % NUM_OF_EDGES].edges[i]);
        }
        children = null;
    }

    @Override
    protected int getNumOfEdges() {
        return NUM_OF_EDGES;
    }

    @Override
    protected int getNumOfChildren() {
        return NUM_OF_CHILDREN;
    }
}
