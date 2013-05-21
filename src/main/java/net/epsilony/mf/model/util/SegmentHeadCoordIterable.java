/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.util;

import java.util.Iterator;
import net.epsilony.tb.solid.Segment2D;

/**
 *
 * @author <a href="mailto:epsionyuan@gmail.com">Man YUAN</a>
 */
public class SegmentHeadCoordIterable implements Iterable<double[]> {

    private Segment2D head;

    public SegmentHeadCoordIterable(Segment2D head) {
        this.head = head;
    }

    @Override
    public Iterator<double[]> iterator() {
        return new SegmentHeadCoordinateIterator();
    }

    private class SegmentHeadCoordinateIterator implements Iterator<double[]> {

        Segment2D nextSegment = head;

        @Override
        public boolean hasNext() {
            return null != nextSegment;
        }

        @Override
        public double[] next() {
            double[] result = nextSegment.getHeadCoord();
            nextSegment = nextSegment.getSucc();
            if (nextSegment.getPred().getSucc() != nextSegment) {
                throw new IllegalStateException("Segment link is broken");
            }
            if (nextSegment == head) {
                nextSegment = null;
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
