/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import net.epsilony.tsmf.util.ui.AnimateModelDrawerAdapter;
import static net.epsilony.tsmf.util.ui.AnimationStatus.*;

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
    private final long appearStartEnd = 10;
    private final long appearEnlargeEnd = 35;
    private final long appearEnd = 45;
    private final long fadeEnd = 30;
    double x0, y0, maxRad;

    protected void drawWhenFreezing(Graphics2D g2) {

        Ellipse2D ellipse = new Ellipse2D.Double(x0 - maxRad, y0 - maxRad, 2 * maxRad, 2 * maxRad);
        g2.setColor(freezingColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(modelToComponentTransform.createTransformedShape(ellipse));
    }

    protected void drawWhenAppearing(Graphics2D g2) {
        Ellipse2D ellipse2D = getEllipse2DWhenAppear();
        g2.setColor(getColorWhenAppear());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(modelToComponentTransform.createTransformedShape(ellipse2D));
        if (getFrame(APPEARING) + 1 >= appearEnd) {
            switchStatus(FREEZING);
        }
    }

    private Color getColorWhenAppear() {
        Color color = null;
        long frame = getFrame(APPEARING);
        if (frame < appearStartEnd) {
            double colorRatio = frame / (double) appearStartEnd;
            int alpha = (int) (appearingColor.getAlpha() * colorRatio);
            try {
                color = new Color(
                        appearingColor.getRed(),
                        appearingColor.getGreen(),
                        appearingColor.getBlue(), alpha);
            } catch (Throwable e) {
                System.out.println("here = ");
            }
        } else if (frame < appearEnlargeEnd) {
            color = appearingColor;
        } else if (frame < appearEnlargeEnd) {
            float[] appearingColorRGBA = appearingColor.getRGBComponents(null);
            float[] freezingColorRGBA = freezingColor.getRGBComponents(null);
            float[] rgba = new float[4];
            float colorRation = (frame - appearEnlargeEnd) / ((float) (appearEnd - appearEnlargeEnd));
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
        long frame = getFrame(APPEARING);
        if (frame < appearStartEnd) {
            rad = INITIAL_RADIUS_RATIO * maxRad;
        } else if (frame < appearEnlargeEnd) {
            double radRatio = INITIAL_RADIUS_RATIO
                    + (1 - INITIAL_RADIUS_RATIO) * (frame - appearStartEnd + 1)
                    / ((double) (appearEnlargeEnd - appearStartEnd));
            rad = maxRad * radRatio;
        } else {
            rad = maxRad;
        }
        return new Ellipse2D.Double(x0 - rad, y0 - rad, 2 * rad, 2 * rad);
    }

    protected void drawWhenFading(Graphics2D g2) {
        Ellipse2D ell;
        Color colorToFade;
        if (getFrame(APPEARING) < appearEnd) {
            ell = getEllipse2DWhenAppear();
            colorToFade = getColorWhenAppear();
        } else {
            colorToFade = freezingColor;
            ell = new Ellipse2D.Double(x0 - maxRad, y0 - maxRad, maxRad * 2, maxRad * 2);
        }
        long frame = getFrame(FADING);
        double fadeFrameSize = Math.ceil(fadeEnd * (getFrame(APPEARING) + 1) / (double) appearEnd);
        double ratio = 1 - (frame + 1) / fadeFrameSize;
        if (ratio < 0) {
            ratio = 0;
        }
        Color color = new Color(
                colorToFade.getRed(),
                colorToFade.getGreen(),
                colorToFade.getBlue(),
                (int) (colorToFade.getAlpha() * ratio));
        g2.setColor(color);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(modelToComponentTransform.createTransformedShape(ell));
        if (frame + 1 >= fadeFrameSize) {
            switchStatus(OVER);
        }
    }

    @Override
    public Rectangle2D getBoundsInModelSpace() {
        return new Ellipse2D.Double(x0 - maxRad, y0 - maxRad, maxRad * 2, maxRad * 2).getBounds2D();
    }

    @Override
    public void drawModel(Graphics2D g2) {
        switch (getStatus()) {
            case OVER:
            case INITIATE:
                return;
            case FREEZING:
                drawWhenFreezing(g2);
                break;
            case APPEARING:
                drawWhenAppearing(g2);
                break;
            case FADING:
                drawWhenFading(g2);
                break;
        }
    }
}
