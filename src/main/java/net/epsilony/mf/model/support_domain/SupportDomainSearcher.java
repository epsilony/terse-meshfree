/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import net.epsilony.tb.solid.Segment2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface SupportDomainSearcher {

    public abstract SupportDomainData searchSupportDomain(double[] center, Segment2D bndOfCenter, double radius);
}
