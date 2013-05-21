/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface ConstitutiveLaw {

    DenseMatrix getMatrix();

    boolean isSymmetric();
}
