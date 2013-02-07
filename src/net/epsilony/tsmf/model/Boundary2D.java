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
public abstract class Boundary2D<T extends Boundary2D<T, N>, N> implements IntIdentity {

    public T pred, succ;
    public N head;

    protected Boundary2D() {
    }

    protected Boundary2D(T pred, T succ) {
        this.pred = pred;
        this.succ = succ;
    }

    public Boundary2D(N head) {
        this.head = head;
    }

    protected Boundary2D(N head, T pred, T succ) {
        this.head = head;
        this.pred = pred;
        this.succ = succ;
    }

    abstract protected T newInstance();

    abstract protected T getThis();

    public T getPred() {
        return pred;
    }

    public void setPred(T pred) {
        this.pred = pred;
    }

    public T getSucc() {
        return succ;
    }

    public void setSucc(T succ) {
        this.succ = succ;
    }

    public N getHead() {
        return head;
    }

    public void setHead(N head) {
        this.head = head;
    }

    public N getRear() {
        return succ.getHead();
    }

    public void setRear(N rear) {
        succ.setHead(rear);
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
