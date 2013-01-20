/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
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

    public static ArrayList<DoubleArrayComparator> comparatorsForAll(int size) {
        ArrayList<DoubleArrayComparator> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new DoubleArrayComparator(i));
        }
        return result;
    }
}
