/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.influence;

import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.LinearSegment2D;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ConstantInfluenceRadiusCalculator implements InfluenceRadiusCalculator {

    double rad;

    @Override
    public double influcenceRadius(Node node, LinearSegment2D seg, SupportDomainSearcher mSer) {
        return rad;
    }

    public ConstantInfluenceRadiusCalculator(double rad) {
        this.rad = rad;
    }
}
