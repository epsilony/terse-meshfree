/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.support_domain;

import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.search.SphereSearcher;

/**
 *
 * @author epsilon
 */
public class RawSupportDomainSearcher implements SupportDomainSearcher {

    SphereSearcher<Node> nodesSearcher;
    SphereSearcher<Segment2D> segmentSearcher;

    public RawSupportDomainSearcher(SphereSearcher<Node> nodesSearcher, SphereSearcher<Segment2D> segmentSearcher) {
        this.nodesSearcher = nodesSearcher;
        this.segmentSearcher = segmentSearcher;
    }

    @Override
    public SupportDomainData searchSupportDomain(double[] center, Segment2D bndOfCenter, double radius) {
        SupportDomainData result = new SupportDomainData();
        result.allNodes = nodesSearcher.searchInSphere(center, radius);
        result.segments = segmentSearcher.searchInSphere(center, radius);
        return result;
    }
}
