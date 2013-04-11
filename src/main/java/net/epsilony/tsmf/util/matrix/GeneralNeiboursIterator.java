/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.matrix;

import gnu.trove.iterator.TIntIterator;
import no.uib.cipr.matrix.Matrix;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class GeneralNeiboursIterator implements TIntIterator {

    Matrix mat;
    int nextNeibour = -1;
    int node;
    boolean upperSym;

    public GeneralNeiboursIterator(Matrix mat, int node, boolean upperSymmetric) {
        this.node = node;
        this.upperSym = upperSymmetric;
        this.mat = mat;
        if (!mat.isSquare()) {
            throw new IllegalArgumentException("mat should be square!");
        }
        findNext();
    }

    private void findNext() {
        do {
            nextNeibour++;
        } while (nextNeibour < mat.numColumns() && (nextNeibour == node || get(nextNeibour) == 0));
    }

    private double get(int index) {
        if (upperSym && index < node) {
            return mat.get(index, node);
        } else {
            return mat.get(node, index);
        }
    }

    @Override
    public int next() {
        int res = nextNeibour;
        findNext();
        return res;
    }

    @Override
    public boolean hasNext() {
        return nextNeibour < mat.numColumns();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
