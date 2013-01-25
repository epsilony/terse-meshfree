/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.quadrature;

import java.util.Iterator;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.ArrvarFunction;

/**
 *
 * @author epsilon
 */
public class Segment2DQuadrature implements Iterable<QuadraturePoint> {

    Segment2D segment = null;
    double segLen;
    int degree;
    double[] points;
    double[] weights;

    private void _setSegment(Segment2D segment) {
        this.segment = segment;
        segLen = segment.length();
    }

    public Segment2DQuadrature(Segment2D segment, int degree) {
        _setSegment(segment);
        _setDegree(degree);
    }

    public Segment2DQuadrature(int degree) {
        _setDegree(degree);
    }

    private void _setDegree(int degree) {
        double[][] pws = GaussLegendre.pointsWeightsByDegree(degree);
        points = pws[0];
        weights = pws[1];
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    public void setSegment(Segment2D segment) {
        _setSegment(segment);
    }

    public void setDegree(int degree) {
        _setDegree(degree);
    }

    @Override
    public Iterator<QuadraturePoint> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<QuadraturePoint> {

        int nextIdx = 0;

        @Override
        public boolean hasNext() {
            return nextIdx < points.length;
        }

        @Override
        public QuadraturePoint next() {
            double weight = weights[nextIdx] / 2 * segLen;
            double point = points[nextIdx];
            double[] cHead = segment.getHead().coord;
            double[] cRear = segment.getRear().coord;
            double p = (point + 1) / 2;
            double x = cHead[0] * (1 - p) + cRear[0] * p;
            double y = cHead[1] * (1 - p) + cRear[1] * p;
            nextIdx++;
            return new QuadraturePoint(weight, x, y);
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
