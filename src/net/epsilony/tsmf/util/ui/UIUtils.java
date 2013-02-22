/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Component;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author epsilon
 */
public class UIUtils {

    public static Rectangle2D transformRectangle(AffineTransform transform, Rectangle2D src, Rectangle2D dst) {
        if (null == dst) {
            dst = new Rectangle2D.Double();
        }
        double[] points = new double[]{
            src.getX(), src.getY(),};
        transform.transform(points, 0, points, 0, 1);
        dst.setRect(
                points[0], points[1],
                transform.getScaleX() * src.getWidth(), transform.getScaleY() * src.getHeight());
        return dst;
    }

    public static void repaintRectangle2D(Component c, Rectangle2D rect) {
        tidyRectangle2D(rect, rect);
        c.repaint(
                (int) Math.floor(rect.getX()),
                (int) Math.floor(rect.getY()),
                (int) Math.ceil(rect.getWidth()) + 1,
                (int) Math.ceil(rect.getHeight()) + 1);
    }

    public static Rectangle2D tidyRectangle2D(Rectangle2D src, Rectangle2D dst) {
        double x = src.getX();
        double y = src.getY();
        double width = src.getWidth();
        double height = src.getHeight();
        if (src == dst && width >= 0 && height >= 0) {
            return dst;
        }
        double dstX, dstY, dstWidth, dstHeight;
        if (width >= 0) {
            dstX = x;
            dstWidth = width;
        } else {
            dstX = x + width;
            dstWidth = -width;
        }
        if (height >= 0) {
            dstY = y;
            dstHeight = height;
        } else {
            dstY = y + height;
            dstHeight = -height;
        }
        if (null == dst) {
            dst = new Rectangle2D.Double(dstX, dstY, dstWidth, dstHeight);
        } else {
            dst.setRect(dstX, dstY, dstWidth, dstHeight);
        }
        return dst;
    }
}
