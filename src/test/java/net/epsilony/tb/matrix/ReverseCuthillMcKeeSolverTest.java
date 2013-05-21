/* (c) Copyright by Man YUAN */
package net.epsilony.tb.matrix;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.UpperSPDDenseMatrix;
import no.uib.cipr.matrix.UpperSymmDenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompColMatrix;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ReverseCuthillMcKeeSolverTest {

    public ReverseCuthillMcKeeSolverTest() {
    }

    @Test
    public void testSolve() {
        double[][] sample = new double[][]{
            {1, 0, 0, 4, 0, 6, 0, 0, 0, 0},
            {0, 2, 3, 0, 5, 0, 7, 0, 0, 10},
            {0, 2, 3, 4, 5, 0, 0, 0, 0, 0},
            {1, 0, 3, 4, 0, 6, 0, 0, 9, 0},
            {0, 2, 3, 0, 5, 0, 7, 0, 0, 0},
            {1, 0, 0, 4, 0, 6, 7, 8, 0, 0},
            {0, 2, 0, 0, 5, 6, 7, 8, 0, 0},
            {0, 0, 0, 0, 0, 6, 7, 8, 0, 0},
            {0, 0, 0, 4, 0, 0, 0, 0, 9, 0},
            {0, 2, 0, 0, 0, 0, 0, 0, 0, 10}
        };
        DenseMatrix dense = new DenseMatrix(sample);
        //DenseMatrix dense=new DenseMatrix(10,10);
        for (int i = 0; i < 10; i++) {
            dense.add(i, i, 100);
        }
        DenseVector b = new DenseVector(dense.numRows());
        for (int i = 0; i < b.size(); i++) {
            b.set(i, 1);
        }
        ReverseCuthillMcKeeSolver rcm = new ReverseCuthillMcKeeSolver(dense, false);
        System.out.println(rcm.getOriginalBandWidth());
        System.out.println(rcm.getOptimizedBandWidth());

        DenseVector exp = (DenseVector) dense.solve(b, new DenseVector(b.size()));
        DenseVector act = rcm.solve(b);
        assertArrayEquals(exp.getData(), act.getData(), 1e-10);

        FlexCompColMatrix flexMat = new FlexCompColMatrix(dense);
        act = new ReverseCuthillMcKeeSolver(flexMat, false).solve(b);
        assertArrayEquals(exp.getData(), act.getData(), 1e-10);

        UpperSymmDenseMatrix uMat = new UpperSPDDenseMatrix(dense);
        uMat.solve(b, exp);
        act = new ReverseCuthillMcKeeSolver(uMat, true).solve(b);
        assertArrayEquals(exp.getData(), act.getData(), 1e-10);

        flexMat = new FlexCompColMatrix(uMat);
        act = new ReverseCuthillMcKeeSolver(flexMat, true).solve(b);
        assertArrayEquals(exp.getData(), act.getData(), 1e-10);
    }
}
