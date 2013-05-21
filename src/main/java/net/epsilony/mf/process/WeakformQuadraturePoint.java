/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.tb.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class WeakformQuadraturePoint extends QuadraturePoint {

    public double[] value;
    public boolean[] mark;

    public WeakformQuadraturePoint(QuadraturePoint qp, double[] value, boolean[] mark) {
        this.weight = qp.weight;
        this.coord = qp.coord;
        this.segment = qp.segment;
        this.segmentParameter = qp.segmentParameter;
        this.mark = mark;
        this.value = value;
    }
}
