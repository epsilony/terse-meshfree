/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.influence;

import net.epsilony.tsmf.model.Node;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface InfluenceRadiusMapper {

    double getInfluenceRadius(Node node);

    double getMaximumInfluenceRadius();
}
