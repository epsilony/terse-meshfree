/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.support_domain;

import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.influence.InfluenceRadiusMapper;
import net.epsilony.tsmf.model.search.LRTreeNodesSphereSearcher;
import net.epsilony.tsmf.model.search.LRTreeSegment2DIntersectingSphereSearcher;
import net.epsilony.tsmf.model.search.SphereSearcher;
import net.epsilony.tsmf.util.Factory;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SupportDomainSearcherFactory implements Factory<SupportDomainSearcher> {

    public static final boolean DEFAULT_USE_CENTER_PERTURB = true;
    public static final boolean DEFAULT_IGNORGE_INVISIBLE_NODES_INFORMATION = true;
    SphereSearcher<Node> nodesSearcher;
    SphereSearcher<Segment2D> segmentsSearcher;
    InfluenceRadiusMapper influenceDomainRadiusMapper;
    boolean useCenterPerturb = DEFAULT_USE_CENTER_PERTURB;
    boolean ignoreInvisibleNodesInformation = DEFAULT_IGNORGE_INVISIBLE_NODES_INFORMATION;

    public boolean isIgnoreInvisibleNodesInformation() {
        return ignoreInvisibleNodesInformation;
    }

    public void setIgnoreInvisibleNodesInformation(boolean ignoreInvisibleNodesInformation) {
        this.ignoreInvisibleNodesInformation = ignoreInvisibleNodesInformation;
    }

    public SupportDomainSearcherFactory() {
        nodesSearcher = new LRTreeNodesSphereSearcher();
        segmentsSearcher = new LRTreeSegment2DIntersectingSphereSearcher();
    }

    public SupportDomainSearcherFactory(
            SphereSearcher<Node> nodesSearcher,
            SphereSearcher<Segment2D> segmentsSearcher) {
        this(nodesSearcher, segmentsSearcher, null);
    }

    public SupportDomainSearcherFactory(
            SphereSearcher<Node> nodesSearcher,
            SphereSearcher<Segment2D> segmentsSearcher,
            InfluenceRadiusMapper influenceDomainRadiusMapper) {
        this(nodesSearcher, segmentsSearcher, influenceDomainRadiusMapper, DEFAULT_USE_CENTER_PERTURB);
    }

    public SupportDomainSearcherFactory(
            SphereSearcher<Node> nodesSearcher,
            SphereSearcher<Segment2D> segmentsSearcher,
            InfluenceRadiusMapper influenceDomainRadiusMapper,
            boolean useCenterPerturb) {
        this.nodesSearcher = nodesSearcher;
        this.segmentsSearcher = segmentsSearcher;
        this.influenceDomainRadiusMapper = influenceDomainRadiusMapper;
        this.useCenterPerturb = DEFAULT_USE_CENTER_PERTURB;
    }

    @Override
    public SupportDomainSearcher produce() {
        SupportDomainSearcher result = new RawSupportDomainSearcher(nodesSearcher, segmentsSearcher);
        if (influenceDomainRadiusMapper != null) {
            result = new FilterByInfluenceDomain(result, influenceDomainRadiusMapper);
        }
        if (useCenterPerturb) {
            result = new CenterPerturbVisibleSupportDomainSearcher(result, ignoreInvisibleNodesInformation);
        } else {
            result = new VisibleSupportDomainSearcher(result, ignoreInvisibleNodesInformation);
        }
        return result;
    }

    public void setNodesSearcher(SphereSearcher<Node> nodesSearcher) {
        this.nodesSearcher = nodesSearcher;
    }

    public void setSegmentsSearcher(SphereSearcher<Segment2D> segmentsSearcher) {
        this.segmentsSearcher = segmentsSearcher;
    }

    public SphereSearcher<Node> getNodesSearcher() {
        return nodesSearcher;
    }

    public SphereSearcher<Segment2D> getSegmentsSearcher() {
        return segmentsSearcher;
    }

    public void setInfluenceDomainRadiusMapper(InfluenceRadiusMapper influenceDomainRadiusMapper) {
        this.influenceDomainRadiusMapper = influenceDomainRadiusMapper;
    }

    public void setUseCenterPerturb(boolean useCenterPerturb) {
        this.useCenterPerturb = useCenterPerturb;
    }
}
