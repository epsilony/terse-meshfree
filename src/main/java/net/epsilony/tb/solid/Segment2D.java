/* (c) Copyright by Man YUAN */
package net.epsilony.tb.solid;

import net.epsilony.tb.IntIdentity;
import net.epsilony.tb.UnivarArrayFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface Segment2D extends IntIdentity, UnivarArrayFunction {

    Segment2D bisectionAndReturnNewSuccessor();

    Node getHead();

    double[] getHeadCoord();

    Segment2D getPred();

    Node getRear();

    double[] getRearCoord();

    Segment2D getSucc();

    void setHead(Node head);

    void setHeadCoord(double[] coord);

    void setPred(Segment2D pred);

    void setRear(Node rear);

    void setRearCoord(double[] coord);

    void setSucc(Segment2D succ);
}
