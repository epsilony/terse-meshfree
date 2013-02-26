/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.ui;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface ModelDrawer {

    boolean isVisible();

    void setVisible(boolean visible);
    
    Rectangle2D getBoundsInModelSpace();

    void setModelToComponentTransform(AffineTransform modelToComponentTransform);

    AffineTransform getModelToComponentTransform();

    void setComponent(Component component);

    Component getComponent();

    void drawModel(Graphics2D g2);
}
