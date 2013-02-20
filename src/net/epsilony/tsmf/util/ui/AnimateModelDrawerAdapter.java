/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.Timer;

/**
 *
 * @author epsilon
 */
public abstract class AnimateModelDrawerAdapter extends ModelDrawerAdapter implements AnimateModelDrawer, ActionListener {

    public static final int DEFAULT_FRAME_GAP = 33;
    AnimationStatus status = AnimationStatus.FREEZING;
    int frameGap = DEFAULT_FRAME_GAP;
    private int appearingCount = 0;
    private int fadingCount = 0;
    Timer timer = new Timer(0, this);

    @Override
    public void drawModel(Graphics2D g2) {
        AffineTransform modelToComponent = getModelToComponentTransform();
        switch (status) {
            case OVER:
                return;
            case FREEZING:
                drawWhenFreezing(g2, modelToComponent);
                break;
            case APPEARING:
                drawWhenAppearing(g2, modelToComponent);
                break;
            case FADDING:
                drawWhenFading(g2, modelToComponent);
        }
    }

    @Override
    public void appear() {
        if (status == AnimationStatus.APPEARING) {
            return;
        }
        appearingCount = 0;
        status = AnimationStatus.APPEARING;
        timer.setDelay(frameGap);
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    public void fade() {
        if (status == AnimationStatus.FADDING) {
            return;
        }
        status = AnimationStatus.FADDING;
        fadingCount = 0;
        timer.setDelay(frameGap);
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    public AnimationStatus getStatus() {
        return status;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (status) {
            case OVER:
                return;
            case FREEZING:
                break;
            case APPEARING:
                appearingCount++;
                if (appearingCount >= getAppearingFrameSize()) {
                    status = AnimationStatus.FREEZING;
                    timer.stop();
                }
                break;
            case FADDING:
                fadingCount++;
                if (fadingCount >= getFadingFrameSize()) {
                    status = AnimationStatus.OVER;
                    timer.stop();
                }
                break;
        }
        callComponentToRepaint();
    }

    private void callComponentToRepaint() {
        Rectangle2D modelBounds = getModelBounds();
        if (null == modelBounds) {
            component.repaint();
        } else {
            double minX = modelBounds.getMinX();
            double minY = modelBounds.getMinY();
            double maxX = modelBounds.getMaxX();
            double maxY = modelBounds.getMaxY();
            double[] transformed = new double[4];
            getModelToComponentTransform().transform(new double[]{minX, minY, maxX, maxY}, 0, transformed, 0, 2);
            int x0 = (int) Math.floor(Math.min(transformed[0], transformed[2]));
            int y0 = (int) Math.floor(Math.min(transformed[1], transformed[3]));
            int wdith = (int) Math.ceil(Math.abs(transformed[0] - transformed[2]));
            int height = (int) Math.ceil(Math.abs(transformed[1] - transformed[3]));
            component.repaint(x0 - 1, y0 - 1, wdith + 2, height + 2);
        }
    }

    protected int getAppearingFrameCount() {
        return appearingCount;
    }

    protected int getFadingFrameCount() {
        return fadingCount;
    }

    abstract protected void drawWhenFreezing(Graphics2D g2, AffineTransform modelToComponent);

    abstract protected void drawWhenAppearing(Graphics2D g2, AffineTransform modelToComponent);

    abstract protected void drawWhenFading(Graphics2D g2, AffineTransform modelToComponent);

    abstract protected int getAppearingFrameSize();

    abstract protected int getFadingFrameSize();
}
