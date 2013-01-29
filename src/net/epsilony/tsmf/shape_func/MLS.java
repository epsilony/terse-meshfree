/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.shape_func;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.List;
import net.epsilony.tsmf.util.Math2D;
import net.epsilony.tsmf.util.WithDiffOrderUtil;
import org.ejml.alg.dense.decomposition.lu.LUDecompositionNR;
import org.ejml.alg.dense.linsol.lu.LinearSolverLu;
import org.ejml.alg.dense.mult.MatrixVectorMult;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
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
    DenseMatrix64F gamma, gamma_d, tVec1, tVec2;
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
        tVec1 = new DenseMatrix64F(basisLen, 1);
        tVec2 = new DenseMatrix64F(basisLen, 1);
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
        if (null == dists) {
            dists = ordinaryDistances(xy, coords);
        }

        calcMatAB(xy, coords, influcenceRads, dists);

        int diffOrder = getDiffOrder();
        basisFunc.setDiffOrder(diffOrder);
        basisFunc.values(ZERO, basisCache);
        solver.setA(matAs[0]);
        copyTo(basisCache[0], tVec1);
        solver.solve(tVec1, gamma);
        dot(matBs[0], gamma, results[0]);

        if (diffOrder > 0) {
            for (int i = 1; i < matAs.length; i++) {
                MatrixVectorMult.mult(matAs[i], gamma, tVec1);
                copyTo(basisCache[i], tVec2);
                CommonOps.sub(tVec2, tVec1, tVec1);
                solver.solve(tVec1, gamma_d);
                dot(matBs[i], gamma, results[i]);
                commonCache.resetQuick();
                dot(matBs[0], gamma_d, commonCache);
                addTo(commonCache, results[i]);
            }
        }

        return results;
    }

    private void calcMatAB(double[] xy, List<double[]> coords, TDoubleArrayList influcenceRads, TDoubleArrayList[] dists) {
        TDoubleArrayList[] weightsByDiffs = weightFunc.values(dists, influcenceRads, weightsCache);
        basisFunc.setDiffOrder(0);
        double[] tds = new double[2];
        int coordIndex = 0;
        for (double[] crd : coords) {
            basisFunc.values(Math2D.subs(crd, xy, tds), basisCache);
            TDoubleArrayList basis = basisCache[0];
            for (int differential = 0; differential < matAs.length; differential++) {
                DenseMatrix64F A_d = matAs[differential];
                TDoubleArrayList[] B_d = matBs[differential];
                double weight = weightsByDiffs[differential].getQuick(coordIndex);
                vectorTransMultVectorAddtoA(weight, basis, A_d);
                appendToB(weight, basis, B_d);
            }
            coordIndex++;
        }
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

    static private void vectorTransMultVectorAddtoA(double scale, TDoubleArrayList vector, DenseMatrix64F matA) {
        for (int i = 0; i < vector.size(); i++) {
            double b1 = vector.getQuick(i);
            for (int j = 0; j < vector.size(); j++) {
                double b2 = vector.getQuick(j);
                matA.add(i, j, scale * b1 * b2);
            }
        }
    }

    static private void appendToB(double scale, TDoubleArrayList vector, TDoubleArrayList[] matB) {
        for (int row = 0; row < vector.size(); row++) {
            matB[row].add(scale * vector.getQuick(row));
        }
    }

    private TDoubleArrayList[] ordinaryDistances(double[] xy, List<double[]> coords) {
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
        return distsCache;
    }
}
