/* (c) Copyright by Man YUAN */
package net.epsilony.tb.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.tb.WithDiffOrderUtil;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MonomialBasis2D implements BasisFunction {

    private int monomialOrder;
    private int diffOrder;

    public void setMonomialOrder(int monomialOrder) {
        this.monomialOrder = monomialOrder;
    }

    public MonomialBasis2D(int monomialOrder) {
        initMonomialBasis2D(monomialOrder);
    }

    public MonomialBasis2D() {
        initMonomialBasis2D(2);
    }

    public int getMonomialOrder() {
        return monomialOrder;
    }

    private void initMonomialBasis2D(int basisOrder) {
        this.monomialOrder = basisOrder;
        diffOrder = 0;
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

    public static int basisLength(int monomialOrder) {
        if (monomialOrder == -1) {
            return 0;
        }
        return WithDiffOrderUtil.outputLength2D(monomialOrder);
    }

    @Override
    public TDoubleArrayList[] values(double[] xy, TDoubleArrayList[] output) {
        TDoubleArrayList[] results = initOutput(output);
        if (monomialOrder >= 0) {
            results[0].add(1);
            if (diffOrder > 0) {
                results[1].add(0);
                results[2].add(0);
            }
        }
        double x = xy[0], y = xy[1];
        if (monomialOrder >= 1) {
            results[0].add(x);
            results[0].add(y);
            if (diffOrder > 0) {
                results[1].add(1);
                results[1].add(0);
                results[2].add(0);
                results[2].add(1);
            }
        }
        for (int n = 2; n <= monomialOrder; n++) {
            int i1 = basisLength(n - 2);

            if (diffOrder > 0) {
                results[1].add(n * results[0].getQuick(i1));
            }
            for (int i = 0; i < n; i++) {
                results[0].add(x * results[0].getQuick(i1 + i));
                if (diffOrder > 0) {
                    results[1].add(y * results[1].getQuick(i1 + i));
                    results[2].add(x * results[2].getQuick(i1 + i));
                }
            }
            results[0].add(y * results[0].getQuick(i1 + n - 1));
            if (diffOrder > 0) {
                results[2].add(n * results[0].getQuick(i1 + n - 1));
            }
        }
        return results;
    }

    public TDoubleArrayList[] initOutput(TDoubleArrayList[] output) {
        return WithDiffOrderUtil.initOutput(output, basisLength(), 2, diffOrder);
    }

    @Override
    public int basisLength() {
        return basisLength(getMonomialOrder());
    }

    @Override
    public BasisFunction synchronizeClone() {
        return new MonomialBasis2D(monomialOrder);
    }
}
