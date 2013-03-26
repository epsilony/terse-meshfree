/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import net.epsilony.tsmf.util.NeedPreparation;
import net.epsilony.tsmf.util.synchron.SynchronizedClonable;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WeakformAssemblier extends NeedPreparation, SynchronizedClonable<WeakformAssemblier> {

    void asmVolume();

    void asmDirichlet();

    void asmNeumann();

    void setWeight(double weight);

    void setShapeFunctionValues(TIntArrayList nodesAssemblyIndes, TDoubleArrayList[] shapeFunValues);

    void setLoad(double[] value, boolean[] validity);

    int getVolumeDiffOrder();

    int getNeumannDiffOrder();

    int getDirichletDiffOrder();

    void setNodesNum(int nodesNum);

    void setMatrixDense(boolean dense);

    boolean isMatrixDense();

    void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw);

    Matrix getMainMatrix();

    DenseVector getMainVector();

    boolean isUpperSymmertric();

    void mergeWithBrother(WeakformAssemblier brother);

    int getNodeValueDimension();
}
