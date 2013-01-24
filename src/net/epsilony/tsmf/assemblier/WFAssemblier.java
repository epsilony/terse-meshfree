/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tsmf.cons_law.ConstitutiveLaw;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface WFAssemblier {

    void asmBalance(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] volumnForce);

    void asmNeumann(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] neumannVal);

    void asmDirichlet(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] dirichletVal, boolean[] dirichletMark);

    void setNodesNum(int nodesNum);

    void setMatrixDense(boolean dense);

    void setConstitutiveLaw(ConstitutiveLaw constitutiveLaw);

    void prepare();

    Matrix getMainMatrix();

    DenseVector getMainVector();

    boolean isUpperSymmertric();
}
