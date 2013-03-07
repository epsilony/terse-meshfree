/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.implicit;

import net.epsilony.tsmf.util.GenericFunction;
import net.epsilony.tsmf.util.Math2D;
import org.junit.Ignore;

/**
 *
 * @author epsilon
 */
@Ignore
public class DiskWithAHoleLevelSetFunction implements GenericFunction<double[], double[]> {

    double diskX = 50, diskY = 50, diskRad = 40;
    double holeX = 44, holeY = 42, holeRad = 15;

    @Override
    public double[] value(double[] input, double[] output) {
        double diskValue = diskRad - Math2D.distance(diskX, diskY, input[0], input[1]);
        double holeValue = Math2D.distance(holeX, holeY, input[0], input[1])-holeRad;
        double value = Math.min(diskValue, holeValue);
        if (output == null) {
            return new double[]{value};
        } else {
            output[0] = value;
            return output;
        }
    }
}
