/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.matrix;

import gnu.trove.iterator.TIntIterator;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;
//import org.apache.log4j.Logger;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class FlexCompRowNeiboursIterator implements TIntIterator {

//    static Logger logger = Logger.getLogger(FlexCompRowNeiboursIterator.class);
    FlexCompRowMatrix mat;
    boolean upperSymmetric;
    int node;
    int nodeSparseIndex;
    int nextNeighbor = -1;
    int nextNeighborSparceIndex = -1;
    int[] rowIndes;
    double[] rowData;

    public FlexCompRowNeiboursIterator(FlexCompRowMatrix mat, int node, boolean upperSymmetric) {
        this.mat = mat;
        if (!mat.isSquare()) {
            throw new IllegalArgumentException("mat should be square!");
        }
        this.upperSymmetric = upperSymmetric;
        this.node = node;
        SparseVector row = mat.getRow(node);
        rowIndes = row.getIndex();
        rowData = row.getData();
        for (int i = 0; i < rowIndes.length; i++) {
            if (rowIndes[i] == node) {
                nodeSparseIndex = i;
                break;
            }
        }
        if (nodeSparseIndex > rowIndes.length) {
            throw new IllegalArgumentException("node " + node + " is absent!");
        }

//        if (upperSymmetric && nodeSparseIndex > 0) {
//            logger.warn("mat is not strictly upper symmetric\nthere is a lower element " + row.get(rowIndes[0]) + " at (" + node + ", " + rowIndes[0] + ")");
//        }

        findNext();
    }

    private void findNext() {
        if (upperSymmetric && nextNeighbor < node) {
            {
                do {
                    nextNeighbor++;
                } while (nextNeighbor < node && (mat.get(nextNeighbor, node) == 0));
                if (nextNeighbor == node) {
                    nextNeighborSparceIndex = nodeSparseIndex;
                    findNext();
                }
            }
        } else {
            do {
                nextNeighborSparceIndex++;
            } while (nextNeighborSparceIndex < rowIndes.length
                    && (nextNeighborSparceIndex == nodeSparseIndex
                    || rowData[nextNeighborSparceIndex] == 0));
            if (nextNeighborSparceIndex < rowIndes.length) {
                nextNeighbor = rowIndes[nextNeighborSparceIndex];
            }
        }
    }

    @Override
    public int next() {
        int res = nextNeighbor;
        findNext();
        return res;
    }

    @Override
    public boolean hasNext() {
        if (upperSymmetric && nextNeighbor < node) {
            return true;
        } else {
            return nextNeighborSparceIndex < rowIndes.length;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
