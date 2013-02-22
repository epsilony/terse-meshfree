/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.ui.demo;

import javax.swing.SwingUtilities;
import net.epsilony.tsmf.model.Polygon2D;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.ui.NodeDrawer;
import net.epsilony.tsmf.model.ui.PolygonDrawer;
import net.epsilony.tsmf.util.TestTool;
import net.epsilony.tsmf.util.ui.BasicModelPanel;
import net.epsilony.tsmf.util.ui.CommonFrame;

/**
 *
 * @author epsilon
 */
public class PolygonDrawerDemo {

    public static void createUI() {
        Polygon2D polygon = TestTool.samplePolygon(null);
        PolygonDrawer polygonDrawer = new PolygonDrawer(polygon);
        CommonFrame frame = new CommonFrame();
        BasicModelPanel mainPanel = frame.getMainPanel();
        mainPanel.addAndSetupModelDrawer(polygonDrawer);
        for (Segment2D seg : polygon) {
            mainPanel.addAndSetupModelDrawer(new NodeDrawer(seg.head));
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
