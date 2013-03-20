/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.support_domain;

import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.search.SphereSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RawSupportDomainSearcher implements SupportDomainSearcher {

    SphereSearcher<Node> nodesSearcher;
    SphereSearcher<Segment2D> segmentSearcher;

    public RawSupportDomainSearcher(SphereSearcher<Node> nodesSearcher, SphereSearcher<Segment2D> segmentSearcher) {
        this.nodesSearcher = nodesSearcher;
        this.segmentSearcher = segmentSearcher;
    }

    public RawSupportDomainSearcher(SphereSearcher<Node> nodesSearcher) {
        this(nodesSearcher, null);
    }

    @Override
    public SupportDomainData searchSupportDomain(double[] center, Segment2D bndOfCenter, double radius) {
        SupportDomainData result = new SupportDomainData();
        result.allNodes = nodesSearcher.searchInSphere(center, radius);
        if (null != segmentSearcher) {
            result.segments = segmentSearcher.searchInSphere(center, radius);
        }
        return result;
    }
}
