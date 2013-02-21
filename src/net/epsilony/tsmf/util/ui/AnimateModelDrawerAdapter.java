/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.Timer;

/**
 *
 * @author epsilon
 */
public abstract class AnimateModelDrawerAdapter extends ModelDrawerAdapter implements AnimateModelDrawer, ActionListener {

    public static final int DEFAULT_FRAME_GAP = 33;
    protected AnimationStatus status = AnimationStatus.INITIATE;
    private AnimationStatus statusToSwitch;
    protected int millisecondsBetweenFrame = DEFAULT_FRAME_GAP;
    private long[] frame = new long[2];
    protected Timer timer = new Timer(0, this);

    @Override
    public void switchStatus(AnimationStatus statusToSwitch) {
        this.statusToSwitch = statusToSwitch;
        if (status != statusToSwitch) {
            switch (statusToSwitch) {
                case APPEARING:
                case FADING:
                    startTimer();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public AnimationStatus getStatus() {
        return status;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (statusToSwitch != status) {
            status = statusToSwitch;
            switch (status) {
                case INITIATE:
                    frame[0] = 0;
                    frame[1] = 0;
                    timer.stop();
                    return;
                case OVER:
                case FREEZING:
                    timer.stop();
                    return;
                case APPEARING:
                case FADING:
                    break;
            }
        }
        switch (status) {
            case INITIATE:
            case OVER:
            case FREEZING:
                return;
            case APPEARING:
                frame[AnimationStatus.APPEARING.ordinal()]++;
                break;
            case FADING:
                frame[AnimationStatus.FADING.ordinal()]++;
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

    protected long getFrame(AnimationStatus status) {
        if (status != AnimationStatus.APPEARING && status != AnimationStatus.FADING) {
            throw new IllegalArgumentException();
        }
        return frame[status.ordinal()];
    }

    private void startTimer() {
        timer.setDelay(millisecondsBetweenFrame);
        timer.setRepeats(true);
        timer.start();
    }

    public int getMillisecondsBetweenFrame() {
        return millisecondsBetweenFrame;
    }

    public void setMillisecondsBetweenFrame(int millisecondsBetweenFrame) {
        this.millisecondsBetweenFrame = millisecondsBetweenFrame;
    }
}
