/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.support_domain.SupportDomainData;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.IntIdentityMap;
import net.epsilony.tsmf.util.WithDiffOrder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Mixer implements WithDiffOrder {

    public static final int DEFAULT_CACHE_CAPACITY = 60;
    ArrayList<double[]> coords = new ArrayList<>(DEFAULT_CACHE_CAPACITY);
    TIntArrayList nodesAssemblierIndes = new TIntArrayList(DEFAULT_CACHE_CAPACITY, -1);
    TDoubleArrayList infRads = new TDoubleArrayList(DEFAULT_CACHE_CAPACITY);
    SupportDomainSearcher supportDomainSearcher;
    ShapeFunction shapeFunction;
    double maxInfluenceRad;
    IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap;

    public Mixer(ShapeFunction shapeFunction, SupportDomainSearcher supportDomainSearcher, IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap) {
        this.shapeFunction = shapeFunction;
        shapeFunction.setDiffOrder(0);
        this.supportDomainSearcher = supportDomainSearcher;
        this.nodesProcessDatasMap = nodesProcessDatasMap;
        this.maxInfluenceRad = getMaxInfluenceRadius(nodesProcessDatasMap);
    }

    public static double getMaxInfluenceRadius(IntIdentityMap<Node, ProcessNodeData> processNodesDatas) {
        double maxRadius = 0;
        for (ProcessNodeData nodeData : processNodesDatas) {
            final double influenceRadius = nodeData.getInfluenceRadius();
            if (maxRadius < influenceRadius) {
                maxRadius = influenceRadius;
            }
        }
        return maxRadius;
    }

    public MixResult mix(double[] center, Segment2D bnd) {
        SupportDomainData searchResult = supportDomainSearcher.searchSupportDomain(center, bnd, maxInfluenceRad);
        if (WeakformProcessor.SUPPORT_COMPLEX_CRITERION) {
            throw new UnsupportedOperationException();
        }
        fromNodesToIdsCoordsInfRads(searchResult.visibleNodes, nodesAssemblierIndes, coords, infRads);
        TDoubleArrayList[] shapeFunctionValueLists = shapeFunction.values(center, coords, infRads, null);
        return new MixResult(shapeFunctionValueLists, nodesAssemblierIndes);
    }

    @Override
    public int getDiffOrder() {
        return shapeFunction.getDiffOrder();
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        shapeFunction.setDiffOrder(diffOrder);
    }

    void fromNodesToIdsCoordsInfRads(
            Collection<? extends Node> nodes,
            TIntArrayList nodesAssemblyIndes,
            ArrayList<double[]> coords,
            TDoubleArrayList infRads) {
        coords.clear();
        coords.ensureCapacity(nodes.size());
        nodesAssemblyIndes.resetQuick();
        nodesAssemblyIndes.ensureCapacity(nodes.size());
        infRads.resetQuick();
        infRads.ensureCapacity(nodes.size());
        for (Node nd : nodes) {
            coords.add(nd.getCoord());
            final ProcessNodeData processNodeData = nodesProcessDatasMap.get(nd);
            nodesAssemblyIndes.add(processNodeData.getAssemblyIndex());
            infRads.add(processNodeData.getInfluenceRadius());
        }
    }

    public static class MixResult {

        public TDoubleArrayList[] shapeFunctionValueLists;
        public TIntArrayList nodesAssemblyIndes;

        public MixResult(TDoubleArrayList[] shapeFunctionValueLists, TIntArrayList nodeIds) {
            this.shapeFunctionValueLists = shapeFunctionValueLists;
            this.nodesAssemblyIndes = nodeIds;
        }
    }
}
