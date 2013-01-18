/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.List;
import net.epsilony.tsmf.util.Math2D;
import net.epsilony.tsmf.util.WithDiffOrder;
import net.epsilony.tsmf.util.WithDiffOrderUtil;
import org.ejml.alg.dense.decomposition.lu.LUDecompositionNR;
import org.ejml.alg.dense.linsol.lu.LinearSolverLu;
import org.ejml.alg.dense.mult.MatrixVectorMult;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 *
 * @author epsilon
 */
public class MLS implements ShapeFunction {

    RadialFunction2D weightFunc;
    BasisFunction basisFunc;
    DenseMatrix64F[] matAs;
    public final int DEFAULT_MAT_B_CAPACITY = 50;
    TDoubleArrayList[][] matBs;
    TDoubleArrayList[] distsCache;
    TDoubleArrayList[] weightsCache;
    TDoubleArrayList[] basisCache;
    TDoubleArrayList commonCache;
    LinearSolverLu solver = new LinearSolverLu(new LUDecompositionNR());
    //DenseMatrix64F solverA;
    DenseMatrix64F b_;
    DenseMatrix64F gamma, gamma_d, tv1, tv2;
    static private final double[] ZERO = new double[]{0, 0};

    @Override
    public int getDiffOrder() {
        return weightFunc.getDiffOrder();
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        if (diffOrder < 0 || diffOrder > 1) {
            throw new IllegalArgumentException("only support diffOrder that is 0 or 1, not " + diffOrder);
        }
        weightFunc.setDiffOrder(diffOrder);
        buildCaches();
    }

    private void buildCaches() {
        int diffSize = WithDiffOrderUtil.outputLength2D(getDiffOrder());
        matAs = new DenseMatrix64F[diffSize];
        distsCache = new TDoubleArrayList[diffSize];
        basisCache = new TDoubleArrayList[diffSize];

        int basisLen = basisFunc.basisLength();
        weightsCache = new TDoubleArrayList[diffSize];
        gamma = new DenseMatrix64F(basisLen, 1);
        gamma_d = new DenseMatrix64F(basisLen, 1);
        tv1 = new DenseMatrix64F(basisLen, 1);
        tv2 = new DenseMatrix64F(basisLen, 1);
        b_ = new DenseMatrix64F(basisLen, 1);
        commonCache = new TDoubleArrayList(DEFAULT_MAT_B_CAPACITY);
        for (int i = 0; i < diffSize; i++) {
            matAs[i] = new DenseMatrix64F(basisLen, basisLen);
            distsCache[i] = new TDoubleArrayList(DEFAULT_MAT_B_CAPACITY);
            weightsCache[i] = new TDoubleArrayList(DEFAULT_MAT_B_CAPACITY);
            basisCache[i] = new TDoubleArrayList(basisLen);
        }
        matBs = new TDoubleArrayList[diffSize][basisLen];
        for (int i = 0; i < matBs.length; i++) {
            TDoubleArrayList[] matB = matBs[i];
            for (int j = 0; j < matB.length; j++) {
                matB[j] = new TDoubleArrayList(DEFAULT_MAT_B_CAPACITY);
            }
        }
    }

    private void resetCaches(int capacity) {
        for (int i = 0; i < distsCache.length; i++) {
            distsCache[i].resetQuick();
            distsCache[i].ensureCapacity(capacity);
            matAs[i].zero();
            TDoubleArrayList[] bMat = matBs[i];
            for (int j = 0; j < bMat.length; j++) {
                bMat[j].resetQuick();
                bMat[j].ensureCapacity(capacity);
            }
        }
        commonCache.resetQuick();
        commonCache.ensureCapacity(capacity);
    }

    public MLS(RadialFunction2D weightFunc, BasisFunction basisFunc) {
        this.weightFunc = weightFunc;
        this.basisFunc = basisFunc;
        weightFunc.setDiffOrder(0);
        buildCaches();
    }

    public MLS(RadialFunction2D weightFunc) {
        this.weightFunc = weightFunc;
        this.basisFunc = new MonomialBasis2D(2);
        weightFunc.setDiffOrder(0);
        buildCaches();
    }

