/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ScaleIndicator extends ModelDrawerAdapter {

    public static double DEFAULT_MARGIN = 15;
    public static double DEFAULT_LENGTH_RATIO = 0.628;
    public static Color DEFAULT_COLOR = Color.ORANGE;
    public static float DEFAULT_LING_WIDTH = 1;
    public static double DEFAULT_MIN_GAP_LENGTH = 30;
    public static double DEFAULT_TICK_LENGTH = 8;
    public static final int FONT_SIZE = 10;
    public static final Font FONT = Font.decode("Monospace " + FONT_SIZE);
    double margin = DEFAULT_MARGIN;
    double lengthRatio = DEFAULT_LENGTH_RATIO;
    float lineWidth = DEFAULT_LING_WIDTH = 1;
    Color color = DEFAULT_COLOR;
    double tickHeight = DEFAULT_TICK_LENGTH;
    Stroke stroke = new BasicStroke(lineWidth);

    @Override
    public Rectangle2D getBoundsInModelSpace() {
        return null;
    }

    @Override
    public void drawModel(Graphics2D g2) {
        int[] numAndPower = rightestTickNumberAndPower();
        String rightestTickString = numberAndPowerToString(numAndPower);
        Rectangle2D rightestTickStringBound = getStringBounds(g2, rightestTickString);
        double length = lenghtOnComponent();
        int tickGapNum = (int) Math.floor(length / rightestTickStringBound.getWidth());
        int num = numAndPower[0];
        if (tickGapNum < num) {
            tickGapNum = num / (num / tickGapNum + 1);
        } else {
            tickGapNum = num;
        }
        if (tickGapNum == 0) {
            return;
        }
        double legnthToRightestTick = num * Math.pow(10, numAndPower[1]) * modelToComponentTransform.getScaleX();
        double tickGapLength = legnthToRightestTick / tickGapNum;
        int tickValueDiff = num / tickGapNum;
        double x0 = getComponent().getWidth() / 2 - length / 2;
        double y0 = getComponent().getHeight() - margin;
        Path2D path = new Path2D.Double();
        path.moveTo(x0, y0);
        path.lineTo(x0 + length, y0);
        for (int i = 0; i <= tickGapNum; i++) {
            path.moveTo(x0 + i * tickGapLength, y0);
            path.lineTo(x0 + i * tickGapLength, y0 - tickHeight);
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        Stroke strokeBak = g2.getStroke();
        g2.setStroke(stroke);
        g2.draw(path);
        g2.setStroke(strokeBak);

        Font fontBak = g2.getFont();
        g2.setFont(FONT);
        int[] tickNumPower = new int[2];
        tickNumPower[1] = numAndPower[1];
        for (int i = 0; i <= tickGapNum; i++) {
            double midX = x0 + i * tickGapLength;
            double baseY = y0 + FONT_SIZE;
            int stickNum = i * tickValueDiff;
            tickNumPower[0] = stickNum;
            String tickString = numberAndPowerToString(tickNumPower);
            Rectangle2D tickStringBounds = getStringBounds(g2, tickString);
            g2.drawString(tickString, (float) (midX - tickStringBounds.getWidth() / 2), (float) baseY);
        }
        g2.setFont(fontBak);
    }

    private int[] rightestTickNumberAndPower() {
        double length = lengthInModelSpace();
        int pow = (int) Math.floor(Math.log10(length));
        int num = (int) Math.floor(length / (Math.pow(10, pow)));
        if (num == 1) {
            pow -= 1;
            num = (int) Math.floor(length / (Math.pow(10, pow)));
        }
        return new int[]{num, pow};
    }

    private double lengthInModelSpace() {
        return lenghtOnComponent() / modelToComponentTransform.getScaleX();
    }

    private double lenghtOnComponent() {
        return getComponent().getWidth() * lengthRatio;
    }

    private static String numberAndPowerToString(int[] numAndPower) {
        int num = numAndPower[0];
        int power = numAndPower[1];
        double value = num * Math.pow(10, power);
        return String.format(" %.2e ", value);
    }

    private Rectangle2D getStringBounds(Graphics2D g2, String str) {
        return FONT.getStringBounds(str, g2.getFontRenderContext());
    }
}
