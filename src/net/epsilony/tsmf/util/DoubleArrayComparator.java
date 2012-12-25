/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util;

import java.util.Comparator;

/**
 *
 * @author Man YUAN <epsilonyuan@gmail.com>
 */
public class DoubleArrayComparator implements Comparator<double[]> {

    final int compareIndex;

    public DoubleArrayComparator(int compareIndex) {
        this.compareIndex = compareIndex;
    }

    @Override
    public int compare(double[] o1, double[] o2) {
        double d1 = o1[compareIndex];
        double d2 = o2[compareIndex];
        if (d1 == d2) {
            return 0;
        } else if (d1 < d2) {
            return -1;
        } else {
            return 1;
        }
    }
}
