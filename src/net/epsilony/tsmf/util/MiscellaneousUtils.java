/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MiscellaneousUtils {

    public static Rectangle2D tidy(Rectangle2D rect, Rectangle2D result) {
        if (null == result) {
            result = new Rectangle2D.Double();
        }
        double x = Math.min(rect.getX(), rect.getX() + rect.getWidth());
        double y = Math.min(rect.getY(), rect.getY() + rect.getHeight());
        double width = Math.abs(rect.getWidth());
        double height = Math.abs(rect.getHeight());
        result.setRect(x, y, width, height);
        return result;
    }

    public static <T> void addToList(T[][] array, List<? super T> dst) {
        for (int i = 0; i < array.length; i++) {
            dst.addAll(Arrays.asList(array[i]));
        }
    }
}
