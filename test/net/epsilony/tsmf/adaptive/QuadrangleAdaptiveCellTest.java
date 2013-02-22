/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive;

import net.epsilony.tsmf.util.TestTool;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author epsilon
 */
public class QuadrangleAdaptiveCellTest {

    public QuadrangleAdaptiveCellTest() {
    }

    @Test
    public void testFission() {
        QuadrangleAdaptiveCell[][] sampleGrid = genSampleGrid();
        sampleGrid[1][1].fissionToChildren();
        sampleGrid[2][2].fissionToChildren();
        sampleGrid[1][2].fissionToChildren();
        sampleGrid[0][2].fissionToChildren();
        sampleGrid[2][2].children[1].fissionToChildren();
        assertEquals(4,sampleGrid[2][2].children[1].children.length);
        //TODO: make an check function to check opposites
    }

    public QuadrangleAdaptiveCell[][] genSampleGrid() {
        double[] ys=TestTool.linSpace(3, 0, 4);
        double[] xs=TestTool.linSpace(0, 3, 4);
        return QuadrangleAdaptiveCellFactory.byCoordGrid(xs,ys);
    }
}
