/* (c) Copyright by Man YUAN */
package net.epsilony.tb.solid.ui.demo;

import javax.swing.SwingUtilities;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.LinearSegment2D;
import net.epsilony.tb.solid.ui.NodeDrawer;
import net.epsilony.tb.solid.ui.PolygonDrawer;
import net.epsilony.tb.TestTool;
import net.epsilony.tb.ui.BasicModelPanel;
import net.epsilony.tb.ui.CommonFrame;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PolygonDrawerDemo {

    public static void createUI() {
        Polygon2D polygon = TestTool.samplePolygon(null);
        PolygonDrawer polygonDrawer = new PolygonDrawer(polygon);
        CommonFrame frame = new CommonFrame();
        BasicModelPanel mainPanel = frame.getMainPanel();
        mainPanel.addAndSetupModelDrawer(polygonDrawer);
        for (LinearSegment2D seg : polygon) {
            mainPanel.addAndSetupModelDrawer(new NodeDrawer(seg.getHead()));
        }
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createUI();
            }
        });
    }
}
