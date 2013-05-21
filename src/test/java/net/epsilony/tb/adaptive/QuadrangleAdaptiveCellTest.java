/* (c) Copyright by Man YUAN */
package net.epsilony.tb.adaptive;

import net.epsilony.tb.TestTool;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class QuadrangleAdaptiveCellTest extends AbstractAdaptiveCellTest {

    public QuadrangleAdaptiveCellTest() {
    }
    int randomTime = 2000;

    @Override
    public QuadrangleAdaptiveCell[][] genSampleGrid() {
        double[] ys = TestTool.linSpace(4, 0, 4);
        double[] xs = TestTool.linSpace(0, 3, 4);
        return QuadrangleAdaptiveCellFactory.byCoordGrid(xs, ys);
    }

    @Override
    protected int getTestTime() {
        return randomTime;
    }

    @Override
    protected double getCellArea(AdaptiveCell cell) {
        AdaptiveCellEdge[] edges = cell.getEdges();
        double[] vetes = edges[0].getHeadCoord();
        double[] vetes2 = edges[2].getHeadCoord();
        double cellArea = (vetes[0] - vetes2[0]) * (vetes[1] - vetes2[1]);
        cellArea = Math.abs(cellArea);
        return cellArea;
    }
}
