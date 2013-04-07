/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.tsmf.util.common_func;

import net.epsilony.tsmf.shape_func.RadialFunctionCore;
import static java.lang.Math.*;
import net.epsilony.tsmf.util.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class NormalFunction implements RadialFunctionCore {

    public static double DEFAULT_SIGMA = 1;
    int diffOrder;
    double sigma = DEFAULT_SIGMA;
    double coef = 1 / (sigma * sqrt(PI * 2));

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    @Override
    public double[] values(double x, double[] results) {
        if (null == results) {
            results = new double[]{diffOrder + 1};
        }
        double t = x / sigma;
        final double v = coef * exp(-0.5 * t * t);
        results[0] = v;
        if (diffOrder >= 1) {
            results[1] = -t * v / sigma;
        }
        return results;
    }

    @Override
    public int getDiffOrder() {
        return diffOrder;
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        if (diffOrder < 0 || diffOrder > 1) {
            throw new IllegalArgumentException("only supports 0 or 1");
        }
    }

    @Override
    public NormalFunction synchronizeClone() {
        NormalFunction result = new NormalFunction();
        result.setDiffOrder(diffOrder);
        result.setSigma(sigma);
        return result;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + '{' + "sigma=" + sigma + '}';
    }
}
