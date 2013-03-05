/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.adaptive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class AdaptiveCellEdge extends Segment2D {

    public static int MAX_SIZE_RATIO_TO_OPPOSITES = 2;
    protected List<AdaptiveCellEdge> opposites = new ArrayList<>(MAX_SIZE_RATIO_TO_OPPOSITES);
    protected AdaptiveCell owner;

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
        if (MAX_SIZE_RATIO_TO_OPPOSITES > 2) {
            return bisectionAndReturnNewSuccessorWithHighSizeRatioLimit();
        }

        AdaptiveCellEdge newSucc = (AdaptiveCellEdge) super.bisectionAndReturnNewSuccessor();

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

    private AdaptiveCellEdge bisectionAndReturnNewSuccessorWithHighSizeRatioLimit() {
        AdaptiveCellEdge newSucc = (AdaptiveCellEdge) super.bisectionAndReturnNewSuccessor();

        if (numOpposites() == 1) {
            int index = getOpposite(0).opposites.indexOf(this);
            getOpposite(0).addOpposite(index, newSucc);
            newSucc.addOpposite(0, getOpposite(0));
        } else {
            List<AdaptiveCellEdge> oppositesBak = opposites;
            opposites = new ArrayList<>(MAX_SIZE_RATIO_TO_OPPOSITES);
            boolean succOpposite = false;
            Node newMidNode = getRear();
            for (AdaptiveCellEdge oppEdge : oppositesBak) {
                if (oppEdge.getRear() == newMidNode) {
                    succOpposite = true;
                }
                if (succOpposite) {
                    oppEdge.setOpposite(0, newSucc);
                    newSucc.addOpposite(oppEdge);
                } else {
                    oppEdge.setOpposite(0, this);
                    addOpposite(oppEdge);
                }
            }
        }
        return newSucc;
    }

    @Override
    protected Node bisectionNode() {
        if (numOpposites() <= 1) {
            return super.bisectionNode();
        } else if (numOpposites() == MAX_SIZE_RATIO_TO_OPPOSITES) {
            return getOpposite(MAX_SIZE_RATIO_TO_OPPOSITES / 2 - 1).getHead();
        } else {
            double[] midPoint = Math2D.pointOnSegment(head.coord, getRear().coord, 0.5, null);
            Node midNode = null;
            double lengthErr = length() / (1 + MAX_SIZE_RATIO_TO_OPPOSITES);
            for (int i = 0; i < numOpposites() - 1; i++) {
                if (Math2D.distance(midPoint, opposites.get(i).getHead().coord) < lengthErr) {
                    midNode = opposites.get(i).getHead();
                    break;
                }
            }
            return midNode;
        }
    }

    public boolean isAbleToBisection() {
        if (numOpposites() == 1) {
            AdaptiveCellEdge opposite = getOpposite(0);
            if (opposite.numOpposites() >= MAX_SIZE_RATIO_TO_OPPOSITES) {
                return false;
            } else if (MAX_SIZE_RATIO_TO_OPPOSITES == 2) {
                return true;
            } else if (opposite.length() / length() > MAX_SIZE_RATIO_TO_OPPOSITES - 0.1) {
                return false;
            }
        }
        return true;
    }

    public void mergeWithGivenSuccessor(AdaptiveCellEdge successor) {
        if (!isAbleToMerge(successor)) {
            throw new IllegalStateException();
        }
        this.succ = successor.succ;
        this.succ.setPred(this);
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

    public AdaptiveCell getOwner() {
        return owner;
    }

    public void setOwner(AdaptiveCell owner) {
        this.owner = owner;
    }

    public void addOpposite(int index, AdaptiveCellEdge element) {
        opposites.add(index, element);
    }

    public void addOpposite(AdaptiveCellEdge oppositeEdge) {
        opposites.add(oppositeEdge);
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
    public AdaptiveCellEdge getPred() {
        return (AdaptiveCellEdge) super.getPred(); //To change body of generated methods, choose Tools | Templates.
    }

    public void setPred(AdaptiveCellEdge pred) {
        super.setPred(pred); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPred(Segment2D pred) {
        super.setPred((AdaptiveCellEdge) pred);
    }

    @Override
    public AdaptiveCellEdge getSucc() {
        return (AdaptiveCellEdge) super.getSucc(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSucc(Segment2D succ) {
        super.setSucc((AdaptiveCellEdge) succ); //To change body of generated methods, choose Tools | Templates.
    }

    public void setSucc(AdaptiveCellEdge succ) {
        super.setSucc(succ);
    }

    public void addOppositesTo(Collection<? super AdaptiveCellEdge> output) {
        output.addAll(opposites);
    }
}