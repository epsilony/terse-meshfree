/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.model.LinearSegment2D;
import net.epsilony.tsmf.util.quadrature.QuadraturePoint;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TaskUnit extends QuadraturePoint {

    public double[] value;
    public boolean[] mark;
    public LinearSegment2D seg;

    public TaskUnit(double weight, double[] coord, LinearSegment2D seg, double[] value, boolean[] mark) {
        this.weight = weight;
        this.coord = coord;
        this.seg = seg;
        this.value = value;
        this.mark = mark;
    }
}
