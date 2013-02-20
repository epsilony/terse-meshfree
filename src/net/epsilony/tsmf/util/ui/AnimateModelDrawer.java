/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

/**
 *
 * @author epsilon
 */
public interface AnimateModelDrawer extends ModelDrawer {

    public void appear();

    public void fade();

    public AnimationStatus getStatus();
}
