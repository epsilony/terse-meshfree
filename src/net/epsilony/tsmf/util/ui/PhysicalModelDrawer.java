/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 *
 * @author epsilon
 */
public interface PhysicalModelDrawer {

    boolean isVisible();

    void setVisible(boolean visible);

    void drawPhysicalModel(Graphics2D g2, AffineTransform physicalToComponent);
}
