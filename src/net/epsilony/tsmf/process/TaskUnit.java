/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TaskUnit extends QuadraturePoint {

    public double[] value;
    public boolean[] mark;

    public TaskUnit(QuadraturePoint qp, double[] value, boolean[] mark) {
        this.weight = qp.weight;
        this.coord = qp.coord;
        this.segment = qp.segment;
        this.segmentParameter = qp.segmentParameter;
        this.mark = mark;
        this.value = value;
    }
}
