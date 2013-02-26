/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.influence;

import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.Model2D;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tsmf.util.Factory;
import net.epsilony.tsmf.util.pair.PairPack;
import net.epsilony.tsmf.util.pair.WithPair;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ArrayInfluenceDomianRadiusMapperFactory implements Factory<InfluenceRadiusMapper> {

    InfluenceRadiusCalculator influenceRadiusCalculator;
    Model2D model;
    SupportDomainSearcher supportDomainSearcher;

    public ArrayInfluenceDomianRadiusMapperFactory(
            Model2D model,
            InfluenceRadiusCalculator influenceRadiusCalculator,
            SupportDomainSearcher supportDomainSearcher) {
        this.influenceRadiusCalculator = influenceRadiusCalculator;
        this.model = model;
        this.supportDomainSearcher = supportDomainSearcher;
    }

    @Override
    public InfluenceRadiusMapper produce() {
        List<WithPair<Node, Double>> pairs = prepare();
        return new ArrayInfluenceDomainRadiusMapper(pairs);
    }

    public List<WithPair<Node, Double>> prepare() {
        List<WithPair<Node, Double>> pairs = new LinkedList<>();
        for (Node nd : model.getSpaceNodes()) {
            double rad = influenceRadiusCalculator.influcenceRadius(nd, null, supportDomainSearcher);
            pairs.add(new PairPack<>(nd, rad));
        }

        for (Segment2D seg : model.getPolygon()) {
            Node nd = seg.getHead();
            double rad = influenceRadiusCalculator.influcenceRadius(nd, seg, supportDomainSearcher);
            pairs.add(new PairPack<>(nd, rad));
        }
        return pairs;
    }
}
