/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Polygon2D;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.TestTool;
import net.epsilony.tsmf.util.ui.BasicModelPanel;
import net.epsilony.tsmf.util.ui.CommonFrame;
import net.epsilony.tsmf.util.ui.ModelDrawerAdapter;

/**
 *
 * @author epsilon
 */
public class PolygonDrawer extends ModelDrawerAdapter {

    public static final Color DEFAULT_LINE_COLOR = Color.BLACK;
    public static final Color DEFAULT_FILLING_COLOR = Color.LIGHT_GRAY;
    public static final double DEFAULT_LINE_ALPHA = 1;
    public static final double DEFAULT_FILLING_ALPHA = 0.4;
    public static final float DEFAULT_LINE_WIDTH = 0.5f;
    Polygon2D polygon;
    GeneralPath polygonPath;
    boolean filling = false;
    float lineWidth = DEFAULT_LINE_WIDTH;
    Color lineColor = DEFAULT_LINE_COLOR;
    Color fillingColor = DEFAULT_FILLING_COLOR;
    double lineAlpha = DEFAULT_LINE_ALPHA;
    double fillingAlpha = DEFAULT_FILLING_ALPHA;
    private Rectangle2D modelBounds;

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

    public void setPolygon(Polygon2D polygon) {
        this.polygon = polygon;
        polygonPath = genGeneralPath(polygon);
        modelBounds = polygonPath.getBounds2D();
    }

    @Override
    public Rectangle2D getModelBounds() {
        return modelBounds;
    }

    @Override
    public void drawModel(Graphics2D g2, AffineTransform modelToComponent) {
        g2.setComposite(AlphaComposite.SrcOver);
        Shape polygonShape = modelToComponent.createTransformedShape(polygonPath);
        if (isFilling()) {
            g2.setColor(fillingColor);
            g2.fill(polygonShape);
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getLineWidth() > 0) {
            g2.setStroke(new BasicStroke(getLineWidth()));
        }
        g2.setColor(lineColor);
        g2.draw(polygonShape);
    }

    public boolean isFilling() {
        return filling;
    }

    public void setFilling(boolean filling) {
        this.filling = filling;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public Color getFillingColor() {
        return fillingColor;
    }

    public void setFillingColor(Color fillingColor) {
        this.fillingColor = fillingColor;
    }

    public double getLineAlpha() {
        return lineAlpha;
    }

    public void setLineAlpha(double lineAlpha) {
        this.lineAlpha = lineAlpha;
    }

    public double getFillingAlpha() {
        return fillingAlpha;
    }

    public void setFillingAlpha(double fillingAlpha) {
        this.fillingAlpha = fillingAlpha;
    }

    public static void main(String[] args) {
        Runnable createUI = new Runnable() {
            @Override
            public void run() {
                CommonFrame frame = new CommonFrame();
                BasicModelPanel basicModelPanel = frame.getBasicModelPanel();
                frame.setDefaultModelOriginAndScale(10, 500, 1);
                frame.setSize(800, 600);
                Polygon2D polygon = TestTool.samplePolygon(null);
                PolygonDrawer drawer = new PolygonDrawer();
                drawer.setPolygon(polygon);
                basicModelPanel.addAndConnectModelDrawer(drawer);
                frame.setVisible(true);
                basicModelPanel.setZoomAllNeeded(true);
                frame.getMainPanel().repaint();
            }
        };

        SwingUtilities.invokeLater(createUI);
    }
}
