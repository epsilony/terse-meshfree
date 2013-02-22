/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author epsilon
 */
public class BasicModelPanel extends JPanel {

    public static boolean defaultShowCoordinateMarker = true;
    MouseDrivenModelTransform mouseDrivenModelTransform = new MouseDrivenModelTransform();
    List<ModelDrawer> modelDrawers = new LinkedList<>();
    CoordinateMarker coordinateMarker = new CoordinateMarker(defaultShowCoordinateMarker);

    public BasicModelPanel(int originX, int originY, double scale) {
        prepareMouseDrivenModelTranform(originX, originY, scale);
        prepareCoordinateMarker();
    }

    private void prepareMouseDrivenModelTranform(int originX, int originY, double scale) {
        mouseDrivenModelTransform.setDefaultOriginAndScale(originX, originY, scale);
        mouseDrivenModelTransform.resetToDefault();
        mouseDrivenModelTransform.addMouseActionListenersTo(this);
    }

    private void prepareCoordinateMarker() {
        coordinateMarker.setModelToComponentTransform(mouseDrivenModelTransform);
        coordinateMarker.setComponent(this);
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
                            boundsOnComponent = new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                creatDemoFrame();
            }
        });
    }

    public static void creatDemoFrame() {
        JFrame frame = new JFrame("OriginTransformListener");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BasicModelPanel myPanel = new BasicModelPanel();
        frame.add(myPanel);
        frame.setSize(300, 300);
        myPanel.addAndSetupModelDrawer(new ModelDrawerAdapter() {
            Rectangle2D rect = new Rectangle2D.Double(0, 0, 100, 50);

            @Override
            public Rectangle2D getModelBounds() {
                return rect;
            }

            @Override
            public void drawModel(Graphics2D g2) {
                g2.setColor(Color.BLACK);
                g2.draw(getModelToComponentTransform().createTransformedShape(rect));
            }
        });

        frame.setVisible(true);
        myPanel.setZoomAllNeeded(true);
        myPanel.repaint();
    }
}
