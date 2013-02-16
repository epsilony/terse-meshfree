/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.common_func;

import java.util.Arrays;
import net.epsilony.tsmf.shape_func.RadialFunctionCore;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TripleSpline implements RadialFunctionCore {

    int diffOrder;

    @Override
    public double[] values(double x, double[] results) {
        if (null == results) {
            results = new double[diffOrder + 1];
        }
        if (x > 1) {
            Arrays.fill(results, 0);
            return results;
        }
        if (x < 0) {
            throw new IllegalArgumentException("x should be >= 0, not" + x);
        }
        if (x <= 0.5) {
            results[0] = 2 / 3.0 - 4 * x * x + 4 * x * x * x;
            if (diffOrder > 0) {
                results[1] = -8 * x + 12 * x * x;
            }
        } else {
            results[0] = 4 / 3.0 - 4 * x + 4 * x * x - 4 / 3.0 * x * x * x;
            if (diffOrder > 0) {
                results[1] = -4 + 8 * x - 4 * x * x;
            }
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
            throw new IllegalArgumentException("only support diffOrder that is 0 or 1, not " + diffOrder);
        }
        this.diffOrder = diffOrder;
    }

    @Override
    public RadialFunctionCore synchronizeClone() {
        TripleSpline result = new TripleSpline();
        result.setDiffOrder(getDiffOrder());
        return result;
    }
}
