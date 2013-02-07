/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.GenericSegment2D;
import net.epsilony.tsmf.model.Node;

/**
 *
 * @author epsilon
 */
public class AdaptiveCellEdge extends GenericSegment2D<AdaptiveCellEdge> {

    public static final int DEFAULT_MAX_SIZE_OF_OPPOSITES = 2;
    public List<AdaptiveCellEdge> opposites = new ArrayList<>(DEFAULT_MAX_SIZE_OF_OPPOSITES);

    public AdaptiveCellEdge() {
    }

    public AdaptiveCellEdge(Node head) {
        super(head);
    }

    public int numOpposites() {
        return opposites.size();
    }

    @Override
    public AdaptiveCellEdge bisectionAndReturnNewSuccessor() {
        if (!isAbleToBisection()) {
            throw new IllegalStateException();
        }
        AdaptiveCellEdge newSucc = super.bisectionAndReturnNewSuccessor();

        if (numOpposites() == 1) {
            getOpposite(0).addOpposite(0, newSucc);
            newSucc.addOpposite(0, getOpposite(0));
        } else if (numOpposites() == 2) {
            getOpposite(1).setOpposite(0, newSucc);
            newSucc.addOpposite(0, getOpposite(1));
            this.removeOpposite(1);
        }
        return newSucc;
    }

    @Override
    protected Node bisectionNode() {
        if (numOpposites() == 1) {
            return super.bisectionNode();
        } else {
            return getOpposite(0).getHead();
        }
    }

    public boolean isAbleToBisection() {
        if (numOpposites() == 1 && getOpposite(0).numOpposites() > 1) {
            return false;
        }
        return true;
    }

    public void mergeSuccessor(AdaptiveCellEdge successor) {
        if (!isAbleToMerge(successor)) {
            throw new IllegalStateException();
        }
        this.succ = successor.succ;
        this.succ.pred = this;
        if (numOpposites() == 0) {
            return;
        }
        if (getOpposite(0).numOpposites() == 2) {
            getOpposite(0).removeOpposite(0);
        } else {
            successor.getOpposite(0).setOpposite(0, this);
            this.addOpposite(1, successor.getOpposite(0));
        }
    }

    public boolean isAbleToMerge(AdaptiveCellEdge successor) {
        if (getRear() != successor.getHead() || numOpposites() > 1 || successor.numOpposites() > 1) {
            return false;
        } else {
            return true;
        }
    }

    public List<AdaptiveCellEdge> getOpposites() {
        return opposites;
    }

    public void addOpposite(int index, AdaptiveCellEdge element) {
        opposites.add(index, element);
    }

    public AdaptiveCellEdge removeOpposite(int index) {
        return opposites.remove(index);
    }

    public AdaptiveCellEdge getOpposite(int index) {
        return opposites.get(index);
    }

    public AdaptiveCellEdge setOpposite(int index, AdaptiveCellEdge element) {
        return opposites.set(index, element);
    }

    @Override
    protected AdaptiveCellEdge newInstance() {
        return new AdaptiveCellEdge();
    }

    @Override
    protected AdaptiveCellEdge getThis() {
        return this;
    }
}
