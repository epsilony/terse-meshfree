/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

/**
 *
 * @author epsilon
 */
public abstract class Boundary2D {

    public Boundary2D pred, succ;

    abstract public Node getHead();

    public Boundary2D() {
    }



    public Boundary2D(Boundary2D pred, Boundary2D succ) {
        this.pred = pred;
        this.succ = succ;
    }

    public Node getRear(){
        return succ.getHead();
    }
}
