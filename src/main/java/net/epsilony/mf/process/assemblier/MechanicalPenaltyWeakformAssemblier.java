/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalPenaltyWeakformAssemblier extends AbstractWeakformAssemblier {

    double penalty;

    public MechanicalPenaltyWeakformAssemblier(double neumannPenalty) {
        this.penalty = neumannPenalty;
    }

    @Override
    public void assembleVolume() {
        double[] volumnForce = load;
        TDoubleArrayList v = shapeFunctionValues[0];
        TDoubleArrayList v_x = shapeFunctionValues[1];
        TDoubleArrayList v_y = shapeFunctionValues[2];
        double b1 = 0, b2 = 0;
        if (volumnForce != null) {
            b1 = volumnForce[0] * weight;
            b2 = volumnForce[1] * weight;
        }
        Matrix mat = mainMatrix;
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i) * 2;
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
            for (int j = jStart; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j) * 2;
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
        DenseMatrix mat = constitutiveLawMatrixCopy;
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
    public void assembleNeumann() {
        DenseVector vec = mainVector;
        double[] neumannVal = load;
        double valueX = neumannVal[0] * weight;
        double valueY = neumannVal[1] * weight;
        TDoubleArrayList vs = shapeFunctionValues[0];
        final boolean vali1 = valueX != 0;
        final boolean vali2 = valueY != 0;
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int vecIndex = nodesAssemblyIndes.getQuick(i) * 2;
            double v = vs.getQuick(i);
            if (vali1) {
                vec.add(vecIndex, valueX * v);
            }
            if (vali2) {
                vec.add(vecIndex + 1, valueY * v);
            }
        }
    }

    @Override
    public void assembleDirichlet() {
        double[] dirichletVal = load;
        boolean[] dirichletMark = loadValidity;
        double factor = weight * penalty;
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;
        TDoubleArrayList vs = shapeFunctionValues[0];

        final boolean dirichletX = dirichletMark[0];
        final boolean dirichletY = dirichletMark[1];
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i) * 2;
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
            for (int j = jStart; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j) * 2;
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
    protected int getMainMatrixSize() {
        return nodesNum * 2;
    }

    @Override
    public MechanicalPenaltyWeakformAssemblier synchronizeClone() {
        MechanicalPenaltyWeakformAssemblier result = new MechanicalPenaltyWeakformAssemblier(penalty);
        result.setNodesNum(nodesNum);
        result.setConstitutiveLaw(constitutiveLaw);
        result.setMatrixDense(dense);
        result.prepare();
        return result;
    }

    @Override
    public int getVolumeDiffOrder() {
        return 1;
    }

    @Override
    public int getDirichletDiffOrder() {
        return 0;
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    @Override
    public int getNodeValueDimension() {
        return 2;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d, mat dense/sym: %b/%b, penalty %f}",
                getNodesNum(),
                getNodeValueDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmertric(),
                getPenalty());
    }
}
