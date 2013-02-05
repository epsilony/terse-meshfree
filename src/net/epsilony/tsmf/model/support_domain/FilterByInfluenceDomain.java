/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.support_domain;

import java.util.Iterator;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.influence.InfluenceRadiusMapper;
import net.epsilony.tsmf.util.Math2D;

/**
 *
 * @author epsilon
 */
public class FilterByInfluenceDomain implements SupportDomainSearcher {

    SupportDomainSearcher supportDomainSearcher;
    InfluenceRadiusMapper influenceDomainMapper;

    public FilterByInfluenceDomain(SupportDomainSearcher supportDomainSearcher, InfluenceRadiusMapper influenceDomainMapper) {
        this.supportDomainSearcher = supportDomainSearcher;
        this.influenceDomainMapper = influenceDomainMapper;
    }

    @Override
    public SupportDomainData searchSupportDomain(double[] center, Segment2D bndOfCenter, double radius) {
        SupportDomainData result = supportDomainSearcher.searchSupportDomain(center, bndOfCenter, radius);
        filter(center, result);
        return result;
    }

    private void filter(double[] center, SupportDomainData filterAim) {
        Iterator<Node> nodesIter = filterAim.allNodes.iterator();
        while (nodesIter.hasNext()) {
            Node node = nodesIter.next();
            double rad = influenceDomainMapper.getInfluenceRadius(node);
            if (rad <= Math2D.distance(node.coord, center)) {
                nodesIter.remove();
            }
        }
    }
}
