/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.weakform;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author epsilon
 */
public interface WFAssemblier {

    void asmBalance(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] volumnForce);

    void asmNeumann(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] neumannVal);

    void asmDirichlet(double weight, TIntArrayList nodesIds, TDoubleArrayList[] shapeFunVals, double[] dirichletVal, boolean[] dirichletMark);

    Matrix getMainMatrix();

    DenseVector getMainVector();
}
