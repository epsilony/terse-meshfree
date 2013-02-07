/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive;

/**
 *
 * @author epsilon
 */
public interface AdaptiveCell<T extends AdaptiveCell<T>> {

    void fissionToChildren();

    boolean isAbleToFissionToChildren();

    void fusionFromChildren();

    boolean isAbleToFusionFromChildren();

    T[] getChildren();
}
