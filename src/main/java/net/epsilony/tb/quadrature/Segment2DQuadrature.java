/* (c) Copyright by Man YUAN */
package net.epsilony.tb.quadrature;

import java.util.Iterator;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.tb.ArrvarFunction;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Segment2DQuadrature implements Iterable<QuadraturePoint> {

    Segment2D segment = null;
    int degree;
    double[] points;
    double[] weights;

    public void setSegment(Segment2D segment) {
        this.segment = segment;
        prepare();
    }

    public Segment2DQuadrature(Segment2D segment, int degree) {
        this.segment = segment;
        this.degree = degree;
        prepare();
    }

    public Segment2DQuadrature(int degree) {
        this.degree = degree;
        prepare();
    }

    public Segment2DQuadrature() {
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    @Override
    public Iterator<QuadraturePoint> iterator() {
        return new MyIterator();
    }

    private void prepare() {
        double[][] pws = GaussLegendre.pointsWeightsByDegree(degree);
        points = pws[0];
        weights = pws[1];
    }

    private class MyIterator implements Iterator<QuadraturePoint> {

        int nextIdx = 0;
        private double[] coordAndDifferential = new double[4];

        @Override
        public boolean hasNext() {
            return nextIdx < points.length;
        }

        @Override
        public QuadraturePoint next() {
            double point = points[nextIdx];
            double t = (point + 1) / 2;
            segment.setDiffOrder(1);
            segment.values(t, coordAndDifferential);
            double dx = coordAndDifferential[2];
            double dy = coordAndDifferential[3];
            double weight = weights[nextIdx] / 2 * (Math.sqrt(dx * dx + dy * dy));

            double x = coordAndDifferential[0];
            double y = coordAndDifferential[1];
            nextIdx++;
            QuadraturePoint result = new QuadraturePoint(weight, x, y);
            result.segment = segment;
            result.segmentParameter = t;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public double quadrate(ArrvarFunction func) {
        double res = 0;
        for (QuadraturePoint qp : this) {
            res += func.value(qp.coord) * qp.weight;
        }
        return res;
    }
}
