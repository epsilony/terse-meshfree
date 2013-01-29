/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.UpperSymmDenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PenaltyWFAssemblier implements WFAssemblier {

    Matrix mainMatrix;
    DenseVector mainVector;
    ConstitutiveLaw constitutiveLaw;
    double penalty;
    boolean dense;

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }
    int nodesNum;

    public PenaltyWFAssemblier(double neumannPenalty) {
        this.penalty = neumannPenalty;
    }

    protected final void initMainMatrixVector(int numRowCol) {
        if (dense) {
            if (constitutiveLaw.isSymmetric()) {
                mainMatrix = new UpperSymmDenseMatrix(numRowCol);
            } else {
                mainMatrix = new DenseMatrix(numRowCol, numRowCol);
            }
        } else {
            mainMatrix = new FlexCompRowMatrix(numRowCol, numRowCol);
        }
        mainVector = new DenseVector(numRowCol);
    }

    @Override
    public boolean isUpperSymmertric() {
        return constitutiveLaw.isSymmetric();
    }

    @Override
    public void asmBalance(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] volumnForce) {
        TDoubleArrayList v = shapeFunVals[0];
        TDoubleArrayList v_x = shapeFunVals[1];
        TDoubleArrayList v_y = shapeFunVals[2];
        double b1 = 0, b2 = 0;
        if (volumnForce != null) {
            b1 = volumnForce[0] * weight;
            b2 = volumnForce[1] * weight;
        }
        Matrix mat = mainMatrix;
        for (int i = 0; i < nodesIds.size(); i++) {
            int row = nodesIds.getQuick(i) * 2;
            double v_x_i = v_x.getQuick(i);
            double v_y_i = v_y.getQuick(i);
            double v_i = v.getQuick(i);
            if (volumnForce != null) {
                mainVector.add(row, b1 * v_i);
                mainVector.add(row + 1, b2 * v_i);
            }
            int jStart = 0;
            if (isUpperSymmertric()) {
                jStart = i;
            }
            for (int j = jStart; j < nodesIds.size(); j++) {
                int col = nodesIds.getQuick(j) * 2;
                double v_x_j = v_x.getQuick(j);
                double v_y_j = v_y.getQuick(j);

                double[] i_v1 = new double[]{v_x_i, 0, v_y_i};
                double[] i_v2 = new double[]{0, v_y_i, v_x_i};
                double[] j_v1 = new double[]{v_x_j, 0, v_y_j};
                double[] j_v2 = new double[]{0, v_y_j, v_x_j};

                double d11 = weight * multConstitutiveLaw(i_v1, j_v1);
                double d21 = weight * multConstitutiveLaw(i_v2, j_v1);
                double d12 = weight * multConstitutiveLaw(i_v1, j_v2);
                double d22 = weight * multConstitutiveLaw(i_v2, j_v2);

                if (isUpperSymmertric() && col <= row) {
                    mat.add(col, row, d11);
                    mat.add(col, row + 1, d21);
                    mat.add(col + 1, row + 1, d22);
                    if (row != col) {
                        mat.add(col + 1, row, d12);
                    }
                } else {
                    mat.add(row, col, d11);
                    mat.add(row, col + 1, d12);
                    mat.add(row + 1, col + 1, d22);
                    if (!(isUpperSymmertric() && row == col)) {
                        mat.add(row + 1, col, d21);
                    }
                }
            }
        }
    }

    private double multConstitutiveLaw(double[] left, double[] right) {
        Matrix mat = constitutiveLaw.getMatrix();
        double result = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double t = left[i] * right[j];
                if (t != 0) {
                    result += t * mat.get(i, j);
                }
            }
        }
        return result;
    }

    @Override
    public void asmNeumann(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] neumannVal) {
        DenseVector vec = mainVector;
        double valueX = neumannVal[0] * weight;
        double valueY = neumannVal[1] * weight;
        TDoubleArrayList vs = shapeFunVals[0];
        final boolean vali1 = valueX != 0;
        final boolean vali2 = valueY != 0;
        for (int i = 0; i < nodesIds.size(); i++) {
            int vecIndex = nodesIds.getQuick(i) * 2;
            double v = vs.getQuick(i);
            if (vali1) {
                vec.add(vecIndex, valueX * v);
            }
            if (vali2) {
                vec.add(vecIndex + 1, valueY * v);
            }
        }
    }

    double getNeumannPenalty() {
        return penalty;
    }

    @Override
    public void asmDirichlet(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFuncVals, double[] dirichletVal, boolean[] dirichletMark) {
        double factor = weight * penalty;
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;
        TDoubleArrayList vs = shapeFuncVals[0];

        final boolean dirichletX = dirichletMark[0];
        final boolean dirichletY = dirichletMark[1];
        for (int i = 0; i < nodesIds.size(); i++) {
            int row = nodesIds.getQuick(i) * 2;
            double vi = vs.getQuick(i);
            if (dirichletX) {
                vec.add(row, vi * dirichletVal[0] * factor);
            }
            if (dirichletY) {
                vec.add(row + 1, vi * dirichletVal[1] * factor);
            }
            int jStart = 0;
            if (isUpperSymmertric()) {
                jStart = i;
            }
            for (int j = jStart; j < nodesIds.size(); j++) {
                int col = nodesIds.getQuick(j) * 2;
                double vij = factor * vi * vs.getQuick(j);
                int tRow;
                int tCol;
                if (isUpperSymmertric() && col <= row) {
                    tRow = col;
                    tCol = row;
                } else {
                    tRow = row;
                    tCol = col;
                }
                if (dirichletX) {
                    mat.add(tRow, tCol, vij);
                }
                if (dirichletY) {
                    mat.add(tRow + 1, tCol + 1, vij);
                }
            }
        }
    }

    @Override
    public Matrix getMainMatrix() {
        return mainMatrix;
    }

    @Override
    public DenseVector getMainVector() {
        return mainVector;
    }

    @Override
    public void setNodesNum(int nodesNum) {
        this.nodesNum = nodesNum;
    }

    @Override
    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
    }

    @Override
    public void prepare() {
        initMainMatrixVector(nodesNum * 2);
    }

    @Override
    public void setMatrixDense(boolean dense) {
        this.dense = dense;
    }
}
