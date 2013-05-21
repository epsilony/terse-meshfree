/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.influence;

import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface InfluenceRadiusCalculator {

    double calcInflucenceRadius(Node node, Segment2D seg);

    SupportDomainSearcher getSupportDomainSearcher();

    void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher);
}
