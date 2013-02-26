/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ScaleIndicator extends ModelDrawerAdapter {

    public static double DEFAULT_MARGIN = 5;
    public static double DEFAULT_LENGTH_RATIO = 0.628;
    public static Color DEFAULT_COLOR = Color.ORANGE;
    public static float DEFAULT_LING_WIDTH = 1;
    public static double DEFAULT_MIN_GAP_LENGTH = 30;
    public static double DEFAULT_TICK_LENGTH = 8;
    boolean vertical = true;
    double margin = DEFAULT_MARGIN;
    double lengthRatio = DEFAULT_LENGTH_RATIO;
    float lineWidth = DEFAULT_LING_WIDTH = 1;
    Color color = DEFAULT_COLOR;
    double minGapLength = DEFAULT_MIN_GAP_LENGTH;
    double tickLength = DEFAULT_TICK_LENGTH;

    @Override
    public Rectangle2D getBoundsInModelSpace() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawModel(Graphics2D g2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
