/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.support_domain;

import java.util.Iterator;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.process.ProcessNodeData;
import net.epsilony.tsmf.util.IntIdentityMap;
import net.epsilony.tsmf.util.Math2D;
import net.epsilony.tsmf.util.MiscellaneousUtils;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class FilterByInfluenceDomain implements SupportDomainSearcher {

    SupportDomainSearcher upperSearcher;
    IntIdentityMap<Node, ProcessNodeData> processNodesDatas;

    public FilterByInfluenceDomain(
            SupportDomainSearcher supportDomainSearcher,
            IntIdentityMap<Node, ProcessNodeData> processNodesDatas) {
        this.upperSearcher = supportDomainSearcher;
        this.processNodesDatas = processNodesDatas;
    }

    @Override
    public SupportDomainData searchSupportDomain(double[] center, Segment2D bndOfCenter, double radius) {
        SupportDomainData result = upperSearcher.searchSupportDomain(center, bndOfCenter, radius);
        filter(center, result);
        return result;
    }

    private void filter(double[] center, SupportDomainData filterAim) {
        Iterator<Node> nodesIter = filterAim.allNodes.iterator();
        while (nodesIter.hasNext()) {
            Node node = nodesIter.next();
            double rad = processNodesDatas.get(node).getInfluenceRadius();
            if (rad <= Math2D.distance(node.getCoord(), center)) {
                nodesIter.remove();
            }
        }
    }

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this) + "{upper searcher: " + upperSearcher + "}";
    }
}
