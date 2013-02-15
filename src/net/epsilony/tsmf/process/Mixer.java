/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.support_domain.SupportDomainData;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tsmf.util.WithDiffOrder;

/**
 *
 * @author epsilon
 */
public class Mixer implements WithDiffOrder {

    ArrayList<double[]> coords = new ArrayList<>(WeakformProcessor2D.DEFAULT_CAPACITY);
    TIntArrayList nodesIds = new TIntArrayList(WeakformProcessor2D.DEFAULT_CAPACITY, -1);
    TDoubleArrayList infRads = new TDoubleArrayList(WeakformProcessor2D.DEFAULT_CAPACITY);
    SupportDomainSearcher supportDomainSearcher;
    WeakformProcessor2D outer;

    public Mixer(final WeakformProcessor2D outer) {
        this.outer = outer;
        outer.shapeFunction.setDiffOrder(0);
        supportDomainSearcher = outer.supportDomainSearcherFactory.produce();
    }

    public WeakformProcessor2D.MixResult mix(double[] center, Segment2D bnd) {
        SupportDomainData searchResult = supportDomainSearcher.searchSupportDomain(center, bnd, outer.maxIfluenceRad);
        if (WeakformProcessor2D.SUPPORT_COMPLEX_CRITERION) {
            throw new UnsupportedOperationException();
        }
        outer.fromNodesToIdsCoordsInfRads(searchResult.visibleNodes, nodesIds, coords, infRads);
        TDoubleArrayList[] shapeFunctionValueLists = outer.shapeFunction.values(center, coords, infRads, null);
        return new WeakformProcessor2D.MixResult(shapeFunctionValueLists, nodesIds);
    }

    @Override
    public int getDiffOrder() {
        return outer.shapeFunction.getDiffOrder();
    }

    @Override
    public void setDiffOrder(int diffOrder) {
        outer.shapeFunction.setDiffOrder(diffOrder);
    }
}
