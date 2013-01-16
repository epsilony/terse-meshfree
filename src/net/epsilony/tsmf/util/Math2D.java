/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util;

/**
 *
 * @author <a href="mailto:epsionyuan@gmail.com">Man YUAN</a>
 */
public class Math2D {

    public static double dot(double[] v1, double[] v2) {
        return v1[0] * v2[0] + v1[1] * v2[1];
    }

    public static double dot(double x1, double y1, double x2, double y2) {
        return x1 * x2 + y1 * y2;
    }

    public static double cross(double[] v1, double[] v2) {
        return v1[0] * v2[1] - v1[1] * v2[0];
    }

    public static double cross(double x1, double y1, double x2, double y2) {
        return x1 * y2 - y1 * x2;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(double[] xy1, double[] xy2) {
        return distance(xy1[0], xy1[1], xy2[0], xy2[1]);
    }

    public static double[] distanceAndPartDiffs(double x, double y, double x0, double y0, double[] results) {
        if (null == results) {
            results = new double[3];
        }
        double dst = distance(x, y, x0, y0);
        results[0] = dst;
        if (dst != 0) {
            results[1] = (x - x0) / dst;
            results[2] = (y - y0) / dst;
        } else {
            results[1] = 0;
            results[2] = 0;
        }
        return results;
    }

    public static double[] distanceAndPartDiffs(double[] xy, double[] to, double[] results) {
        return distanceAndPartDiffs(xy[0], xy[1], to[0], to[1], results);
    }

    public static double[] subs(double[] v1, double[] v2, double[] results) {
        if (results == null) {
            results = new double[]{v1[0] - v2[0], v1[1] - v2[1]};
        } else {
            results[0] = v1[0] - v2[0];
            results[1] = v1[1] - v2[1];
        }
        return results;
    }

    public static double triangleArea(double x1, double y1, double x2, double y2, double x3, double y3) {
        return 0.5 * cross(x2 - x1, y2 - y1, x3 - x1, y3 - y1);
    }
}
