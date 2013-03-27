/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.tsmf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractWeakformLagrangeAssemblier extends AbstractWeakformAssemblier implements WeakformLagrangeAssemblier {

    int dirichletNodesNum;
    TIntArrayList lagrangeAssemblyIndes;
    TDoubleArrayList lagrangeShapeFunctionValue;

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
}
