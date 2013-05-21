/* (c) Copyright by Man YUAN */
package net.epsilony.tb.adaptive;

import java.awt.geom.Rectangle2D;
import net.epsilony.tb.Math2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleAdaptiveCellTest extends AbstractAdaptiveCellTest {

    public Rectangle2D rectangle = new Rectangle2D.Double(10, 10, 110, 70);
    double edgeLength = 20;
    int testTime = 2000;

    @Override
    public AdaptiveCell[][] genSampleGrid() {
        return new TriangleAdaptiveCellFactory().coverRectangle(rectangle, edgeLength);
    }

    @Override
    protected int getTestTime() {
        return testTime;
    }

    @Override
    protected double getCellArea(AdaptiveCell cell) {
        AdaptiveCellEdge[] edges = cell.getEdges();
        double[] c1 = edges[0].getHeadCoord();
        double[] c2 = edges[1].getHeadCoord();
        double[] c3 = edges[2].getHeadCoord();
        double cross = Math2D.cross(c2[0] - c1[0], c2[1] - c1[1], c3[0] - c1[0], c3[1] - c1[1]);
        return 0.5 * Math.abs(cross);
    }
}
