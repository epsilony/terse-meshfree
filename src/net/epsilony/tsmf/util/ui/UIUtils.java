/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.ui;

import java.awt.Component;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class UIUtils {

    private static final double[] ZERO2D = new double[]{0, 0};

    public static Rectangle2D transformAndTidyRectangle(AffineTransform transform, Rectangle2D src, Rectangle2D dst) {
        if (null == dst) {
            dst = new Rectangle2D.Double();
        }
        double[] points = new double[]{
            src.getMinX(), src.getMinY(), src.getMaxX(), src.getMaxY()};
        transform.transform(points, 0, points, 0, 2);
        dst.setRect(
                points[0], points[1],
                points[2] - points[0], points[3] - points[1]);
        tidyRectangle2D(dst, dst);
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

    public static Rectangle2D tidyRectangle2D(Rectangle2D src, Rectangle2D result) {
        if (null == result) {
            result = new Rectangle2D.Double();
        }
        double x = Math.min(src.getX(), src.getX() + src.getWidth());
        double y = Math.min(src.getY(), src.getY() + src.getHeight());
        double width = Math.abs(src.getWidth());
        double height = Math.abs(src.getHeight());
        result.setRect(x, y, width, height);
        return result;
    }

    public static double[] transformVector(AffineTransform transform, double[] vec, double[] result) {
        if (null == result) {
            result = new double[]{2};
        }
        transform.transform(vec, 0, result, 0, 1);
        double[] transformedOrigin = new double[2];
        transform.transform(ZERO2D, 0, transformedOrigin, 0, 1);
        result[0] = result[0] - transformedOrigin[0];
        result[1] = result[1] - transformedOrigin[1];
        return result;
    }
}
