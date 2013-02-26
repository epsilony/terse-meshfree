/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model;

import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>yuan+mb@gmail.com
 */
public abstract class GenericSegment2D<T extends Boundary2D<T, Node>> extends Boundary2D<T, Node> {

    protected GenericSegment2D() {
    }

    protected GenericSegment2D(Node head) {
        super(head);
    }

    protected GenericSegment2D(T pred, T succ) {
        super(pred, succ);
    }

    protected GenericSegment2D(Node head, T pred, T succ) {
        super(head, pred, succ);
    }

    public double distanceTo(double x, double y) {
        double[] v1 = getHead().coord;
        double[] v2 = getRear().coord;
        double d12_x = v2[0] - v1[0];
        double d12_y = v2[1] - v1[1];
        double len12 = Math.sqrt(d12_x * d12_x + d12_y * d12_y);
        double d1p_x = x - v1[0];
        double d1p_y = y - v1[1];

        double project_len = Math2D.dot(d1p_x, d1p_y, d12_x, d12_y) / len12;

        if (project_len > len12) {
            double dx = x - v2[0];
            double dy = y - v2[1];
            return Math.sqrt(dx * dx + dy * dy);
        } else if (project_len < 0) {
            return Math.sqrt(d1p_x * d1p_x + d1p_y * d1p_y);
        } else {
            return Math.abs(Math2D.cross(d12_x, d12_y, d1p_x, d1p_y)) / len12;
        }
    }

    public double length() {
        double[] start = getHead().coord;
        double[] end = getRear().coord;
        double dx = start[0] - end[0];
        double dy = start[1] - end[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double[] midPoint(double[] result) {
        double[] start = getHead().coord;
        double[] end = getRear().coord;
        double x = (start[0] + end[0]) / 2;
        double y = (start[1] + end[1]) / 2;
        if (null == result) {
            return new double[]{x, y};
        } else {
            result[0] = x;
            result[1] = y;
            return result;
        }
    }

    public double[] midPoint() {
        return midPoint(null);
    }

    public T bisectionAndReturnNewSuccessor() {
        T newSucc = newInstance();
        newSucc.setHead(bisectionNode());
        newSucc.succ = this.succ;
        newSucc.pred = getThis();
        this.succ.pred = newSucc;
        this.succ = newSucc;
        return newSucc;
    }
    
    protected Node bisectionNode(){
        return new Node(midPoint());
    }

    @Override
    public String toString() {
        Node rear = getRear();
        String rearStr = null == rear ? "NULL" : rear.toString();
        return String.format("Segment2D(%d)[h:(%s), r:(%s)]", id, head, rearStr);
    }

    public boolean isStrictlyAtLeft(double[] xy) {
        double[] headCoord = getHead().coord;
        double[] rearCoord = getRear().coord;
        double dhrX = rearCoord[0] - headCoord[0];
        double dhrY = rearCoord[1] - headCoord[1];
        double dx = xy[0] - headCoord[0];
        double dy = xy[1] - headCoord[1];
        double cross = Math2D.cross(dhrX, dhrY, dx, dy);
        return cross > 0 ? true : false;
    }
}
