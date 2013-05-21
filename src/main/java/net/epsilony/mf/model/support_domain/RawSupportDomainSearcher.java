/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.mf.model.search.SphereSearcher;
import net.epsilony.tb.MiscellaneousUtils;

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

    @Override
    public String toString() {
        return MiscellaneousUtils.simpleToString(this)
                + "{" + "nodesSearcher="
                + nodesSearcher
                + ", segmentSearcher=" + segmentSearcher + '}';
    }
}
