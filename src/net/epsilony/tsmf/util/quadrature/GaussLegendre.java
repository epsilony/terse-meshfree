/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.quadrature;

import static java.lang.Math.sqrt;
import java.util.Arrays;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GaussLegendre {

    public static final int MAXPOINTS = 5;
    public static final int MINPOINTS = 1;
    private static final double[][] points = new double[][]{
        {0},
        {-sqrt(3) / 3, sqrt(3) / 3},
        {-sqrt(15) / 5, 0, sqrt(15) / 5},
        {-sqrt(525 + 70 * sqrt(30)) / 35, -sqrt(525 - 70 * sqrt(30)) / 35, sqrt(525 - 70 * sqrt(30)) / 35, sqrt(525 + 70 * sqrt(30)) / 35},
        {-sqrt(245 + 14 * sqrt(70)) / 21, -sqrt(245 - 14 * sqrt(70)) / 21, 0, sqrt(245 - 14 * sqrt(70)) / 21, sqrt(245 + 14 * sqrt(70)) / 21}};
    private static final double[][] weights = new double[][]{
        {2},
        {1, 1},
        {5 / 9d, 8 / 9d, 5 / 9d},
        {(18 - sqrt(30)) / 36, (18 + sqrt(30)) / 36, (18 + sqrt(30)) / 36, (18 - sqrt(30)) / 36},
        {(322 - 13 * sqrt(70)) / 900, (322 + 13 * sqrt(70)) / 900, 128 / 225d, (322 + 13 * sqrt(70)) / 900, (322 - 13 * sqrt(70)) / 900}};

    public static boolean isNumInDomain(int n) {
        if (n < MINPOINTS || n > MAXPOINTS) {
            throw new UnsupportedOperationException("The quadrature points number:" + n + " is not supported yet");
        }

        return true;
    }

    public static double[][] pointsWeightsByDegree(int degree) {
        int num = (int) Math.ceil((degree + 1) / 2.0);
        return pointsWeightsByNum(num);
    }

    public static double[][] pointsWeightsByNum(int num) {
        isNumInDomain(num);
        return new double[][]{Arrays.copyOf(points[num - 1], num), Arrays.copyOf(weights[num - 1], num)};
    }
}
