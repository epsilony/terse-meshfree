/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.influence;

import net.epsilony.tsmf.model.Node;

/**
 *
 * @author epsilon
 */
public interface InfluenceRadiusMapper {

    double getInfluenceRadius(Node node);

    double getMaximumInfluenceRadius();
}
