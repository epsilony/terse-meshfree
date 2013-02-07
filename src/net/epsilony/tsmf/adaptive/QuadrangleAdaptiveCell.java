/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive;

import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author epsilon
 */
public class QuadrangleAdaptiveCell implements AdaptiveCell<QuadrangleAdaptiveCell> {

    private final int NUM_OF_EDGES = 4;
    private final int NUM_OF_CHILDREN = 4;
    QuadrangleAdaptiveCell[] children;
    AdaptiveCellEdge[] edges;

    private QuadrangleAdaptiveCell(AdaptiveCellEdge[] edges) {
        this.edges = edges;
    }

    @Override
    public void fissionToChildren() {
        if (!isAbleToFissionToChildren()) {
            throw new IllegalStateException();
        }
        AdaptiveCellEdge[] newEdges = new AdaptiveCellEdge[NUM_OF_EDGES];
        for (int i = 0; i < edges.length; i++) {
            newEdges[i] = edges[i].bisectionAndReturnNewSuccessor();
        }
        Node centerNode = centerNodeForFission(edges);
        children = new QuadrangleAdaptiveCell[NUM_OF_CHILDREN];
        for (int i = 0; i < children.length; i++) {
            AdaptiveCellEdge[] newChildEdges = new AdaptiveCellEdge[NUM_OF_EDGES];
            newChildEdges[i] = edges[i];
            newChildEdges[(i - 1) % NUM_OF_EDGES] = newEdges[(i - 1) % NUM_OF_EDGES];
            newChildEdges[(i + 1) % NUM_OF_EDGES] = new AdaptiveCellEdge(edges[i].getRear());
            newChildEdges[(i + 2) % NUM_OF_EDGES] = new AdaptiveCellEdge(centerNode);
            for (int j = 0; j < newChildEdges.length; j++) {
                newChildEdges[j].succ = newChildEdges[(j + 1) % NUM_OF_EDGES];
                newChildEdges[(j + 1) % NUM_OF_EDGES].pred = newChildEdges[j];
            }
            children[i] = new QuadrangleAdaptiveCell(newChildEdges);
        }
        edges = null;
    }

    private static Node centerNodeForFission(AdaptiveCellEdge[] bisectionedEdges) {
        double[] centerCoord = Math2D.intersectionPoint(
                bisectionedEdges[0].getRear().coord,
                bisectionedEdges[2].getRear().coord,
                bisectionedEdges[1].getRear().coord,
                bisectionedEdges[3].getRear().coord, null);
        return new Node(centerCoord);
    }

    @Override
    public boolean isAbleToFissionToChildren() {
        if (null != children) {
            return false;
        }
        for (AdaptiveCellEdge eg : edges) {
            if (!eg.isAbleToBisection()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void fusionFromChildren() {
        if (!isAbleToFusionFromChildren()) {
            throw new IllegalStateException();
        }
        edges = new AdaptiveCellEdge[NUM_OF_EDGES];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = children[i].edges[i];
            edges[i].mergeSuccessor(children[(i + 1) % NUM_OF_EDGES].edges[i]);
        }
        children = null;
    }

    @Override
    public boolean isAbleToFusionFromChildren() {
        if (null == children) {
            return false;
        }
        for (int i = 0; i < children.length; i++) {
            int child_index = i;
            int child_edge_index = i;
            int succ_child_index = (i + 1) % children.length;
            int succ_child_edge_index = i;
            if (!children[child_index].edges[child_edge_index].isAbleToMerge(children[succ_child_index].edges[succ_child_edge_index])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public QuadrangleAdaptiveCell[] getChildren() {
        return children;
    }
}
