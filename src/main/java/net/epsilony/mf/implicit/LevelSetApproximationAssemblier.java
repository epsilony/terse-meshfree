/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.implicit;

import gnu.trove.list.array.TDoubleArrayList;
import net.epsilony.mf.process.assemblier.AbstractWeakformLagrangeAssemblier;
import net.epsilony.tb.shape_func.RadialFunctionCore;
import net.epsilony.tb.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LevelSetApproximationAssemblier extends AbstractWeakformLagrangeAssemblier {

    RadialFunctionCore weightFunction;

    public RadialFunctionCore getWeightFunction() {
        return weightFunction;
    }

    public void setWeightFunction(RadialFunctionCore weightFunction) {
        this.weightFunction = weightFunction;
        weightFunction.setDiffOrder(0);
    }
    private double[] weightFunctionValue = new double[1];

    @Override
    public boolean isUpperSymmertric() {
        return true;
    }

    @Override
    public void assembleVolume() {
        double aimFunc = load[0];
        double wholeWeight = weight * weightFunction.values(aimFunc, weightFunctionValue)[0];
        TDoubleArrayList shapeFunc = shapeFunctionValues[0];
        for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
            int row = nodesAssemblyIndes.getQuick(i);
            double rowShapeFunc = shapeFunc.getQuick(i);
            mainVector.add(row, wholeWeight * aimFunc * rowShapeFunc);
            for (int j = 0; j < nodesAssemblyIndes.size(); j++) {
                int col = nodesAssemblyIndes.getQuick(j);
                if (isUpperSymmertric() && row > col) {
                    continue;
                }
                double colShapeFunc = shapeFunc.getQuick(j);
                mainMatrix.add(row, col, wholeWeight * rowShapeFunc * colShapeFunc);
            }
        }
    }

    @Override
    public void assembleDirichlet() {
        if (!loadValidity[0]) {
            return;
        }
        double aimFunc = load[0];
        double vectorWeight = aimFunc * weight;
        TDoubleArrayList shapeFunc = shapeFunctionValues[0];

        for (int j = 0; j < lagrangeAssemblyIndes.size(); j++) {
            int col = lagrangeAssemblyIndes.getQuick(j);
            double colShapeFunc = lagrangeShapeFunctionValue.getQuick(j);
            mainVector.add(col, -vectorWeight * colShapeFunc);
            for (int i = 0; i < nodesAssemblyIndes.size(); i++) {
                int row = nodesAssemblyIndes.getQuick(i);
                double rowShapeFunc = shapeFunc.getQuick(i);
                double matrixValue = -rowShapeFunc * colShapeFunc * weight;
                mainMatrix.add(row, col, matrixValue);
                if (!isUpperSymmertric()) {
                    mainMatrix.add(col, row, matrixValue);
                }
            }
        }
    }

    @Override
    public void assembleNeumann() {
    }

    @Override
    public int getVolumeDiffOrder() {
        return 0;
    }

    @Override
    public int getDirichletDiffOrder() {
        return 0;
    }

    @Override
    public int getNodeValueDimension() {
        return 1;
    }

    @Override
    public LevelSetApproximationAssemblier synchronizeClone() {
        LevelSetApproximationAssemblier result = new LevelSetApproximationAssemblier();
        result.setWeightFunction(weightFunction.synchronizeClone());
        result.setNodesNum(nodesNum);
        result.setDirichletNodesNum(dirichletNodesNum);
        result.prepare();
        return result;
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + String.format("{nodes*val: %d*%d, diff V/N/D: %d/%d/%d, "
                + "mat dense/sym: %b/%b, dirichlet lagrangian nodes: %d  weight function: %s}",
                getNodesNum(),
                getNodeValueDimension(),
                getVolumeDiffOrder(),
                getNeumannDiffOrder(),
                getDirichletDiffOrder(),
                isMatrixDense(),
                isUpperSymmertric(),
                getDirichletNodesNum(),
                weightFunction);
    }
}
