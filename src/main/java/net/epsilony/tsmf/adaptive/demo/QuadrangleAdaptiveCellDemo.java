/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.adaptive.demo;

import javax.swing.SwingUtilities;
import net.epsilony.tsmf.adaptive.AdaptiveCell;
import net.epsilony.tsmf.adaptive.QuadrangleAdaptiveCellFactory;
import net.epsilony.tsmf.util.TestTool;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class QuadrangleAdaptiveCellDemo extends AbstractAdaptiveCellDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new QuadrangleAdaptiveCellDemo().createDemoUI();
            }
        });
    }

    @Override
    protected AdaptiveCell[][] genCells() {
        AdaptiveCell[][] cells = QuadrangleAdaptiveCellFactory.byCoordGrid(
                TestTool.linSpace(0, 200, 10), TestTool.linSpace(100, 0, 5));
        return cells;
    }
}
