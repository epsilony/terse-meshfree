/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractWeakformLagrangeAssemblier
        extends AbstractWeakformAssemblier
        implements WeakformLagrangeAssemblier {

    protected int dirichletNodesNum;
    protected TIntArrayList lagrangeAssemblyIndes;
    protected TDoubleArrayList lagrangeShapeFunctionValue;

    @Override
    protected int getMainMatrixSize() {
        return getNodeValueDimension() * (nodesNum + dirichletNodesNum);
    }

    @Override
    public void setLagrangeShapeFunctionValue(
            TIntArrayList lagrangeAssemblyIndes,
            TDoubleArrayList lagrangeShapeFunctionValue) {
        this.lagrangeAssemblyIndes = lagrangeAssemblyIndes;
        this.lagrangeShapeFunctionValue = lagrangeShapeFunctionValue;
    }

    @Override
    public int getDirichletNodesNum() {
        return dirichletNodesNum;
    }

    @Override
    public void setDirichletNodesNum(int dirichletNodesNum) {
        this.dirichletNodesNum = dirichletNodesNum;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D:%d/%d/%d, "
                + "mat dense/sym: %b/%b, "
                + "dirichlet lagrangian nodes: %d}",
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
