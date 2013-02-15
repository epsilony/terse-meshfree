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
        double[][][] gridCoords=new double[ys.length][xs.length][2];
        for(int i=0;i<gridCoords.length;i++){
            for(int j=0;j<gridCoords[i].length;j++){
                gridCoords[i][j][0]=xs[j];
                gridCoords[i][j][1]=ys[i];
            }
        }
        return QuadrangleAdaptiveCellFactory.byCoordGrid(gridCoords);
    }
}
