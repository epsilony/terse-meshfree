/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.adaptive;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleAdaptiveCell extends AdaptiveCellAdapter<TriangleAdaptiveCell> {

    public static final int NUM_OF_EDGES = 3;
    public static final int NUM_OF_CHILDREN = 4;

    @Override
    protected void createChildrenFromBisectionedEdges() {
        children = new TriangleAdaptiveCell[NUM_OF_CHILDREN];

        for (int i = 0; i < NUM_OF_EDGES; i++) {
            TriangleAdaptiveCell cell = new TriangleAdaptiveCell();
            children[i] = cell;
            AdaptiveCellEdge[] childEdges = new AdaptiveCellEdge[NUM_OF_EDGES];
            childEdges[i] = edges[i];
            childEdges[(i + 1) % NUM_OF_EDGES] = new AdaptiveCellEdge(edges[i].getRear());
            childEdges[(i + 2) % NUM_OF_EDGES] = edges[i].pred;
            cell.setEdges(childEdges);
        }

        children[3] = new TriangleAdaptiveCell();
        children[3].setEdges(new AdaptiveCellEdge[]{
                    new AdaptiveCellEdge(children[0].edges[2].getHead()),
                    new AdaptiveCellEdge(children[1].edges[0].getHead()),
                    new AdaptiveCellEdge(children[2].edges[1].getHead())
                });
    }

    @Override
    protected void fillInnerOppositeForNewChildren() {
        for (int i = 0; i < NUM_OF_EDGES; i++) {
            AdaptiveCellEdge outerEdge = children[i].edges[(i + 1) % NUM_OF_EDGES];
            AdaptiveCellEdge innerEdge = children[3].edges[i];
            outerEdge.addOpposite(innerEdge);
            innerEdge.addOpposite(outerEdge);
        }
    }

    @Override
    public void fusionFromChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
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