    public MLS() {
        this.weightFunc = new RadialFunction2D();
        this.basisFunc = new MonomialBasis2D(2);
        weightFunc.setDiffOrder(0);
        buildCaches();
    }

    @Override
    public TDoubleArrayList[] values(double[] xy, List<double[]> coords, TDoubleArrayList influcenceRads, TDoubleArrayList[] dists, TDoubleArrayList[] output) {
        resetCaches(coords.size());
        TDoubleArrayList[] results = WithDiffOrderUtil.initOutput(output, coords.size(), 2, getDiffOrder());
        TDoubleArrayList[] ds = dists;
        if (null == dists) {
            fillDistsCache(xy, coords);
            ds = distsCache;
        }
        weightFunc.values(ds, influcenceRads, weightsCache);

        basisFunc.setDiffOrder(0);
        double[] tds = new double[2];
        int crd_index = 0;
        for (double[] crd : coords) {
            basisFunc.values(Math2D.subs(crd,xy, tds), basisCache);
            TDoubleArrayList basis = basisCache[0];
            for (int i = 0; i < matAs.length; i++) {
                DenseMatrix64F A_d = matAs[i];
                TDoubleArrayList[] B_d = matBs[i];
                double weight = weightsCache[i].getQuick(crd_index);
                addToA(weight, basis, A_d);
                addToB(weight, basis, B_d);
            }
            crd_index++;
        }

        int diffOrder = getDiffOrder();
        if (diffOrder > 0) {
            basisFunc.setDiffOrder(diffOrder);
        }
        basisFunc.values(ZERO, basisCache);
        solver.setA(matAs[0]);
        copyTo(basisCache[0], b_);
        solver.solve(b_, gamma);
        dot(matBs[0], gamma, results[0]);

        if (diffOrder > 0) {
            for (int i = 1; i < matAs.length; i++) {
                MatrixVectorMult.mult(matAs[i], gamma, tv1);
                copyTo(basisCache[i], tv2);
                CommonOps.sub(tv2, tv1, tv1);
                solver.solve(tv1, gamma_d);
                dot(matBs[i], gamma, results[i]);
                commonCache.resetQuick();
                dot(matBs[0], gamma_d, commonCache);
                addTo(commonCache, results[i]);
            }
        }

        return results;
    }

    static private void addTo(TDoubleArrayList from, TDoubleArrayList to) {
        for (int i = 0; i < from.size(); i++) {
            to.setQuick(i, to.getQuick(i) + from.getQuick(i));
        }
    }

    static private void dot(TDoubleArrayList[] mat, DenseMatrix64F vec, TDoubleArrayList result) {
        for (int i = 0; i < mat[0].size(); i++) {
            double sum = 0;
            for (int j = 0; j < mat.length; j++) {
                sum += vec.get(j) * mat[j].getQuick(i);
            }
            result.add(sum);
        }
    }

    static private void copyTo(TDoubleArrayList from, DenseMatrix64F to) {
        for (int i = 0; i < from.size(); i++) {
            to.set(i, from.getQuick(i));
        }
    }

    static private void addToA(double w, TDoubleArrayList basis, DenseMatrix64F matA) {
        for (int i = 0; i < basis.size(); i++) {
            double b1 = basis.getQuick(i);
            for (int j = 0; j < basis.size(); j++) {
                double b2 = basis.getQuick(j);
                matA.add(i, j, w * b1 * b2);
            }
        }
    }

    static private void addToB(double w, TDoubleArrayList basis, TDoubleArrayList[] matB) {
        for (int i = 0; i < basis.size(); i++) {
            matB[i].add(w * basis.getQuick(i));
        }
    }

    private void fillDistsCache(double[] xy, List<double[]> coords) {
        int diffSize = distsCache.length;
        double[] tds = null;
        if (diffSize > 1) {
            tds = new double[diffSize];
        }
        for (double[] crd : coords) {
            if (diffSize > 1) {
                Math2D.distanceAndPartDiffs(xy, crd, tds);
                for (int i = 0; i < distsCache.length; i++) {
                    distsCache[i].add(tds[i]);
                }
            } else {
                distsCache[0].add(Math2D.distance(xy, crd));
            }
        }
    }
}
