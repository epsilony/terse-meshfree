/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

/**
 *
 * @author epsilon
 */
public class Segment2D extends Boundary2D{
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

    
    
}
