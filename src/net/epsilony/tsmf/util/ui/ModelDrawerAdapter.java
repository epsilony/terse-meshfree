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
public abstract class ModelDrawerAdapter implements ModelDrawer {

    protected boolean visible = true;
    protected Component component;
    protected AffineTransform modelToComponentTransform;

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void setComponent(Component component) {
        this.component = component;
    }

    @Override
    public AffineTransform getModelToComponentTransform() {
        return modelToComponentTransform;
    }

    @Override
    public void setModelToComponentTransform(AffineTransform modelToComponentTransform) {
        this.modelToComponentTransform = modelToComponentTransform;
    }
}
