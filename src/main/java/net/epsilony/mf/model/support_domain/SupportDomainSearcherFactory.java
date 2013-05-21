/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.mf.model.search.LRTreeNodesSphereSearcher;
import net.epsilony.mf.model.search.LRTreeSegmentChordIntersectingSphereSearcher;
import net.epsilony.mf.model.search.SphereSearcher;
import net.epsilony.mf.process.ProcessNodeData;
import net.epsilony.tb.Factory;
import net.epsilony.tb.IntIdentityMap;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SupportDomainSearcherFactory implements Factory<SupportDomainSearcher> {

    public static final boolean DEFAULT_USE_CENTER_PERTURB = true;
    public static final boolean DEFAULT_IGNORGE_INVISIBLE_NODES_INFORMATION = true;
    SphereSearcher<Node> nodesSearcher;
    SphereSearcher<Segment2D> segmentsSearcher;
    IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap;
    boolean useCenterPerturb = DEFAULT_USE_CENTER_PERTURB;
    boolean ignoreInvisibleNodesInformation = DEFAULT_IGNORGE_INVISIBLE_NODES_INFORMATION;

    public boolean isIgnoreInvisibleNodesInformation() {
        return ignoreInvisibleNodesInformation;
    }

    public void setIgnoreInvisibleNodesInformation(boolean ignoreInvisibleNodesInformation) {
        this.ignoreInvisibleNodesInformation = ignoreInvisibleNodesInformation;
    }

    public SupportDomainSearcherFactory() {
        nodesSearcher = new LRTreeNodesSphereSearcher<>();
        segmentsSearcher = new LRTreeSegmentChordIntersectingSphereSearcher();
    }

    public SupportDomainSearcherFactory(
            SphereSearcher<Node> nodesSearcher,
            SphereSearcher<Segment2D> segmentsSearcher) {
        this(nodesSearcher, segmentsSearcher, null);
    }

    public SupportDomainSearcherFactory(
            SphereSearcher<Node> nodesSearcher,
            SphereSearcher<Segment2D> segmentsSearcher,
            IntIdentityMap<Node, ProcessNodeData> processNodesDatas) {
        this(nodesSearcher, segmentsSearcher, processNodesDatas, DEFAULT_USE_CENTER_PERTURB);
    }

    public SupportDomainSearcherFactory(
            SphereSearcher<Node> nodesSearcher,
            SphereSearcher<Segment2D> segmentsSearcher,
            IntIdentityMap<Node, ProcessNodeData> processNodesDatas,
            boolean useCenterPerturb) {
        this.nodesSearcher = nodesSearcher;
        this.segmentsSearcher = segmentsSearcher;
        this.nodesProcessDatasMap = processNodesDatas;
        this.useCenterPerturb = DEFAULT_USE_CENTER_PERTURB;
    }

    @Override
    public SupportDomainSearcher produce() {
        SupportDomainSearcher result = new RawSupportDomainSearcher(nodesSearcher, segmentsSearcher);
        if (nodesProcessDatasMap != null) {
            result = new FilterByInfluenceDomain(result, nodesProcessDatasMap);
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

    public void setNodesProcessDatasMap(IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap) {
        this.nodesProcessDatasMap = nodesProcessDatasMap;
    }

    public void setUseCenterPerturb(boolean useCenterPerturb) {
        this.useCenterPerturb = useCenterPerturb;
    }
}
