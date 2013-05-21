/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2D;
import net.epsilony.mf.model.support_domain.SupportDomainData;
import net.epsilony.mf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tb.shape_func.ShapeFunction;
import net.epsilony.tb.IntIdentityMap;
import net.epsilony.tb.MiscellaneousUtils;
import net.epsilony.tb.WithDiffOrder;

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

    public SupportDomainSearcher getSupportDomainSearcher() {
        return supportDomainSearcher;
    }

    public void setSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
        this.supportDomainSearcher = supportDomainSearcher;
    }

    public ShapeFunction getShapeFunction() {
        return shapeFunction;
    }

    public void setShapeFunction(ShapeFunction shapeFunction) {
        this.shapeFunction = shapeFunction;
        shapeFunction.setDiffOrder(0);
    }

    public IntIdentityMap<Node, ProcessNodeData> getNodesProcessDatasMap() {
        return nodesProcessDatasMap;
    }

    public void setNodesProcessDatasMap(IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap) {
        this.nodesProcessDatasMap = nodesProcessDatasMap;
        this.maxInfluenceRad = getMaxInfluenceRadius(nodesProcessDatasMap);
    }

    public double getMaxInfluenceRad() {
        return maxInfluenceRad;
    }

    public static class MixResult {

        public TDoubleArrayList[] shapeFunctionValueLists;
        public TIntArrayList nodesAssemblyIndes;

        public MixResult(TDoubleArrayList[] shapeFunctionValueLists, TIntArrayList nodeIds) {
            this.shapeFunctionValueLists = shapeFunctionValueLists;
            this.nodesAssemblyIndes = nodeIds;
        }
    }

    @Override
    public String toString() {
        return String.format("%s{influ rad: %f, shape function: %s, support domain searcher: %s}",
                MiscellaneousUtils.simpleToString(this),
                getMaxInfluenceRad(),
                getShapeFunction(),
                getSupportDomainSearcher());
    }
}
