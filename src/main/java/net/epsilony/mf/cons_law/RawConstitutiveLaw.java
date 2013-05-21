/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawConstitutiveLaw implements ConstitutiveLaw {

    boolean isSym;
    DenseMatrix matrix;

    @Override
    public DenseMatrix getMatrix() {
        return matrix;
    }

    @Override
    public boolean isSymmetric() {
        return isSym;
    }

    public RawConstitutiveLaw(boolean isSym, DenseMatrix matrix) {
        this.isSym = isSym;
        this.matrix = matrix;
    }
}
