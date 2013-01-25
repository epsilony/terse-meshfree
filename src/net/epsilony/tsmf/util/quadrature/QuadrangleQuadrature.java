/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.quadrature;

import java.util.Iterator;
import net.epsilony.tsmf.util.ArrvarFunction;

/**
 *
 * @author epsilon
 */
public class QuadrangleQuadrature implements Iterable<QuadraturePoint> {

    public static double[] uv2xy(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double u, double v, double[] results) {
        if (null == results) {
            results = new double[3];
        }
        double p = (u + 1) / 2;
        double q = (v + 1) / 2;

        double uv = p * q;
        double tx = x1 - x2 + x3 - x4;
        double ty = y1 - y2 + y3 - y4;
        results[0] = tx * uv + (x2 - x1) * p + (x4 - x1) * q + x1;     //x
        results[1] = ty * uv + (y2 - y1) * p + (y4 - y1) * q + y1;    //y
        if (results.length > 2) {
            double dxdu = (tx * q + (x2 - x1)) / 2;
            double dxdv = (tx * p + (x4 - x1)) / 2;
            double dydu = (ty * q + (y2 - y1)) / 2;
            double dydv = (ty * p + (y4 - y1)) / 2;
            results[2] = Math.abs(dxdu * dydv - dydu * dxdv);     //|Jacobi|
        }
        return results;
    }
    int degree;
    double x1, y1, x2, y2, x3, y3, x4, y4;
    double[] weights;
    double[] us_or_vs;

    public void setQuadrangle(double[] xys) {
        setQuadrangle(xys[0], xys[1], xys[2], xys[3], xys[4], xys[5], xys[6], xys[7]);
    }

    public void setQuadrangle(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.x4 = x4;
        this.y4 = y4;
    }

    public void setDegree(int degree) {
        this.degree = degree;
        double[][] pointsWeightsByDegree = GaussLegendre.pointsWeightsByDegree(degree);
        us_or_vs = pointsWeightsByDegree[0];
        weights = pointsWeightsByDegree[1];
    }

    public int getDegree() {
        return degree;
    }

    public double quadrate(ArrvarFunction func) {
        double res = 0;
        for (QuadraturePoint qp : this) {
            res += func.value(qp.coord) * qp.weight;
        }
        return res;
    }

    @Override
    public Iterator<QuadraturePoint> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator<QuadraturePoint> {

        int u_index = 0;
        int v_index = 0;
        double[] xyJ = new double[3];

        @Override
        public boolean hasNext() {
            return u_index < weights.length;
        }

        @Override
        public QuadraturePoint next() {
            double u = us_or_vs[u_index];
            double v = us_or_vs[v_index];
            double u_w = weights[u_index];
            double v_w = weights[v_index];
            uv2xy(x1, y1, x2, y2, x3, y3, x4, y4, u, v, xyJ);
            double w = u_w * v_w * xyJ[2];
            v_index++;
            if (v_index >= weights.length) {
                u_index++;
                v_index = 0;
            }
            return new QuadraturePoint(w, xyJ[0], xyJ[1]);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
