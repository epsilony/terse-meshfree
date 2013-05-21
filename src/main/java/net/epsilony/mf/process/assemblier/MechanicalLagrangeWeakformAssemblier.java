/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MechanicalLagrangeWeakformAssemblier
        extends MechanicalPenaltyWeakformAssemblier
        implements WeakformLagrangeAssemblier {

    int dirichletNodesNum;
    TIntArrayList lagrangeAssemblyIndes;
    TDoubleArrayList lagrangeShapeFunctionValue;

    public MechanicalLagrangeWeakformAssemblier() {
        super(0);
    }

    @Override
    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            TDoubleArrayList lagrangeShapeFunctionValue) {
        this.lagrangeAssemblyIndes = lagrangeAssemblyIndes;
        this.lagrangeShapeFunctionValue = lagrangeShapeFunctionValue;
    }

    @Override
    public void assembleDirichlet() {
        Matrix mat = mainMatrix;
        DenseVector vec = mainVector;
        TDoubleArrayList vs = shapeFunctionValues[0];
        boolean[] dirichletMark = loadValidity;
        double[] dirichletVal = load;
        final boolean dirichletX = dirichletMark[0];
        final boolean dirichletY = dirichletMark[1];
        double drk1 = dirichletVal[0];
        double drk2 = dirichletVal[1];

        for (int j = 0; j < lagrangeAssemblyIndes.size(); j++) {
            double v_j_w = lagrangeShapeFunctionValue.getQuick(j) * weight;
            int col = lagrangeAssemblyIndes.getQuick(j) * 2;
            if (dirichletX) {
                vec.add(col, -v_j_w * drk1);
            }
            if (dirichletY) {
                vec.add(col + 1, -v_j_w * drk2);
            }
            for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
                double v_i = vs.getQuick(i);
                int row = nodesAssemblyIndes.getQuick(i) * 2;

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

    @Override
    public void setDirichletNodesNum(int dirichletNodesNum) {
        this.dirichletNodesNum = dirichletNodesNum;
    }

    @Override
    public int getDirichletNodesNum() {
        return dirichletNodesNum;
    }

    @Override
    protected int getMainMatrixSize() {
        return getNodeValueDimension() * (nodesNum + dirichletNodesNum);
    }

    @Override
    public MechanicalLagrangeWeakformAssemblier synchronizeClone() {
        MechanicalLagrangeWeakformAssemblier result = new MechanicalLagrangeWeakformAssemblier();
        result.setConstitutiveLaw(constitutiveLaw);
        result.setDirichletNodesNum(dirichletNodesNum);
        result.setMatrixDense(isMatrixDense());
        result.setNodesNum(nodesNum);
        result.prepare();
        return result;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d,"
                + " mat dense/sym: %b/%b,"
                + " dirichlet lagrangian nodes: %d}",
                getNodesNum(),
                getNodeValueDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmertric(),
                getDirichletNodesNum());
    }
}
