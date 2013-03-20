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
    public Segment2D segment;

    public TaskUnit(double weight, double[] coord, Segment2D seg, double[] value, boolean[] mark) {
        this.weight = weight;
        this.coord = coord;
        this.segment = seg;
        this.value = value;
        this.mark = mark;
    }
}
