/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import net.epsilony.tsmf.util.IntIdentity;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class Boundary2D implements IntIdentity {

    public Boundary2D pred, succ;

    abstract public Node getHead();

    public Boundary2D() {
    }

    public Boundary2D(Boundary2D pred, Boundary2D succ) {
        this.pred = pred;
        this.succ = succ;
    }

    public Node getRear() {
        return succ.getHead();
    }
    public int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
