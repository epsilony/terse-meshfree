/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author epsilonyuan+mb@gmail.com
 */
public class Segment2D extends Boundary2D {

    public Node head;

    @Override
    public Node getHead() {
        return head;
    }

    public Segment2D(Node head) {
        this.head = head;
    }

    public Segment2D(Node head, Boundary2D pred, Boundary2D succ) {
        super(pred, succ);
        this.head = head;
    }

    public Segment2D() {
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
}
