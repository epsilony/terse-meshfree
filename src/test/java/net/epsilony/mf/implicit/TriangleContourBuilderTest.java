/* (c) Copyright by Man YUAN */
package net.epsilony.mf.implicit;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.solid.LinearSegment2D;
import net.epsilony.mf.model.util.SegmentHeadCoordIterable;
import net.epsilony.tb.Math2D;
import net.epsilony.tb.MiscellaneousUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleContourBuilderTest {

    public TriangleContourBuilderTest() {
    }

    @Test
    public void testDiskWithAHole() {
        TriangleContourCellFactory factory = new TriangleContourCellFactory();
        Rectangle2D range = new Rectangle2D.Double(0, 0, 100, 100);
        double edgeLength = 5;
        int expChainsSize = 2;
        double errRatio = 0.05;
        DiskWithAHoleLevelSetFunction levelsetFunction = new DiskWithAHoleLevelSetFunction();
        TriangleContourCell[][] cellsGrid = factory.coverRectangle(range, edgeLength);
        LinkedList<TriangleContourCell> cells = new LinkedList<>();
        MiscellaneousUtils.addToList(cellsGrid, cells);
        TriangleContourBuilder builder = new TriangleContourBuilder();
        builder.cells = cells;
        builder.levelSetFunction = levelsetFunction;
        builder.genContour();
        List<LinearSegment2D> contourHeads = builder.contourHeads;

        assertEquals(expChainsSize, contourHeads.size());

        for (int i = 0; i < contourHeads.size(); i++) {
            double x0, y0, rad;
            LinearSegment2D head = contourHeads.get(i);
            boolean b = Math2D.isAnticlockwise(new SegmentHeadCoordIterable(head));
            if (b) {
                x0 = levelsetFunction.diskX;
                y0 = levelsetFunction.diskY;
                rad = levelsetFunction.diskRad;
            } else {
                x0 = levelsetFunction.holeX;
                y0 = levelsetFunction.holeY;
                rad = levelsetFunction.holeRad;
            }
            double expArea = Math.PI * rad * rad;
            expArea *= b ? 1 : -1;

            LinearSegment2D seg = head;
            double actArea = 0;
            do {
                double[] headCoord = seg.getHeadCoord();
                double[] rearCoord = seg.getRearCoord();
                actArea += 0.5 * Math2D.cross(rearCoord[0] - headCoord[0], rearCoord[1] - headCoord[1],
                        x0 - headCoord[0], y0 - headCoord[1]);
                seg = (LinearSegment2D) seg.getSucc();
            } while (seg != head);
            assertEquals(expArea, actArea, Math.abs(expArea) * errRatio);
        }

    }
}