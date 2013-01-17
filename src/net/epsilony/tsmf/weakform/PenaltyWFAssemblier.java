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
import no.uib.cipr.matrix.UpperSymmDenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 *
 * @author epsilon
 */
public class PenaltyWFAssemblier implements WFAssemblier {

    Matrix mainMatrix;
    DenseVector mainVector;
    DenseMatrix constitutiveLaw;
    double neumannPenalty;
    final boolean upperSymmetricMainMatrix;

    public PenaltyWFAssemblier(DenseMatrix constitutiveLaw, int nodesSize, double neumannPenalty, boolean denseMainMatrix, boolean upperSymmetricMainMatrix) {
        if (denseMainMatrix) {
            if (upperSymmetricMainMatrix) {
                mainMatrix = new UpperSymmDenseMatrix(nodesSize * 2);
            } else {
                mainMatrix = new DenseMatrix(nodesSize * 2, nodesSize * 2);
            }
        } else {
            mainMatrix = new FlexCompRowMatrix(nodesSize * 2, nodesSize * 2);
        }
        mainVector = new DenseVector(nodesSize * 2);
        this.constitutiveLaw = constitutiveLaw;
        this.neumannPenalty = neumannPenalty;
        this.upperSymmetricMainMatrix = upperSymmetricMainMatrix;
    }

    @Override
    public void asmBalance(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] volumnForce) {
        TDoubleArrayList v = shapeFunVals[0];
        TDoubleArrayList v_x = shapeFunVals[1];
        TDoubleArrayList v_y = shapeFunVals[2];
        double b1 = volumnForce[0] * weight;
        double b2 = volumnForce[1] * weight;
        Matrix mat = mainMatrix;
        for (int i = 0; i < nodesIds.size(); i++) {
            int mat_i = nodesIds.getQuick(i) * 2;
            double v_x_i = v_x.getQuick(i);
            double v_y_i = v_y.getQuick(i);
            double v_i = v.getQuick(i);
            mainVector.add(mat_i, b1 * v_i);
            mainVector.add(mat_i + 1, b2 * v_i);

            int jStart = 0;
            if (upperSymmetricMainMatrix) {
                jStart = i;
            }
            for (int j = jStart; j < nodesIds.size(); j++) {
                int mat_j = nodesIds.getQuick(j) * 2;
                double v_x_j = v_x.getQuick(j);
                double v_y_j = v_y.getQuick(j);

                double[] i_v1 = new double[]{v_x_i, 0, v_y_i};
                double[] i_v2 = new double[]{0, v_y_i, v_x_i};
                double[] j_v1 = new double[]{v_x_j, 0, v_y_j};
                double[] j_v2 = new double[]{0, v_y_j, v_x_j};

                double d11 = weight * leftRightMult(i_v1, j_v1);
                double d21 = weight * leftRightMult(i_v2, j_v1);
                double d12 = weight * leftRightMult(i_v1, j_v2);
                double d22 = weight * leftRightMult(i_v2, j_v2);

                if (upperSymmetricMainMatrix && mat_j <= mat_i) {
                    mat.add(mat_j, mat_i, d11);
                    mat.add(mat_j, mat_i + 1, d21);
                    mat.add(mat_j + 1, mat_i + 1, d22);
                    if (mat_i != mat_j) {
                        mat.add(mat_j + 1, mat_i, d12);
                    }
                } else {
                    mat.add(mat_i, mat_j, d11);
                    mat.add(mat_i, mat_j + 1, d12);
                    mat.add(mat_i + 1, mat_j + 1, d22);
                    if (!(upperSymmetricMainMatrix && mat_i == mat_j)) {
                        mat.add(mat_i + 1, mat_j, d21);
                    }
                }
            }
        }
    }

    private double leftRightMult(double[] l, double[] r) {
        Matrix mat = constitutiveLaw;
        double result = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double t = l[i] * r[j];
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
        return neumannPenalty;
    }

    @Override
    public void asmDirichlet(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFuncVals, double[] dirichletVal, boolean[] dirichletMark) {
        double factor = weight * neumannPenalty;
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;
        TDoubleArrayList vs = shapeFuncVals[0];

        final boolean vb1 = dirichletMark[0];
        final boolean vb2 = dirichletMark[1];
        for (int i = 0; i < nodesIds.size(); i++) {
            int mat_i = nodesIds.getQuick(i) * 2;
            double vi = vs.getQuick(i);
            if (vb1) {
                vec.add(mat_i, vi * dirichletVal[0] * factor);
            }
            if (vb2) {
                vec.add(mat_i + 1, vi * dirichletVal[1] * factor);
            }
            int jStart=0;
            if(upperSymmetricMainMatrix){
                jStart=i;
            }
            for (int j = jStart; j < nodesIds.size(); j++) {
                int mat_j = nodesIds.getQuick(j) * 2;
                double vij = factor * vi * vs.getQuick(j);
                int indexI;
                int indexJ;
                if (upperSymmetricMainMatrix&&mat_j <= mat_i) {
                    indexI = mat_j;
                    indexJ = mat_i;
                } else {
                    indexI = mat_i;
                    indexJ = mat_j;
                }
                if (vb1) {
                    mat.add(indexI, indexJ, vij);
                }
                if (vb2) {
                    mat.add(indexI + 1, indexJ + 1, vij);
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
}
