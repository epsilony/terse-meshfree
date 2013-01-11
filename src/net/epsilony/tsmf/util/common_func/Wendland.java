/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.common_func;

import java.util.Arrays;
import net.epsilony.tsmf.util.UnivarArrayFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

/**
 *
 * @author epsilon
 */
public class Wendland implements UnivarArrayFunction {

    private static final double[] COEFS_1 = new double[]{1, -1};
    private static final double[] COEFS_1_ORDERS = new double[]{4, 6, 8};
    private static final double[][] COEFS_2S = new double[][]{
        {1, 4},
        {3, 18, 35},
        {1, 8, 25, 32}};
    int diffOrder;
    private static final int[] LEGAL_CONTINUOUS_ORDERS = new int[]{2, 4, 6};
    PolynomialFunction pFunc;
    PolynomialFunction[] pFuncs;

    private void initWendland(int c) {
        boolean legal = false;
        for (int lc : LEGAL_CONTINUOUS_ORDERS) {
            if (lc == c) {
                legal = true;
                break;
            }
        }
        if (!legal) {
            throw new IllegalArgumentException("The legal continuous order should be one of " + Arrays.toString(LEGAL_CONTINUOUS_ORDERS) + " , not " + c);
        }
        int index = c / 2 - 1;
        PolynomialFunction p1 = new PolynomialFunction(COEFS_1);
        for (int i = 1; i < COEFS_1_ORDERS[index]; i++) {
            p1 = p1.multiply(p1);
        }
        PolynomialFunction p2 = new PolynomialFunction(COEFS_2S[index]);
        pFunc = p1.multiply(p2);
        diffOrder = -1;
        _setDiffOrder(0);

    }

    private void _setDiffOrder(int diffOrder) {
        if (diffOrder < 0) {
            throw new IllegalArgumentException("only support diffOrder that is > 0, not " + diffOrder);
        }
        if (diffOrder == this.diffOrder) {
            return;
        }
        this.diffOrder = diffOrder;
        pFuncs = new PolynomialFunction[diffOrder + 1];
        pFuncs[0] = pFunc;
        for (int i = 1; i <= diffOrder; i++) {
            pFuncs[i] = pFuncs[i - 1].polynomialDerivative();
        }
    }

    @Override
    public double[] values(double x, double[] results) {
        if (null == results) {
            results = new double[diffOrder + 1];
        }
        for (int i = 0; i < results.length; i++) {
            results[i] = pFuncs[i].value(x);
        }
        return results;
    }

    @Override
    public int getDiffOrder() {
        return diffOrder;
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        _setDiffOrder(diffOrder);
    }
}
