/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.cons_law;

import no.uib.cipr.matrix.DenseMatrix;

/**
 *
 * @author epsilon
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
