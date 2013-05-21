/* (c) Copyright by Man YUAN */
package net.epsilony.tb.solid.ui;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.LinearSegment2D;
import net.epsilony.tb.TestTool;
import net.epsilony.tb.ui.BasicModelPanel;
import net.epsilony.tb.ui.CommonFrame;
import net.epsilony.tb.ui.SingleModelShapeDrawer;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PolygonDrawer extends SingleModelShapeDrawer {

    Polygon2D polygon;

    public static GeneralPath genGeneralPath(Polygon2D polygon) {
        GeneralPath path = new GeneralPath();
        ArrayList<LinearSegment2D> chainsHeads = polygon.getChainsHeads();
        for (LinearSegment2D chainHead : chainsHeads) {
            Node nd = chainHead.getHead();
            path.moveTo(nd.getCoord()[0], nd.getCoord()[1]);

            LinearSegment2D seg = chainHead;
            do {
                seg = (LinearSegment2D) seg.getSucc();
                nd = seg.getHead();
                path.lineTo(nd.getCoord()[0], nd.getCoord()[1]);
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
