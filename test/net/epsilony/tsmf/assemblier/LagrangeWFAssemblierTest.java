/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.assemblier;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tsmf.assemblier.LagrangeWeakformAssemblier;
import net.epsilony.tsmf.cons_law.RawConstitutiveLaw;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.VectorEntry;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LagrangeWFAssemblierTest {

    public LagrangeWFAssemblierTest() {
    }

    @Before
    public void setUp() {
    }
    TDoubleArrayList[] shapeFuncVal = new TDoubleArrayList[]{new TDoubleArrayList(new double[]{-1.1, 2.01, 3.42, 14, 50})};
    TIntArrayList nodesIds = new TIntArrayList(new int[]{5, 2, 0, 8, 6});
    int nodesSize = 6;
    int lagNodesSize = 4;
    double[] dirichletVal = new double[]{3.4, -1.2};
    double weight = 0.23;

    /**
     * Test of asmDirichlet method, of class LagrangeWeakformAssemblier.
     */
    @Test
    public void testAsmDirichlet() {
        double[][] exp_m = new double[][]{{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -171.0, -0, 0.0, 0.0, -47.88, -0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0, -171.0, 0.0, 0.0, -0, -47.88, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -100.5, -0, 0.0, 0.0, -28.14, -0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0, -100.5, 0.0, 0.0, -0, -28.14, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 55.0, -0, 0.0, 0.0, 15.4, -0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0, 55.0, 0.0, 0.0, -0, 15.4, 0.0, 0.0},
            {-171.0, -0, 0.0, 0.0, -100.5, -0, 0.0, 0.0, 0.0, 0.0, 55.0, -0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-0, -171.0, 0.0, 0.0, -0, -100.5, 0.0, 0.0, 0.0, 0.0, -0, 55.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-47.88, -0, 0.0, 0.0, -28.14, -0, 0.0, 0.0, 0.0, 0.0, 15.4, -0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {-0, -47.88, 0.0, 0.0, -0, -28.14, 0.0, 0.0, 0.0, 0.0, -0, 15.4, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}};


        double[] exp_v = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -170.0, 60.0, 0.0, 0.0, -47.6, 16.8, 0.0, 0.0};

        for (boolean upperSym : new boolean[]{true, false}) {
            LagrangeWeakformAssemblier lag = new LagrangeWeakformAssemblier();
            lag.setConstitutiveLaw(new RawConstitutiveLaw(upperSym, new DenseMatrix(3, 3)));
            lag.setNodesNum(nodesSize);
            lag.setMatrixDense(upperSym);
            lag.setDirichletNodesNums(lagNodesSize);
            lag.prepare();
            for (int test = 1; test <= 2; test++) {
                lag.asmDirichlet(weight, nodesIds, shapeFuncVal, dirichletVal, new boolean[]{true, true});
                Matrix mat = lag.getMainMatrix();
                DenseVector vec = lag.getMainVector();
                boolean getHere = false;
                for (MatrixEntry me : mat) {
                    double act = me.get();
                    double exp = exp_m[me.row()][me.column()] * weight * test;
                    assertEquals(exp, act, 1e-7);
                    getHere = true;
                }
                assertTrue(getHere);
                getHere = false;
                for (VectorEntry ve : vec) {
                    double act = ve.get();
                    double exp = exp_v[ve.index()] * weight * test;
                    assertEquals(exp, act, 1e-7);
                    getHere = true;
                }
                assertTrue(getHere);
            }
        }
    }
}
