/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.adaptive.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.adaptive.AdaptiveCell;
import net.epsilony.tsmf.adaptive.TriangleAdaptiveCellFactory;
import net.epsilony.tsmf.util.ui.ModelDrawer;
import net.epsilony.tsmf.util.ui.ModelDrawerAdapter;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class TriangleAdaptiveCellDemo extends AbstractAdaptiveCellDemo {

    public static double TRIANGLE_EDGE_LENGTH = 20;
    public static Rectangle2D TRIANGLE_COVERY_RANGE = new Rectangle2D.Double(5, 5, 100, 60);

    @Override
    protected AdaptiveCell[][] genCells() {
        return TriangleAdaptiveCellFactory.coverRectangle(TRIANGLE_COVERY_RANGE, TRIANGLE_EDGE_LENGTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TriangleAdaptiveCellDemo().createDemoUI();
            }
        });
    }

    @Override
    protected List<ModelDrawer> getExtraDrawers() {
        ModelDrawer drawer = new ModelDrawerAdapter() {
            @Override
            public Rectangle2D getBoundsInModelSpace() {
                return null;
            }

            @Override
            public void drawModel(Graphics2D g2) {
                g2.setColor(Color.GRAY);
                g2.draw(modelToComponentTransform.createTransformedShape(TRIANGLE_COVERY_RANGE));
            }
        };

        return Arrays.asList(drawer);
    }
}
