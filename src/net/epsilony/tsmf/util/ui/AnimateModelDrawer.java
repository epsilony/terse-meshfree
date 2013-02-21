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

    public void switchStatus(AnimationStatus status);

    public AnimationStatus getStatus();
}
