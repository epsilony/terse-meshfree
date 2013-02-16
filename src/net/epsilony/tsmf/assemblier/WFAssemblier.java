/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.assemblier;

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
public interface WFAssemblier extends NeedPreparation, SynchronizedClonable<WFAssemblier> {

    void asmBalance(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] volumnForce);

    void asmNeumann(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] neumannVal);

    void asmDirichlet(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] dirichletVal, boolean[] dirichletMark);

    void setNodesNum(int nodesNum);

    void setMatrixDense(boolean dense);

    void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw);

    Matrix getMainMatrix();

    DenseVector getMainVector();

    boolean isUpperSymmertric();

    void addToMainMatrix(WFAssemblier assemblier);
}
