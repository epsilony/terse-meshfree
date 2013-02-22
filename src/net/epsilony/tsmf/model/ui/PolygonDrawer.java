/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.ui;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Polygon2D;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.TestTool;
import net.epsilony.tsmf.util.ui.BasicModelPanel;
import net.epsilony.tsmf.util.ui.CommonFrame;
import net.epsilony.tsmf.util.ui.SingleModelShapeDrawer;

/**
 *
 * @author epsilon
 */
public class PolygonDrawer extends SingleModelShapeDrawer {

    Polygon2D polygon;

    public static GeneralPath genGeneralPath(Polygon2D polygon) {
        GeneralPath path = new GeneralPath();
        ArrayList<Segment2D> chainsHeads = polygon.getChainsHeads();
        for (Segment2D chainHead : chainsHeads) {
            Node nd = chainHead.getHead();
            path.moveTo(nd.coord[0], nd.coord[1]);

            Segment2D seg = chainHead;
            do {
                seg = seg.succ;
                nd = seg.getHead();
                path.lineTo(nd.coord[0], nd.coord[1]);
            } while (seg != chainHead);
            path.closePath();
        }
        return path;
    }

    public PolygonDrawer() {
    }

    public PolygonDrawer(Polygon2D polygon) {
        _setPolygon(polygon);
    }

    public void setPolygon(Polygon2D polygon) {
        _setPolygon(polygon);
    }

    private void _setPolygon(Polygon2D polygon) {
        this.polygon = polygon;
        Shape polygonPath = genGeneralPath(polygon);
        _setShape(polygonPath);
    }

    public static void main(String[] args) {
        Runnable createUI = new Runnable() {
            @Override
            public void run() {
                CommonFrame frame = new CommonFrame();
                BasicModelPanel basicModelPanel = frame.getMainPanel();
                basicModelPanel.setPreferredSize(new Dimension(800, 600));
                Polygon2D polygon = TestTool.samplePolygon(null);
                PolygonDrawer drawer = new PolygonDrawer();
                drawer.setPolygon(polygon);
                basicModelPanel.addAndSetupModelDrawer(drawer);
                frame.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(createUI);
    }
}
