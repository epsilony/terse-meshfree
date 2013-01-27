/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.quadrature.QuadraturePoint;

/**
 *
 * @author epsilon
 */
public class ProcessPoint extends QuadraturePoint {

    public double[] value;
    public boolean[] mark;
    public Segment2D seg;

    public ProcessPoint(double weight, double[] coord, Segment2D seg, double[] value, boolean[] mark) {
        this.weight = weight;
        this.coord = coord;
        this.seg = seg;
        this.value = value;
        this.mark = mark;
    }
}