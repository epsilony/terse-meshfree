/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui.demo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.util.ui.AnimateModelDrawer;
import net.epsilony.tsmf.util.ui.AnimationStatus;
import net.epsilony.tsmf.util.ui.CommonFrame;
import net.epsilony.tsmf.util.ui.ModelDrawer;

/**
 *
 * @author epsilon
 */
public class CirclesAnimateDrawerDemo {

    public static void createUI() {
        final double rad = 30;
        final CommonFrame frame = new CommonFrame();
        frame.setSize(800, 600);
        frame.getMainPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        Point2D pt = new Point2D.Double(e.getX(), e.getY());
                        try {
                            frame.getBasicModelPanel().getModelToComponentTransform().inverseTransform(pt, pt);
                        } catch (NoninvertibleTransformException ex) {
                            Logger.getLogger(CirclesAnimateDrawerDemo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        CircleAnimateDrawer animateDrawer = new CircleAnimateDrawer(pt.getX(), pt.getY(), rad);
                        frame.getBasicModelPanel().addAndSetupModelDrawer(animateDrawer);
                        animateDrawer.switchStatus(AnimationStatus.APPEARING);
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    List<ModelDrawer> modelDrawers = frame.getBasicModelPanel().getModelDrawers();
                    for (ModelDrawer md : modelDrawers) {
                        if (md instanceof AnimateModelDrawer) {
                            AnimateModelDrawer amd = (AnimateModelDrawer) md;
                            amd.switchStatus(AnimationStatus.FADING);
                        }
                    }
                }
            }
        });
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createUI();
            }
        });
    }
}
