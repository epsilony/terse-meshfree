/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model;

import net.epsilony.tsmf.util.IntIdentity;
import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Segment2D implements IntIdentity {

    protected Segment2D pred;
    protected Segment2D succ;
    public Node head;
    public int id;

    public Segment2D() {
    }

    public Segment2D(Node head) {
        this.head = head;
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

    public Segment2D bisectionAndReturnNewSuccessor() {
        Segment2D newSucc = newInstance();
        newSucc.setHead(bisectionNode());
        newSucc.succ = this.succ;
        newSucc.pred = this;
        this.succ.pred = newSucc;
        this.succ = newSucc;
        return newSucc;
    }

    protected Node bisectionNode() {
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

    public double[] outNormal() {
        double[] result = Math2D.subs(getRear().coord, head.coord, null);
        Math2D.normalize(result, result);
        return result;
    }

    public double[] getHeadCoord() {
        return head.coord;
    }

    public double[] getRearCoord() {
        return getRear().coord;
    }

    public void setHeadCoord(double[] coord) {
        head.coord = coord;
    }

    public void setRearCoord(double[] coord) {
        getRear().coord = coord;
    }

    protected Segment2D newInstance() {
        return new Segment2D();
    }

    public Segment2D getPred() {
        return pred;
    }

    public void setPred(Segment2D pred) {
        this.pred = pred;
    }

    public Segment2D getSucc() {
        return succ;
    }

    public void setSucc(Segment2D succ) {
        this.succ = succ;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Node getRear() {
        return succ.getHead();
    }

    public void setRear(Node rear) {
        succ.setHead(rear);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
