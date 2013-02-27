/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util;

import java.util.Arrays;

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

    public static double measureLength(double[] vec) {
        return Math.sqrt(dot(vec, vec));
    }

    public static double[] normalize(double[] vec, double[] result) {
        double length = measureLength(vec);
        result = scale(vec, 1 / length, result);
        return result;
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

    public static double[] scale(double[] vec, double scale, double[] result) {
        if (null == result) {
            result = new double[2];
        }
        result[0] = vec[0] * scale;
        result[1] = vec[1] * scale;
        return result;
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

    public static boolean isSegmentsIntersecting(double[] head1, double[] rear1, double[] head2, double[] rear2) {
        return isSegmentsIntersecting(head1[0], head1[1], rear1[0], rear1[1], head2[0], head2[1], rear2[0], rear2[1]);
    }

    public static boolean isSegmentsIntersecting(double h1x, double h1y, double r1x, double r1y, double h2x, double h2y, double r2x, double r2y) {
        double u1 = r1x - h1x;
        double u2 = r1y - h1y;
        double v1 = r2x - h2x;
        double v2 = r2y - h2y;
        double w1 = h2x - h1x;
        double w2 = h2y - h1y;
        double denorm = v1 * u2 - v2 * u1;
        if (0 == denorm) {// coincident or just parrel  
            if (w1 * u2 - w2 * u1 != 0) {
                return false;
            }
            double d1 = u1;
            double d2 = w1;
            double d3 = r2x - h1x;
            if (d1 == 0) {
                d1 = u2;
                d2 = w2;
                d3 = r2y - h1y;
            }
            double t = d2 / d1;
            if (t <= 1 && t >= 0) {
                return true;
            }
            t = d3 / d1;
            if (t <= 1 && t >= 0) {
                return true;
            }
            if (d2 * d3 < 0) {
                return true;
            }
            return false;

        }
        double t1 = -(v2 * w1 - v1 * w2) / denorm;
        double t2 = (u1 * w2 - u2 * w1) / denorm;
        if (t1 < 0 || t1 > 1 || t2 < 0 || t2 > 1) {
            return false;
        }
        return true;
    }

    public static double[] pointOnSegment(double[] head, double[] rear, double t, double[] result) {
        if (null == result) {
            result = new double[2];
        }
        result[0] = head[0] * (1 - t) + rear[0] * t;
        result[1] = head[1] * (1 - t) + rear[1] * t;
        return result;
    }

    public static double[] intersectionPoint(double[] headA, double[] rearA, double[] headB, double[] rearB, double[] result) {
        double deltaAx = rearA[0] - headA[0];
        double deltaAy = rearA[1] - headA[1];
        double deltaBx = rearB[0] - headB[0];
        double deltaBy = rearB[1] - headB[1];

        double crossDelta = cross(deltaAx, deltaAy, deltaBx, deltaBy);
        if (crossDelta == 0) {
            throw new IllegalArgumentException("the two segments are colinear or parrallel or one of them has zero length:\n\t"
                    + "SegA :" + Arrays.toString(headA) + "-" + Arrays.toString(rearA) + "\n\t"
                    + "SegB :" + Arrays.toString(headB) + "-" + Arrays.toString(rearB));
        }

        double uA = cross(headB[0] - headA[0], headB[1] - headA[1], deltaBx, deltaBy) / crossDelta;

        return pointOnSegment(headA, rearA, uA, result);
    }

    public static double cos(double x1, double y1, double x2, double y2) {
        return (x1 * x2 + y1 * y2) / (Math.sqrt(x1 * x1 + y1 * y1) * Math.sqrt(x2 * x2 + y2 * y2));
    }
}
