/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

/**
 *
 * @author epsilon
 */
public abstract class ModelDrawerAdapter implements ModelDrawer {

    boolean visible = true;

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
