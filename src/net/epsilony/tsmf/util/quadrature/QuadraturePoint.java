/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.quadrature;

import net.epsilony.tsmf.model.Segment2D;

/**
 *
 * @author <a href="mailto:epsionyuan@gmail.com">Man YUAN</a>
 */
public class QuadraturePoint {

    public double weight;
    public double[] coord;
    public Segment2D segment;
    public double segmentParameter;

    QuadraturePoint(double weight, double x, double y) {
        this.weight = weight;
        coord = new double[]{x, y};
    }

    public QuadraturePoint() {
        coord = new double[2];
    }

    public QuadraturePoint(int dim) {
        coord = new double[dim];
    }
}
