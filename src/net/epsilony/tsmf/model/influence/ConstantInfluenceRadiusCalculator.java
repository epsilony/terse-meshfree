/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model.influence;

import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ConstantInfluenceRadiusCalculator implements InfluenceRadiusCalculator {

    double rad;

    @Override
    public double influcenceRadius(Node node, Segment2D seg, SupportDomainSearcher mSer) {
        return rad;
    }

    public ConstantInfluenceRadiusCalculator(double rad) {
        this.rad = rad;
    }
}