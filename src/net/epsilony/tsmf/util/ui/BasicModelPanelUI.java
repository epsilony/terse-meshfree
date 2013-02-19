/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

/**
 *
 * @author epsilon
 */
public class BasicModelPanelUI<V extends Component> extends LayerUI<V> {

    public static boolean defaultShowCoordinateMarker = true;
    MouseDrivenModelTransform mouseDrivenModelTransform = new MouseDrivenModelTransform();
    List<ModelDrawer> modelDrawers = new LinkedList<>();
    CoordinateMarker coordinateMarker = new CoordinateMarker(defaultShowCoordinateMarker);

    public BasicModelPanelUI(int originX, int originY, double scale) {
        mouseDrivenModelTransform.setDefaultOriginAndScale(originX, originY, scale);
        mouseDrivenModelTransform.resetToDefault();
    }

    public BasicModelPanelUI() {
        this(0, 0, 1);
    }

    public void addModelDrawer(ModelDrawer element) {
        modelDrawers.add(element);
    }

    public void setDefaultModelOriginAndScale(double originX, double originY, double scale) {
        mouseDrivenModelTransform.setDefaultOriginAndScale(originX, originY, scale);
    }

    public ModelTransform getModelTransform() {
        return mouseDrivenModelTransform;
    }

    public void setZoomAllNeeded(boolean zoomAllNeeded) {
        mouseDrivenModelTransform.setZoomAllNeeded(zoomAllNeeded);
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        mouseDrivenModelTransform.addMouseActionListenersTo(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        Graphics2D g2 = (Graphics2D) g;

        if (mouseDrivenModelTransform.isZoomAllNeeded()) {
            Rectangle2D drawerBoundInModelSpace = new Rectangle();
            for (ModelDrawer md : modelDrawers) {
                if (md.isVisible()) {
                    Rectangle2D mdBounds = md.getModelBounds();
                    if (null != mdBounds) {
                        Rectangle2D.union(drawerBoundInModelSpace, mdBounds, drawerBoundInModelSpace);
                    }
                }
            }
            mouseDrivenModelTransform.setToZoomAll(drawerBoundInModelSpace, c.getWidth(), c.getHeight());
            mouseDrivenModelTransform.setZoomAllNeeded(false);
        }
        for (ModelDrawer md : modelDrawers) {
            if (md.isVisible()) {
                md.drawModel(g2, mouseDrivenModelTransform);
            }
        }
        if (coordinateMarker.isVisible()) {
            coordinateMarker.drawModel(g2, mouseDrivenModelTransform);
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
        BasicModelPanelUI<JPanel> myLayerUI = new BasicModelPanelUI<>(10, 180, 1);
        JPanel panel = new JPanel();
        frame.add(new JLayer<>(panel, myLayerUI));
        frame.setSize(300, 300);
        myLayerUI.addModelDrawer(new ModelDrawerAdapter() {
            Rectangle rect = new Rectangle(100, 50);

            @Override
            public Rectangle2D getModelBounds() {
                return rect;
            }

            @Override
            public void drawModel(Graphics2D g2, AffineTransform modelToComponent) {
                g2.setColor(Color.BLACK);
                g2.draw(modelToComponent.createTransformedShape(rect));
            }
        });

        frame.setVisible(true);
        myLayerUI.setZoomAllNeeded(true);
        panel.repaint();
    }
}
