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
public class TransformUtils {

    public static Rectangle2D transformRectangle(AffineTransform transform, Rectangle2D src, Rectangle2D dst) {
        if (null == dst) {
            dst = new Rectangle2D.Double();
        }
        double[] points = new double[]{
            src.getX(), src.getY(),
            src.getX() + src.getWidth(), src.getY() + src.getHeight()};
        transform.transform(points, 0, points, 0, 2);
        dst.setRect(points[0], points[1], points[2] - points[0], points[3] - points[0]);
        return dst;
    }
}
