/* (c) Copyright by Man YUAN */
package net.epsilony.tb.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class BasicModelPanel extends JPanel {

    public static boolean defaultShowCoordinateMarker = true;
    MouseDrivenModelTransform mouseDrivenModelTransform = new MouseDrivenModelTransform();
    List<ModelDrawer> modelDrawers = new LinkedList<>();
    CoordinateMarker coordinateMarker = new CoordinateMarker(defaultShowCoordinateMarker);
    ScaleIndicator scaleIndicator = new ScaleIndicator();

    public BasicModelPanel(int originX, int originY, double scale) {
        prepareMouseDrivenModelTranform(originX, originY, scale);
        prepareCoordinateMarkerAndScaleIndicator();
        prepareWhiteBackground();
    }

    private void prepareMouseDrivenModelTranform(int originX, int originY, double scale) {
        mouseDrivenModelTransform.setDefaultOriginAndScale(originX, originY, scale);
        mouseDrivenModelTransform.resetToDefault();
        mouseDrivenModelTransform.addMouseActionListenersTo(this);
    }

    private void prepareCoordinateMarkerAndScaleIndicator() {
        coordinateMarker.setModelToComponentTransform(mouseDrivenModelTransform);
        coordinateMarker.setComponent(this);
        scaleIndicator.setModelToComponentTransform(mouseDrivenModelTransform);
        scaleIndicator.setComponent(this);
    }

    private void prepareWhiteBackground() {
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    public BasicModelPanel() {
        this(0, 0, 1);
    }

    public void addAndSetupModelDrawer(ModelDrawer element) {
        modelDrawers.add(element);
        element.setComponent(this);
        element.setModelToComponentTransform(mouseDrivenModelTransform);
    }

    public List<ModelDrawer> getModelDrawers() {
        return modelDrawers;
    }

    public void setDefaultModelOriginAndScale(double originX, double originY, double scale) {
        mouseDrivenModelTransform.setDefaultOriginAndScale(originX, originY, scale);
    }

    public ModelTransform getModelToComponentTransform() {
        return mouseDrivenModelTransform;
    }

    public void setZoomAllNeeded(boolean zoomAllNeeded) {
        mouseDrivenModelTransform.setZoomAllNeeded(zoomAllNeeded);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (mouseDrivenModelTransform.isZoomAllNeeded()) {
            Rectangle2D boundsOnComponent = null;
            for (ModelDrawer md : modelDrawers) {
                if (md.isVisible()) {
                    Rectangle2D bounds = md.getBoundsInModelSpace();
                    if (null != bounds) {
                        if (null == boundsOnComponent) {
                            boundsOnComponent = new Rectangle2D.Double(
                                    bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
                        } else {
                            Rectangle2D.union(boundsOnComponent, bounds, boundsOnComponent);
                        }
                    }
                }
            }
            if (null != boundsOnComponent) {
                mouseDrivenModelTransform.setToZoomAll(boundsOnComponent, getWidth(), getHeight());
            } else {
                mouseDrivenModelTransform.setToZoomAll(getWidth(), getHeight());
            }
            mouseDrivenModelTransform.setZoomAllNeeded(false);
        }

        Iterator<ModelDrawer> modelDrawerIterator = modelDrawers.iterator();
        while (modelDrawerIterator.hasNext()) {
            ModelDrawer md = modelDrawerIterator.next();
            if (md instanceof AnimateModelDrawer) {
                AnimateModelDrawer amd = (AnimateModelDrawer) md;
                if (amd.getStatus() == AnimationStatus.OVER) {
                    modelDrawerIterator.remove();
                    continue;
                }
            }
            if (md.isVisible()) {
                md.drawModel(g2);
            }
        }

        if (coordinateMarker.isVisible()) {
            coordinateMarker.drawModel(g2);
        }

        if (scaleIndicator.isVisible()) {
            scaleIndicator.drawModel(g2);
        }
    }

    public CoordinateMarker getCoordinateMarker() {
        return coordinateMarker;
    }

    public boolean isShowCoordinateMarker() {
        return coordinateMarker.isVisible();
    }

    public void setShowCoordinateMarker(boolean showCoordinateMark) {
        coordinateMarker.setVisible(showCoordinateMark);
    }
}
