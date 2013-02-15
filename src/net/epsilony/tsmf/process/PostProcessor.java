/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.WithDiffOrderUtil;

/**
 *
 * @author epsilon
 */
public class PostProcessor extends Mixer {

    public PostProcessor(WeakformProcessor2D outer) {
        super(outer);
    }

    public double[] value(double[] center, Segment2D bnd) {
        WeakformProcessor2D.MixResult mixResult = mix(center, bnd);
        double[] output = new double[WithDiffOrderUtil.outputLength2D(getDiffOrder()) * 2];
        for (int i = 0; i < mixResult.nodeIds.size(); i++) {
            int nodeId = mixResult.nodeIds.getQuick(i);
            double nu = outer.nodesValue.get(nodeId * 2);
            double nv = outer.nodesValue.get(nodeId * 2 + 1);
            for (int j = 0; j < mixResult.shapeFunctionValueLists.length; j++) {
                double sv = mixResult.shapeFunctionValueLists[j].get(i);
                output[j * 2] += nu * sv;
                output[j * 2 + 1] += nv * sv;
            }
        }
        return output;
    }
}
