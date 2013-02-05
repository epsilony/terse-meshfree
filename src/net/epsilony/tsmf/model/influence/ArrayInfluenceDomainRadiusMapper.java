/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.influence;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.Collection;
import java.util.List;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.util.pair.WithPair;

/**
 *
 * @author epsilon
 */
public class ArrayInfluenceDomainRadiusMapper implements InfluenceRadiusMapper {

    TDoubleArrayList influenceDomainRadiusById;

    public ArrayInfluenceDomainRadiusMapper(Collection<? extends WithPair<Node, Double>> nodeRadiusPairs) {

        checkNodesIdsIdentityAndStartFromZero(nodeRadiusPairs);

        double[] radiusById = new double[nodeRadiusPairs.size()];
        int i = 0;
        for (WithPair<Node, Double> pair : nodeRadiusPairs) {
            radiusById[pair.getKey().id] = pair.getValue();
        }
        influenceDomainRadiusById = TDoubleArrayList.wrap(radiusById);
    }

    public static void checkNodesIdsIdentityAndStartFromZero(Collection<? extends WithPair<Node, Double>> pairs) {
        boolean[] haveIds = new boolean[pairs.size()];
        for (WithPair<Node, Double> pair : pairs) {
            Node node = pair.getKey();
            if (node.id > haveIds.length) {
                throw new IllegalArgumentException();
            }
            haveIds[node.id] = true;
        }
        for (boolean b : haveIds) {
            if (!b) {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public double getInfluenceRadius(Node node) {
        return influenceDomainRadiusById.get(node.id);
    }

    @Override
    public double getMaximumInfluenceRadius() {
        return influenceDomainRadiusById.max();
    }
}
