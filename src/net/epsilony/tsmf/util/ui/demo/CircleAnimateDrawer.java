/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import net.epsilony.tsmf.util.ui.AnimateModelDrawerAdapter;

/**
 *
 * @author epsilon
 */
public class CircleAnimateDrawer extends AnimateModelDrawerAdapter {

    public CircleAnimateDrawer(double x0, double y0, double maxRad) {
        this.x0 = x0;
        this.y0 = y0;
        this.maxRad = maxRad;
    }
    private static final double INITIAL_RADIUS_RATIO = 0.1;
    Color freezingColor = Color.BLUE;
    Color appearingColor = Color.RED;
    Color fadingColor = Color.GREEN;
    int initFrameEnd = 10;
    int enlargeFrameEnd = 35;
    int toFreezingFrameEnd = 45;
    double x0, y0, maxRad;
    int toOverFrameEnd = 30;

    @Override
    protected void drawWhenFreezing(Graphics2D g2, AffineTransform modelToComponent) {

        Ellipse2D ellipse = new Ellipse2D.Double(x0 - maxRad, y0 - maxRad, 2 * maxRad, 2 * maxRad);
        g2.setColor(freezingColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(modelToComponent.createTransformedShape(ellipse));
    }

    @Override
    protected void drawWhenAppearing(Graphics2D g2, AffineTransform modelToComponent) {

        Ellipse2D ellipse2D = getEllipse2DWhenAppear();
        g2.setColor(getColorWhenAppear());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(modelToComponent.createTransformedShape(ellipse2D));
    }

    private Color getColorWhenAppear() {
        Color color = null;
        int count = getAppearingFrameCount();
        if (count < initFrameEnd) {
            double colorRatio = count / (double) initFrameEnd;
            int alpha = (int) (appearingColor.getAlpha() * colorRatio);
            try {
                color = new Color(
                        appearingColor.getRed(),
                        appearingColor.getGreen(),
                        appearingColor.getBlue(), alpha);
            } catch (Throwable e) {
                System.out.println("here = ");
            }
        } else if (count < enlargeFrameEnd) {
            color = appearingColor;
        } else if (count < toFreezingFrameEnd) {
            float[] appearingColorRGBA = appearingColor.getRGBComponents(null);
            float[] freezingColorRGBA = freezingColor.getRGBComponents(null);
            float[] rgba = new float[4];
            float colorRation = (count - enlargeFrameEnd) / ((float) (toFreezingFrameEnd - enlargeFrameEnd));
            for (int i = 0; i < rgba.length; i++) {
                rgba[i] = appearingColorRGBA[i] * (1 - colorRation) + freezingColorRGBA[i] * colorRation;
            }
            color = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
        } else {
            color = freezingColor;
        }
        return color;
    }

    private Ellipse2D getEllipse2DWhenAppear() {
        double rad;
        int count = getAppearingFrameCount();
        if (count < initFrameEnd) {
            rad = INITIAL_RADIUS_RATIO * maxRad;
        } else if (count < enlargeFrameEnd) {
            double radRatio = INITIAL_RADIUS_RATIO
                    + (1 - INITIAL_RADIUS_RATIO) * (count - initFrameEnd + 1) / ((double) (enlargeFrameEnd - initFrameEnd));
            rad = maxRad * radRatio;
        } else {
            rad = maxRad;
        }
        return new Ellipse2D.Double(x0 - rad, y0 - rad, 2 * rad, 2 * rad);
    }

    @Override
    protected void drawWhenFading(Graphics2D g2, AffineTransform modelToComponent) {
        Ellipse2D ell;
        Color colorToFade;
        if (getAppearingFrameCount() < getAppearingFrameSize()) {
            ell = getEllipse2DWhenAppear();
            colorToFade = getColorWhenAppear();
        } else {
            colorToFade = freezingColor;
            ell = new Ellipse2D.Double(x0 - maxRad, y0 - maxRad, maxRad * 2, maxRad * 2);
        }
        double ratio = 1 - (getFadingFrameCount() + 1) / (double) getFadingFrameSize();
        Color color = new Color(
                colorToFade.getRed(),
                colorToFade.getGreen(),
                colorToFade.getBlue(),
                (int) (colorToFade.getAlpha() * ratio));
        g2.setColor(color);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(modelToComponent.createTransformedShape(ell));
    }

    @Override
    protected int getAppearingFrameSize() {
        return toFreezingFrameEnd;
    }

    @Override
    protected int getFadingFrameSize() {
        return toOverFrameEnd;
    }

    @Override
    public Rectangle2D getModelBounds() {
        return new Ellipse2D.Double(x0 - maxRad, y0 - maxRad, maxRad * 2, maxRad * 2).getBounds2D();
    }
}
