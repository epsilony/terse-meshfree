/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.LinearSegment2D;
import net.epsilony.tsmf.model.influence.InfluenceRadiusMapper;
import net.epsilony.tsmf.model.support_domain.SupportDomainData;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.WithDiffOrder;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Mixer implements WithDiffOrder {

    public static final int DEFAULT_CACHE_CAPACITY = 60;
    ArrayList<double[]> coords = new ArrayList<>(DEFAULT_CACHE_CAPACITY);
    TIntArrayList nodesIds = new TIntArrayList(DEFAULT_CACHE_CAPACITY, -1);
    TDoubleArrayList infRads = new TDoubleArrayList(DEFAULT_CACHE_CAPACITY);
    SupportDomainSearcher supportDomainSearcher;
    ShapeFunction shapeFunction;
    double maxInfluenceRad;
    InfluenceRadiusMapper influenceRadiusMapper;

    public Mixer(ShapeFunction shapeFunction, SupportDomainSearcher supportDomainSearcher, InfluenceRadiusMapper influenceRadiusMapper) {
        this.shapeFunction = shapeFunction;
        shapeFunction.setDiffOrder(0);
        this.supportDomainSearcher = supportDomainSearcher;
        this.influenceRadiusMapper = influenceRadiusMapper;
        this.maxInfluenceRad = influenceRadiusMapper.getMaximumInfluenceRadius();
    }

    public MixResult mix(double[] center, LinearSegment2D bnd) {
        SupportDomainData searchResult = supportDomainSearcher.searchSupportDomain(center, bnd, maxInfluenceRad);
        if (WeakformProcessor2D.SUPPORT_COMPLEX_CRITERION) {
            throw new UnsupportedOperationException();
        }
        fromNodesToIdsCoordsInfRads(searchResult.visibleNodes, nodesIds, coords, infRads);
        TDoubleArrayList[] shapeFunctionValueLists = shapeFunction.values(center, coords, infRads, null);
        return new MixResult(shapeFunctionValueLists, nodesIds);
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
            TIntArrayList ids,
            ArrayList<double[]> coords,
            TDoubleArrayList infRads) {
        coords.clear();
        coords.ensureCapacity(nodes.size());
        ids.resetQuick();
        ids.ensureCapacity(nodes.size());
        infRads.resetQuick();
        infRads.ensureCapacity(nodes.size());
        for (Node nd : nodes) {
            coords.add(nd.coord);
            ids.add(nd.getId());
            infRads.add(influenceRadiusMapper.getInfluenceRadius(nd));
        }
    }

    public static class MixResult {

        public TDoubleArrayList[] shapeFunctionValueLists;
        public TIntArrayList nodeIds;

        public MixResult(TDoubleArrayList[] shapeFunctionValueLists, TIntArrayList nodeIds) {
            this.shapeFunctionValueLists = shapeFunctionValueLists;
            this.nodeIds = nodeIds;
        }
    }
}
