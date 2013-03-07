/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.implicit;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.util.SegmentHeadCoordIterable;
import net.epsilony.tsmf.util.Math2D;
import net.epsilony.tsmf.util.MiscellaneousUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author epsilon
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
        List<Segment2D> contourHeads = builder.contourHeads;

        assertEquals(expChainsSize, contourHeads.size());

        for (int i = 0; i < contourHeads.size(); i++) {
            double x0, y0, rad;
            Segment2D head = contourHeads.get(i);
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

            Segment2D seg = head;
            double actArea = 0;
            do {
                double[] headCoord = seg.getHeadCoord();
                double[] rearCoord = seg.getRearCoord();
                actArea += 0.5 * Math2D.cross(rearCoord[0] - headCoord[0], rearCoord[1] - headCoord[1],
                        x0 - headCoord[0], y0 - headCoord[1]);
                seg = seg.getSucc();
            } while (seg != head);
            assertEquals(expArea, actArea, Math.abs(expArea) * errRatio);
        }

    }
}