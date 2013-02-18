/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
public class BasicMouseDrivedPanelUI<V extends JPanel> extends LayerUI<V> {

    public static boolean defaultShowCoordinateMarker = true;
    MouseDrivenPhysicalTransform physicalTransform = new MouseDrivenPhysicalTransform();
    List<PhysicalModelDrawer> pyhsicalModelDrawer = new LinkedList<>();
    CoordinateMarker coordinateMark = new CoordinateMarker(defaultShowCoordinateMarker);

    public BasicMouseDrivedPanelUI(int originX, int originY, double scale) {
        physicalTransform.setDefault(originX, originY, scale);
        physicalTransform.resetToDefault();
    }

    public AffineTransform getPhysicalTransform() {
        return physicalTransform;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.addMouseListener(physicalTransform);
        c.addMouseMotionListener(physicalTransform);
        c.addMouseWheelListener(physicalTransform);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.RED);
        if (coordinateMark.isVisible()) {
            coordinateMark.drawPhysicalModel(g2, physicalTransform);
        }
    }

    public CoordinateMarker getCoordinateMark() {
        return coordinateMark;
    }

    public boolean isShowCoordinateMarker() {
        return coordinateMark.isVisible();
    }

    public void setShowCoordinateMarker(boolean showCoordinateMark) {
        coordinateMark.setVisible(showCoordinateMark);
    }

    public void addPysicalModelDrawer(PhysicalModelDrawer element) {
        pyhsicalModelDrawer.add(element);
    }

    public List<PhysicalModelDrawer> getPhysicalModelDrawers() {
        return pyhsicalModelDrawer;
    }
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createDemoUI();
            }
        });
    }

    public static void createDemoUI() {
        JFrame frame = new JFrame("OriginTransformListener");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BasicMouseDrivedPanelUI<JPanel> myLayerUI = new BasicMouseDrivedPanelUI<>(10, 180, 1);
        JPanel pan = new JPanel();
        frame.add(new JLayer<>(pan, myLayerUI));
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}
