/* (c) Copyright by Man YUAN */
package net.epsilony.tb.adaptive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.epsilony.tb.solid.LinearSegment2D;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.tb.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class AdaptiveCellEdge extends LinearSegment2D {

    public static int DEFAULT_MAX_SIZE_RATIO_TO_OPPOSITES = 2;
    protected int maxSizeRatioToOpposites = DEFAULT_MAX_SIZE_RATIO_TO_OPPOSITES;
    protected List<AdaptiveCellEdge> opposites = new ArrayList<>(maxSizeRatioToOpposites);
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
        if (maxSizeRatioToOpposites > 2) {
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
            opposites = new ArrayList<>(maxSizeRatioToOpposites);
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
        } else if (numOpposites() == maxSizeRatioToOpposites) {
            return getOpposite(maxSizeRatioToOpposites / 2 - 1).getHead();
        } else {
            double[] midPoint = Math2D.pointOnSegment(head.getCoord(), getRear().getCoord(), 0.5, null);
            Node midNode = null;
            double lengthErr = length() / (1 + maxSizeRatioToOpposites);
            for (int i = 0; i < numOpposites() - 1; i++) {
                if (Math2D.distance(midPoint, opposites.get(i).getHead().getCoord()) < lengthErr) {
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
            if (opposite.numOpposites() >= maxSizeRatioToOpposites) {
                return false;
            } else if (maxSizeRatioToOpposites == 2) {
                return true;
            } else if (opposite.length() / length() > maxSizeRatioToOpposites - 0.1) {
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
        return (AdaptiveCellEdge) super.getPred();
    }

    @Override
    public void setPred(Segment2D pred) {
        super.setPred((AdaptiveCellEdge) pred);
    }

    @Override
    public AdaptiveCellEdge getSucc() {
        return (AdaptiveCellEdge) super.getSucc();
    }

    @Override
    public void setSucc(Segment2D succ) {
        super.setSucc((AdaptiveCellEdge) succ);
    }

    public void addOppositesTo(Collection<? super AdaptiveCellEdge> output) {
        output.addAll(opposites);
    }

    public int getMaxSizeRatioToOpposites() {
        return maxSizeRatioToOpposites;
    }

    public void setMaxSizeRatioToOpposites(int maxSizeRatioToOpposites) {
        this.maxSizeRatioToOpposites = maxSizeRatioToOpposites;
    }
}