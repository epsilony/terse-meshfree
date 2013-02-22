/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author epsilon
 */
public class ModelTransform extends AffineTransform {

    public static final double SCALE_LOWER_LIMITE = Double.MIN_VALUE;
    /**
     *
     * @author epsilon
     */
    public static int DEFAULT_ZOOM_ALL_MARGIN = 10;
    private double defaultOriginX;
    private double defaultOriginY;
    private double defaultScale;
    private int zoomAllMargin = MouseDrivenModelTransform.DEFAULT_ZOOM_ALL_MARGIN;

    public void unitScaleAndSetOrigin(double originX, double originY) {
        setToIdentity();
        translate(originX, originY);
        scale(1, -1);
    }

    void translateOrigin(double dx, double dy) {
        preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
    }

    public void scaleByCenter(double centerX, double centerY, double scale) {
        AffineTransform tranformBack = new AffineTransform(this);
        setToTranslation(centerX, centerY);
        scale(scale, scale);
        translate(-centerX, -centerY);
        if (Math.abs(getScaleX()) < SCALE_LOWER_LIMITE || Math.abs(getScaleY()) < SCALE_LOWER_LIMITE) {
            setTransform(tranformBack);
        } else {
            concatenate(tranformBack);
        }
    }

    public void setDefaultOriginAndScale(double originX, double originY, double scale) {
        defaultOriginX = originX;
        defaultOriginY = originY;
        defaultScale = scale;
    }

    public void resetToDefault() {
        unitScaleAndSetOrigin(defaultOriginX, defaultOriginY);
        scale(defaultScale, defaultScale);
    }

    public void setToZoomAll(Rectangle2D modelBound, int componentWidth, int componentHeight) {
        UIUtils.tidyRectangle2D(modelBound, modelBound);
        double modelWidth = modelBound.getWidth();
        double modelHeight = modelBound.getHeight();
        if (0 == modelWidth && 0 == modelHeight) {
            setDefaultOriginAndScale(zoomAllMargin, componentHeight - zoomAllMargin, 1);
        } else {
            double scaleX = modelWidth > 0 ? (componentWidth - 2 * zoomAllMargin) / modelWidth : Double.POSITIVE_INFINITY;
            double scaleY = modelHeight > 0 ? (componentHeight - 2 * zoomAllMargin) / modelHeight : Double.POSITIVE_INFINITY;
            double scale = Math.min(scaleX, scaleY);
            if (scale < ModelTransform.SCALE_LOWER_LIMITE) {
                scale = ModelTransform.SCALE_LOWER_LIMITE;
            }
            double minXOnComponent = zoomAllMargin;
            double maxYOnComponent = componentHeight - zoomAllMargin;
            if (scaleX > scaleY) {
                minXOnComponent = (componentWidth - modelWidth * scale) / 2;
            } else if (scaleY > scaleX) {
                maxYOnComponent = (componentHeight + modelHeight * scale) / 2;
            }
            double oriX = minXOnComponent - modelBound.getMinX() * scale;
            double oriY = maxYOnComponent + modelBound.getMinY() * scale;
            setDefaultOriginAndScale(oriX, oriY, scale);
        }
        resetToDefault();
    }

    void setToZoomAll(int componentWidth, int componentHeight) {
        setDefaultOriginAndScale(componentWidth / 2, componentHeight / 2, 1);
        resetToDefault();
    }

    public int getZoomAllMargin() {
        return zoomAllMargin;
    }

    public void setZoomAllMargin(int zoomAllMargin) {
        this.zoomAllMargin = zoomAllMargin;
    }
}
