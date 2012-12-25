/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util;

import java.util.List;
import java.util.Random;

/**
 *
 * @author Man YUAN <epsilonyuan@gmail.com>
 */
public class TestTool {

    public static double[] linSpace(double start, double end, int numPt) {
        double d = end - start;
        double[] result = new double[numPt];
        double numD = numPt - 1;
        for (int i = 0; i < numPt; i++) {
            result[i] = start + (end - start) * (i / numD);
        }
        result[result.length - 1] = end;
        return result;
    }

    public static <E> void wash(List<E> list, int time) {
        Random rd = new Random();
        int size = list.size();
        for (int i = 0; i < time; i++) {
            int ri = rd.nextInt(size);
            E e = list.remove(ri);
            ri = rd.nextInt(size);
            list.add(ri, e);
        }
    }
}
