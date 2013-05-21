/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.mf.cons_law.ConstitutiveLaw;
import net.epsilony.tb.NeedPreparation;
import net.epsilony.tb.synchron.SynchronizedClonable;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WeakformAssemblier extends NeedPreparation, SynchronizedClonable<WeakformAssemblier> {

    void assembleVolume();

    void assembleDirichlet();

    void assembleNeumann();

    void setWeight(double weight);

    void setShapeFunctionValue(TIntArrayList nodesAssemblyIndes, TDoubleArrayList[] shapeFunValues);

    void setLoad(double[] value, boolean[] validity);

    int getVolumeDiffOrder();

    int getNeumannDiffOrder();

    int getDirichletDiffOrder();

    void setNodesNum(int nodesNum);

    int getNodesNum();

    void setMatrixDense(boolean dense);

    boolean isMatrixDense();

    void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw);

    Matrix getMainMatrix();

    DenseVector getMainVector();

    boolean isUpperSymmertric();

    void mergeWithBrother(WeakformAssemblier brother);

    int getNodeValueDimension();
}
