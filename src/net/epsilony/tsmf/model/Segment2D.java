/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import net.epsilony.tsmf.util.IntIdentity;
import net.epsilony.tsmf.util.UnivarArrayFunction;

/**
 *
 * @author epsilon
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
