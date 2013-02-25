/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive;

/**
 *
 * @author epsilon
 */
public interface AdaptiveCell {

    void fissionToChildren();

    boolean isAbleToFissionToChildren();
    
    AdaptiveCell findOneFissionObstrutor();
    
    void fusionFromChildren();

    boolean isAbleToFusionFromChildren();

    AdaptiveCell[] getChildren();
}
