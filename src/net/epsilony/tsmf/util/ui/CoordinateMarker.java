/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 *
 * @author epsilon
 */
public class CoordinateMarker extends ModelDrawerAdater {

    private static final Point2D ORIGIN = new Point2D.Float(0, 0);
    public static final boolean DEFAULT_VISIBLE = true;
    public static final float DEFAULT_AXIS_LENGTH = 20;
    public static final Color DEFAULT_X_COLOR = Color.RED;
    public static final Color DEFAULT_Y_COLOR = Color.GREEN;
    float axisLength = DEFAULT_AXIS_LENGTH;
    float strokeWidth = -1;
    Color xAxisColor = DEFAULT_X_COLOR;
    Color yAxisColor = DEFAULT_Y_COLOR;

    public CoordinateMarker(boolean visible) {
        this.visible = visible;
    }

    public CoordinateMarker() {
        this(DEFAULT_VISIBLE);
    }

    @Override
    public void drawPhysicalModel(Graphics2D g2, AffineTransform physcialToComponentTransform) {
        Point2D oriOnComponent = physcialToComponentTransform.transform(ORIGIN, null);
        GeneralPath path = new GeneralPath();
        if (strokeWidth > 0) {
            g2.setStroke(new BasicStroke(strokeWidth));
        }
        g2.setColor(xAxisColor);
        path.moveTo(oriOnComponent.getX(), oriOnComponent.getY());
        path.lineTo(oriOnComponent.getX() + axisLength, oriOnComponent.getY());
        g2.draw(path);
        g2.setColor(yAxisColor);
        path.reset();
        path.moveTo(oriOnComponent.getX(), oriOnComponent.getY());
        path.lineTo(oriOnComponent.getX(), oriOnComponent.getY() - axisLength);
        g2.draw(path);
    }

    public float getAxisLength() {
        return axisLength;
    }

    public void setAxisLength(float axisLength) {
        this.axisLength = axisLength;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public Color getxAxisColor() {
        return xAxisColor;
    }

    public void setxAxisColor(Color xAxisColor) {
        this.xAxisColor = xAxisColor;
    }

    public Color getyAxisColor() {
        return yAxisColor;
    }

    public void setyAxisColor(Color yAxisColor) {
        this.yAxisColor = yAxisColor;
    }
}
