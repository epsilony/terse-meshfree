/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.tsmf.util.WithDiffOrder;
import net.epsilony.tsmf.util.WithDiffOrderUtil;
import net.epsilony.tsmf.util.common_func.TripleSpline;
import net.epsilony.tsmf.util.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RadialFunction2D implements WithDiffOrder, SynchronizedClonable<RadialFunction2D> {

    RadialFunctionCore coreFunc;

    @Override
    public int getDiffOrder() {
        return coreFunc.getDiffOrder();
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        coreFunc.setDiffOrder(diffOrder);
    }

    public TDoubleArrayList[] initOutput(int capacity, TDoubleArrayList[] outputs) {
        return WithDiffOrderUtil.initOutput(outputs, capacity, 2, getDiffOrder());
    }

    public RadialFunction2D(RadialFunctionCore coreFunc) {
        this.coreFunc = coreFunc;
    }

    public RadialFunction2D() {
        this.coreFunc = new TripleSpline();
    }

    public TDoubleArrayList[] values(TDoubleArrayList[] dists, TDoubleArrayList influenceRads, TDoubleArrayList[] outputs) {
        TDoubleArrayList[] results = initOutput(dists[0].size(), outputs);
        boolean isUniRad = true;
        double uniRad = influenceRads.getQuick(0);
        if (influenceRads.size() > 1) {
            isUniRad = false;
        }
        int numRows = WithDiffOrderUtil.outputLength2D(getDiffOrder());
        double[] coreVals = new double[getDiffOrder() + 1];
        for (int i = 0; i < dists[0].size(); i++) {
            double dst = dists[0].getQuick(i);
            double rad = uniRad;
            if (!isUniRad) {
                rad = influenceRads.getQuick(i);
            }
            coreVals = coreFunc.values(dst / rad, coreVals);
            results[0].add(coreVals[0]);
            for (int j = 1; j < numRows; j++) {
                results[j].add(coreVals[1] / rad * dists[j].getQuick(i));
            }
        }
        return results;
    }

    @Override
    public RadialFunction2D synchronizeClone() {
        return new RadialFunction2D(coreFunc.synchronizeClone());
    }
}
