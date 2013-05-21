/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import net.epsilony.tb.solid.LinearSegment2D;
import net.epsilony.tb.WithDiffOrderUtil;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PostProcessor extends Mixer {

    int nodeValueDimension;

    public int getNodeValueDimension() {
        return nodeValueDimension;
    }

    public void setNodeValueDimension(int nodeValueDimension) {
        this.nodeValueDimension = nodeValueDimension;
    }

    public double[] value(double[] center, LinearSegment2D bnd) {
        MixResult mixResult = mix(center, bnd);
        double[] output = new double[WithDiffOrderUtil.outputLength2D(getDiffOrder()) * nodeValueDimension];
        for (int i = 0; i < mixResult.nodesAssemblyIndes.size(); i++) {
            int nodeId = mixResult.nodesAssemblyIndes.getQuick(i);
            double[] value = nodesProcessDatasMap.getById(nodeId).getValue();
            for (int j = 0; j < mixResult.shapeFunctionValueLists.length; j++) {
                double sv = mixResult.shapeFunctionValueLists[j].get(i);
                for (int k = 0; k < nodeValueDimension; k++) {
                    output[j * nodeValueDimension + k] += value[k] * sv;
                }
            }
        }
        return output;
    }
}
