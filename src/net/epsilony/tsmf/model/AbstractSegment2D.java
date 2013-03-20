/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractSegment2D implements Segment2D {

    protected int diffOrder = 0;
    protected Node head;
    public int id;
    protected Segment2D pred;
    protected Segment2D succ;

    public abstract double distanceTo(double x, double y);

    @Override
    public int getDiffOrder() {
        return diffOrder;
    }

    @Override
    public Node getHead() {
        return head;
    }

    @Override
    public double[] getHeadCoord() {
        return head.coord;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Segment2D getPred() {
        return pred;
    }

    @Override
    public Node getRear() {
        return succ.getHead();
    }

    @Override
    public double[] getRearCoord() {
        return getRear().coord;
    }

    @Override
    public Segment2D getSucc() {
        return succ;
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        if (diffOrder < 0 || diffOrder > 1) {
            throw new UnsupportedOperationException("Only support 0 and 1, not :" + diffOrder);
        }
        this.diffOrder = diffOrder;
    }

    @Override
    public void setHead(Node head) {
        this.head = head;
    }

    @Override
    public void setHeadCoord(double[] coord) {
        head.coord = coord;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setPred(Segment2D pred) {
        this.pred = pred;
    }

    @Override
    public void setRear(Node rear) {
        succ.setHead(rear);
    }

    @Override
    public void setRearCoord(double[] coord) {
        getRear().coord = coord;
    }

    @Override
    public void setSucc(Segment2D succ) {
        this.succ = succ;
    }
}
