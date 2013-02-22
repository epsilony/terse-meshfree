/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.util.ui.ModelDrawerAdapter;

/**
 *
 * @author epsilon
 */
public class NodeDrawer extends ModelDrawerAdapter {

    public static Color DEFAULT_COLOR = Color.BLACK;
    public static double DEFAULT_RAD = 3;
    public static float DEFAULT_LINE_WIDTH = 2;
    Node node;
    Color color = DEFAULT_COLOR;
    double rad = DEFAULT_RAD;
    private float lineWidth = DEFAULT_LINE_WIDTH;
    private Stroke stroke=new BasicStroke(lineWidth);

    public NodeDrawer(Node node) {
        this.node = node; 
    }

    public NodeDrawer() {
    }

    @Override
    public void drawModel(Graphics2D g2) {
        double[] center = getCenterOnComponent();
        Ellipse2D circle = new Ellipse2D.Double(center[0] - rad, center[1] - rad, rad * 2, rad * 2);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke strokeBack = g2.getStroke();
        g2.setStroke(stroke);
        g2.setColor(color);
        g2.draw(circle);
        g2.setStroke(strokeBack);
    }

    @Override
    public Rectangle2D getBoundsInModelSpace() {
        return new Rectangle2D.Double(node.coord[0], node.coord[1], 0, 0);
    }

    private double[] getCenterOnComponent() {
        double[] point = new double[2];
        modelToComponentTransform.transform(node.coord, 0, point, 0, 1);
        return point;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getRad() {
        return rad;
    }

    public void setRad(double rad) {
        this.rad = rad;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
}
