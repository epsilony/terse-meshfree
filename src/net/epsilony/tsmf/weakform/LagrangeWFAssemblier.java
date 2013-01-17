/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.weakform;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author epsilon
 */
public class LagrangeWFAssemblier extends PenaltyWFAssemblier {

    private final int nodesSize;

    public LagrangeWFAssemblier(DenseMatrix constitutiveLaw, int nodesSize, int lagrangeNodesSize, boolean denseMainMatrix, boolean upperSymmetricMainMatrix) {
        super(constitutiveLaw, nodesSize + lagrangeNodesSize, 0, denseMainMatrix, upperSymmetricMainMatrix);
        this.nodesSize = nodesSize;
    }

    @Override
    public void asmDirichlet(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFuncVals, double[] dirichletVal, boolean[] dirichletMark) {
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;

        TDoubleArrayList vs = shapeFuncVals[0];
        final boolean vb1 = dirichletMark[0];
        final boolean vb2 = dirichletMark[1];
        double drk1 = dirichletVal[0];
        double drk2 = dirichletVal[1];

        int lagIndex = firstLagIndex(nodesIds);

        for (int j = lagIndex; j < nodesIds.size(); j++) {
            double v_j_w = vs.getQuick(j) * weight;
            int mat_j = nodesIds.getQuick(j) * 2;
            if (vb1) {
                vec.add(mat_j, -v_j_w * drk1);
            }
            if (vb2) {
                vec.add(mat_j + 1, -v_j_w * drk2);
            }
            for (int i = 0; i < lagIndex; i++) {
                double v_i = vs.getQuick(i);
                int mat_i = nodesIds.getQuick(i) * 2;

                double d = -v_i * v_j_w;
                mat.add(mat_i, mat_j, d);
                mat.add(mat_i + 1, mat_j + 1, d);
                if (!upperSymmetricMainMatrix) {
                    mat.add(mat_j, mat_i, d);
                    mat.add(mat_j + 1, mat_i + 1, d);
                }
            }
        }
    }

    private int firstLagIndex(TIntArrayList nodesIds) {
        for (int i = nodesIds.size() - 1; i >= 0; i--) {
            if (nodesIds.getQuick(i) < nodesSize) {
                return i + 1;
            }
        }
        throw new IllegalStateException("There isn't any normal node ids here!");
    }
}
