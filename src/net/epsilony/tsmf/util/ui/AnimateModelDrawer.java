/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import java.awt.Component;
import java.awt.geom.AffineTransform;

/**
 *
 * @author epsilon
 */
public interface AnimateModelDrawer extends ModelDrawer {

    public void setComponent(Component component);

    public void setModelTransform(AffineTransform modelTransform);

    public void appear();

    public void fade();

    public AnimationStatus getStatus();
}
