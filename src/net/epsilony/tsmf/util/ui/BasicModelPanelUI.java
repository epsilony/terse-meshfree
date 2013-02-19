/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Color;
import java.awt.Component;
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
public class BasicModelPanelUI<V extends Component> extends LayerUI<V> {

    public static boolean defaultShowCoordinateMarker = true;
    MouseDrivenModelTransform physicalTransform = new MouseDrivenModelTransform();
    List<ModelDrawer> pyhsicalModelDrawer = new LinkedList<>();
    CoordinateMarker coordinateMark = new CoordinateMarker(defaultShowCoordinateMarker);

    public BasicModelPanelUI(int originX, int originY, double scale) {
        physicalTransform.setDefault(originX, originY, scale);
        physicalTransform.resetToDefault();
    }

    public AffineTransform getPhysicalTransform() {
        return physicalTransform;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        physicalTransform.addMouseActionListenerTo(c);
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

    public void addPysicalModelDrawer(ModelDrawer element) {
        pyhsicalModelDrawer.add(element);
    }

    public List<ModelDrawer> getPhysicalModelDrawers() {
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
        BasicModelPanelUI<JPanel> myLayerUI = new BasicModelPanelUI<>(10, 180, 1);
        JPanel pan = new JPanel();
        frame.add(new JLayer<>(pan, myLayerUI));
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}
