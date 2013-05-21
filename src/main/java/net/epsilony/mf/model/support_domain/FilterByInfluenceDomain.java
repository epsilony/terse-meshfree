/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import java.util.Iterator;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.mf.process.ProcessNodeData;
import net.epsilony.tb.IntIdentityMap;
import net.epsilony.tb.Math2D;
import net.epsilony.tb.MiscellaneousUtils;

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
