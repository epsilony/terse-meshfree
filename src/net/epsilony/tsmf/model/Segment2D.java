/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Segment2D extends GenericSegment2D<Segment2D> {

    public Segment2D() {
    }

    public Segment2D(Node head) {
        super(head);
    }

    public Segment2D(Segment2D pred, Segment2D succ) {
        super(pred, succ);
    }

    public Segment2D(Node head, Segment2D pred, Segment2D succ) {
        super(head, pred, succ);
    }

    @Override
    protected Segment2D newInstance() {
        return new Segment2D();
    }

    @Override
    protected Segment2D getThis() {
        return this;
    }
    
}
