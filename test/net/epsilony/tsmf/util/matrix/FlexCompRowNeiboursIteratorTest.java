/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.matrix;

import gnu.trove.list.array.TIntArrayList;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class FlexCompRowNeiboursIteratorTest {

    public FlexCompRowNeiboursIteratorTest() {
    }

    @Test
    public void testTheWholeIterator() {
        DenseMatrix denseMatrix = new DenseMatrix(new double[][]{
                    {00, 00, 00, 11, 00, 00, 00, 00, 00, 00},
                    {00, 00, 00, 00, 00, 00, 00, 00, 00, 00},
                    {00, 00, 00, 11, 00, 00, 00, 00, 00, 00},
                    {00, 00, 00, 11, 00, 11, 00, 11, 00, 11},
                    {00, 00, 00, 00, 00, 00, 00, 00, 00, 00},
                    {00, 00, 00, 00, 00, 00, 00, 00, 00, 00},
                    {00, 00, 00, 00, 00, 00, 00, 00, 00, 00},
                    {00, 00, 00, 00, 00, 00, 00, 00, 00, 00},
                    {00, 00, 00, 00, 00, 00, 00, 00, 00, 00},
                    {00, 00, 00, 00, 00, 00, 00, 00, 00, 00},});

        FlexCompRowMatrix flexMat = new FlexCompRowMatrix(denseMatrix.numRows(), denseMatrix.numColumns());
        for (MatrixEntry me : denseMatrix) {
            if (me.get() != 0) {
                flexMat.add(me.row(), me.column(), me.get());
            }
        }
        FlexCompRowNeiboursIterator iter = new FlexCompRowNeiboursIterator(flexMat, 3, true);
        TIntArrayList acts = new TIntArrayList();
        while (iter.hasNext()) {
            acts.add(iter.next());
        }
        int[] exp = new int[]{0, 2, 5, 7, 9};
        assertArrayEquals(exp, acts.toArray());

        flexMat.set(3, 1, 11);
        iter = new FlexCompRowNeiboursIterator(flexMat, 3, false);
        acts = new TIntArrayList();
        while (iter.hasNext()) {
            acts.add(iter.next());
        }
        exp = new int[]{1, 5, 7, 9};
        assertArrayEquals(exp, acts.toArray());


        flexMat.set(3, 1, 11);
        iter = new FlexCompRowNeiboursIterator(flexMat, 3, true);
        acts = new TIntArrayList();
        while (iter.hasNext()) {
            acts.add(iter.next());
        }
        exp = new int[]{0, 2, 5, 7, 9};
        assertArrayEquals(exp, acts.toArray());
    }
}
