/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author epsilon
 */
public interface ModelDrawer {

    boolean isVisible();

    void setVisible(boolean visible);

    Rectangle2D getModelBounds();

    void setModelToComponentTransform(AffineTransform modelToComponentTransform);

    AffineTransform getModelToComponentTransform();

    void setComponent(Component component);

    Component getComponent();

    void drawModel(Graphics2D g2);
}
