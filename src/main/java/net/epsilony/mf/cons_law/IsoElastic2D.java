/* (c) Copyright by Man YUAN */
package net.epsilony.mf.cons_law;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.UpperSymmDenseMatrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class IsoElastic2D implements ConstitutiveLaw {

    double E, nu;
    DenseMatrix matrix;

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public DenseMatrix getMatrix() {
        return matrix;
    }

    public IsoElastic2D(double E, double nu, DenseMatrix matrix) {
        this.E = E;
        this.nu = nu;
        this.matrix = matrix;
    }

    public static IsoElastic2D planeStressMatrix(double E, double nu) {
        double t = E / (1 - nu * nu);
        UpperSymmDenseMatrix mat = new UpperSymmDenseMatrix(3);
        mat.set(0, 0, t);
        mat.set(0, 1, nu * t);
        mat.set(1, 1, t);
        mat.set(2, 2, (1 - nu) / 2 * t);
        return new IsoElastic2D(E, nu, new DenseMatrix(mat));
    }
}
