/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.influence;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.Collection;
import java.util.List;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.util.pair.WithPair;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ArrayInfluenceDomainRadiusMapper implements InfluenceRadiusMapper {

    TDoubleArrayList influenceDomainRadiusById;

    public ArrayInfluenceDomainRadiusMapper(Collection<? extends WithPair<Node, Double>> nodeRadiusPairs) {

        checkNodesIdsIdentityAndStartFromZero(nodeRadiusPairs);

        double[] radiusById = new double[nodeRadiusPairs.size()];
        int i = 0;
        for (WithPair<Node, Double> pair : nodeRadiusPairs) {
            radiusById[pair.getKey().getId()] = pair.getValue();
        }
        influenceDomainRadiusById = TDoubleArrayList.wrap(radiusById);
    }

    public static void checkNodesIdsIdentityAndStartFromZero(Collection<? extends WithPair<Node, Double>> pairs) {
        boolean[] haveIds = new boolean[pairs.size()];
        for (WithPair<Node, Double> pair : pairs) {
            Node node = pair.getKey();
            if (node.getId() > haveIds.length) {
                throw new IllegalArgumentException();
            }
            haveIds[node.getId()] = true;
        }
        for (boolean b : haveIds) {
            if (!b) {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public double getInfluenceRadius(Node node) {
        return influenceDomainRadiusById.get(node.getId());
    }

    @Override
    public double getMaximumInfluenceRadius() {
        return influenceDomainRadiusById.max();
    }
}
