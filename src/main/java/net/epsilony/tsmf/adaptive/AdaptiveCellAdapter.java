/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.adaptive;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AdaptiveCellAdapter<T extends AdaptiveCellAdapter> implements AdaptiveCell {

    protected T[] children;
    protected AdaptiveCellEdge[] edges;

    protected void bisectionAllEdges() {
        for (int i = 0; i < edges.length; i++) {
            edges[i].bisectionAndReturnNewSuccessor();
        }
    }

    @Override
    public AdaptiveCell findOneFissionObstrutor() {
        if (null != children) {
            throw new IllegalStateException();
        }
        for (AdaptiveCellEdge eg : edges) {
            if (!eg.isAbleToBisection()) {
                return eg.getOpposite(0).getOwner();
            }
        }
        return null;
    }

    @Override
    public T[] getChildren() {
        return children;
    }

    @Override
    public AdaptiveCellEdge[] getEdges() {
        return edges;
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
    public boolean isAbleToFusionFromChildren() {
        if (null == children) {
            return false;
        }
        for (int i = 0; i < children.length; i++) {
            int child_index = i;
            int child_edge_index = i;
            int succ_child_index = (i + 1) % children.length;
            int succ_child_edge_index = i;
            if (!children[child_index].edges[child_edge_index].
                    isAbleToMerge(children[succ_child_index].edges[succ_child_edge_index])) {
                return false;
            }
        }
        return true;
    }

    public void setEdges(AdaptiveCellEdge[] edges) {
        if (edges.length != getNumOfEdges()) {
            throw new IllegalArgumentException("number of input edges must be " + getNumOfEdges());
        }
        this.edges = edges;
        for (AdaptiveCellEdge eg : edges) {
            eg.setOwner(this);
        }
        linkEdges();
    }

    private void linkEdges() {
        final int numOfEdges = getNumOfEdges();
        for (int i = 0; i < edges.length; i++) {
            edges[i].setSucc(edges[(i + 1) % numOfEdges]);
            edges[(i + 1) % numOfEdges].setPred(edges[i]);
        }
    }

    abstract protected int getNumOfEdges();

    abstract protected int getNumOfChildren();

    protected abstract void createChildrenFromBisectionedEdges();

    protected abstract void fillInnerOppositeForNewChildren();

    @Override
    public void fissionToChildren() {
        if (!isAbleToFissionToChildren()) {
            throw new IllegalStateException();
        }
        bisectionAllEdges();
        createChildrenFromBisectionedEdges();
        fillInnerOppositeForNewChildren();
        edges = null;
    }
}
