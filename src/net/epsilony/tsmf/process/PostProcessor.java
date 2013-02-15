/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.model.influence.InfluenceRadiusMapper;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.WithDiffOrderUtil;
import no.uib.cipr.matrix.Vector;

/**
 *
 * @author epsilon
 */
public class PostProcessor extends Mixer {

    Vector nodesValues;

    public PostProcessor(ShapeFunction shapeFunction, SupportDomainSearcher supportDomainSearcher, InfluenceRadiusMapper influenceRadiusMapper, Vector nodesValues) {
        super(shapeFunction, supportDomainSearcher, influenceRadiusMapper);
        this.nodesValues = nodesValues;
    }

    public double[] value(double[] center, Segment2D bnd) {
        MixResult mixResult = mix(center, bnd);
        double[] output = new double[WithDiffOrderUtil.outputLength2D(getDiffOrder()) * 2];
        for (int i = 0; i < mixResult.nodeIds.size(); i++) {
            int nodeId = mixResult.nodeIds.getQuick(i);
            double nu = nodesValues.get(nodeId * 2);
            double nv = nodesValues.get(nodeId * 2 + 1);
            for (int j = 0; j < mixResult.shapeFunctionValueLists.length; j++) {
                double sv = mixResult.shapeFunctionValueLists[j].get(i);
                output[j * 2] += nu * sv;
                output[j * 2 + 1] += nv * sv;
            }
        }
        return output;
    }
}
