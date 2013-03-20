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

    void asmVolume(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] volumnForce);

    int volumeDiffOrder();

    void asmNeumann(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] neumannVal);

    int neumannDiffOrder();

    void asmDirichlet(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] dirichletVal, boolean[] dirichletMark);

    int dirichletDiffOrder();

    void setNodesNum(int nodesNum);

    void setMatrixDense(boolean dense);

    boolean isMatrixDense();

    void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw);

    Matrix getMainMatrix();

    DenseVector getMainVector();

    boolean isUpperSymmertric();

    void addToMainMatrix(WeakformAssemblier assemblier);
}
