/* (c) Copyright by Man YUAN */
package net.epsilony.tb.matrix;

import gnu.trove.list.array.TIntArrayList;
import no.uib.cipr.matrix.DenseMatrix;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeneralNeiboursIteratorTest {

    public GeneralNeiboursIteratorTest() {
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
        GeneralNeiboursIterator iter = new GeneralNeiboursIterator(denseMatrix, 3, true);
        TIntArrayList acts = new TIntArrayList();
        while (iter.hasNext()) {
            acts.add(iter.next());
        }
        int[] exp = new int[]{0, 2, 5, 7, 9};
        assertArrayEquals(exp, acts.toArray());

        acts = new TIntArrayList();
        denseMatrix.set(3, 1, 11);
        iter = new GeneralNeiboursIterator(denseMatrix, 3, false);
        while (iter.hasNext()) {
            acts.add(iter.next());
        }
        exp = new int[]{1, 5, 7, 9};
        assertArrayEquals(exp, acts.toArray());

        acts = new TIntArrayList();
        denseMatrix.set(3, 1, 11);
        iter = new GeneralNeiboursIterator(denseMatrix, 3, true);
        while (iter.hasNext()) {
            acts.add(iter.next());
        }
        exp = new int[]{0, 2, 5, 7, 9};
        assertArrayEquals(exp, acts.toArray());
    }
}
