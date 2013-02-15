/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangeWFAssemblier extends PenaltyWFAssemblier implements SupportLagrange {

    @Override
    public WFAssemblier synchronizeClone() {
        return new LagrangeWFAssemblier();
    }
    int diriNum;

    public LagrangeWFAssemblier() {
        super(0);
    }

    @Override
    public void asmDirichlet(
            double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFuncVals,
            double[] dirichletVal, boolean[] dirichletMark) {
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;

        TDoubleArrayList vs = shapeFuncVals[0];
        final boolean dirichletX = dirichletMark[0];
        final boolean dirichletY = dirichletMark[1];
        double drk1 = dirichletVal[0];
        double drk2 = dirichletVal[1];

        int lagIndex = firstLagIndex(nodesIds);

        for (int j = lagIndex; j < nodesIds.size(); j++) {
            double v_j_w = vs.getQuick(j) * weight;
            int col = nodesIds.getQuick(j) * 2;
            if (dirichletX) {
                vec.add(col, -v_j_w * drk1);
            }
            if (dirichletY) {
                vec.add(col + 1, -v_j_w * drk2);
            }
            for (int i = 0; i < lagIndex; i++) {
                double v_i = vs.getQuick(i);
                int row = nodesIds.getQuick(i) * 2;

                double d = -v_i * v_j_w;
                mat.add(row, col, d);
                mat.add(row + 1, col + 1, d);
                if (!isUpperSymmertric()) {
                    mat.add(col, row, d);
                    mat.add(col + 1, row + 1, d);
                }
            }
        }
    }

    private int firstLagIndex(TIntArrayList nodesIds) {
        for (int i = nodesIds.size() - 1; i >= 0; i--) {
            if (nodesIds.getQuick(i) < nodesNum) {
                return i + 1;
            }
        }
        throw new IllegalStateException("nodesIds should contains both Lagrange-node ids and normal-nodes ids");
    }

    @Override
    public void setDirichletNodesNums(int diriNum) {
        this.diriNum = diriNum;
    }

    @Override
    public void prepare() {
        initMainMatrixVector(2 * (nodesNum + diriNum));
    }
}
