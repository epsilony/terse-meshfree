/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author epsilon
 */
public class SingleModelShapeDrawer extends ModelDrawerAdapter {

    public static boolean DEFAULT_FILLING;
    public static Color DEFAULT_LINE_COLOR = Color.BLACK;
    public static Color DEFAULT_FILLING_COLOR = Color.LIGHT_GRAY;
    public static double DEFAULT_LINE_ALPHA = 1;
    public static double DEFAULT_FILLING_ALPHA = 0.4;
    public static float DEFAULT_LINE_WIDTH = 0.5f;
    double fillingAlpha = DEFAULT_FILLING_ALPHA;
    Color fillingColor = DEFAULT_FILLING_COLOR;
    double lineAlpha = DEFAULT_LINE_ALPHA;
    Color lineColor = DEFAULT_LINE_COLOR;
    float lineWidth = DEFAULT_LINE_WIDTH;
    boolean filling = DEFAULT_FILLING;
    Rectangle2D modelBounds;
    private Shape shape;

    public SingleModelShapeDrawer(Shape shape) {
        _setShape(shape);
    }

    public SingleModelShapeDrawer() {
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        _setShape(shape);
    }

    protected final void _setShape(Shape shape) {
        this.shape = shape;
        if (null != shape) {
            modelBounds = shape.getBounds2D();
        }
    }

    @Override
    public Rectangle2D getBoundsInModelSpace() {
        return modelBounds;
    }

    @Override
    public void drawModel(Graphics2D g2) {
        if (null == shape) {
            return;
        }
        Color backColor = g2.getColor();
        Composite backComposite = g2.getComposite();
        Stroke backStroke = g2.getStroke();

        g2.setComposite(AlphaComposite.SrcOver);
        Shape polygonShape = getModelToComponentTransform().createTransformedShape(getShape());
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

        g2.setColor(backColor);
        g2.setStroke(backStroke);
        g2.setComposite(backComposite);
    }

    public double getFillingAlpha() {
        return fillingAlpha;
    }

    public Color getFillingColor() {
        return fillingColor;
    }

    public double getLineAlpha() {
        return lineAlpha;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public boolean isFilling() {
        return filling;
    }

    public void setFilling(boolean filling) {
        this.filling = filling;
    }

    public void setFillingAlpha(double fillingAlpha) {
        this.fillingAlpha = fillingAlpha;
    }

    public void setFillingColor(Color fillingColor) {
        this.fillingColor = fillingColor;
    }

    public void setLineAlpha(double lineAlpha) {
        this.lineAlpha = lineAlpha;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
}
