/*
 * (c) Copyright by Man YUAN
 */
package net.epsilony.mf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.tb.MiscellaneousUtils;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.UpperSymmDenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public abstract class AbstractWeakformAssemblier implements WeakformAssemblier {

    protected ConstitutiveLaw constitutiveLaw;
    protected DenseMatrix constitutiveLawMatrixCopy;
    protected boolean dense;
    protected double[] load;
    protected boolean[] loadValidity;
    protected Matrix mainMatrix;
    protected DenseVector mainVector;
    protected TIntArrayList nodesAssemblyIndes;
    protected int nodesNum;
    protected TDoubleArrayList[] shapeFunctionValues;
    protected double weight;

    @Override
    public Matrix getMainMatrix() {
        return mainMatrix;
    }

    @Override
    public DenseVector getMainVector() {
        return mainVector;
    }

    @Override
    public int getNeumannDiffOrder() {
        return 0;
    }

    @Override
    public void prepare() {
        initMainMatrixVector();
    }

    protected final void initMainMatrixVector() {
        int numRowCol = getMainMatrixSize();
        if (dense) {
            if (isUpperSymmertric()) {
                mainMatrix = new UpperSymmDenseMatrix(numRowCol);
            } else {
                mainMatrix = new DenseMatrix(numRowCol, numRowCol);
            }
        } else {
            mainMatrix = new FlexCompRowMatrix(numRowCol, numRowCol);
        }
        mainVector = new DenseVector(numRowCol);
    }

    protected int getMainMatrixSize() {
        return getNodeValueDimension() * nodesNum;
    }

    @Override
    public boolean isMatrixDense() {
        return dense;
    }

    @Override
    public boolean isUpperSymmertric() {
        return constitutiveLaw.isSymmetric();
    }

    @Override
    public void mergeWithBrother(WeakformAssemblier otherAssemblier) {
        if (otherAssemblier.isUpperSymmertric() != isUpperSymmertric()) {
            throw new IllegalArgumentException("the assemblier to add in should be with same symmetricity");
        }
        Matrix otherMat = otherAssemblier.getMainMatrix();
        mainMatrix.add(otherMat);
        mainVector.add(otherAssemblier.getMainVector());
    }

    @Override
    public void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw) {
        this.constitutiveLaw = constitutiveLaw;
        constitutiveLawMatrixCopy = new DenseMatrix(constitutiveLaw.getMatrix());
    }

    @Override
    public void setLoad(double[] value, boolean[] validity) {
        this.load = value;
        this.loadValidity = validity;
    }

    @Override
    public void setMatrixDense(boolean dense) {
        this.dense = dense;
    }

    @Override
    public void setNodesNum(int nodesNum) {
        this.nodesNum = nodesNum;
    }

    @Override
    public int getNodesNum() {
        return nodesNum;
    }

    @Override
    public void setShapeFunctionValue(TIntArrayList nodesAssemblyIndes, TDoubleArrayList[] shapeFunValues) {
        this.nodesAssemblyIndes = nodesAssemblyIndes;
        this.shapeFunctionValues = shapeFunValues;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
