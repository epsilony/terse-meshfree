/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.model.LinearSegment2D;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.IntIdentityMap;
import net.epsilony.tsmf.util.WithDiffOrderUtil;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PostProcessor extends Mixer {

    int nodeValueDimension;

    public PostProcessor(
            ShapeFunction shapeFunction,
            SupportDomainSearcher supportDomainSearcher,
            IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap, int nodeValueDimension) {
        super(shapeFunction, supportDomainSearcher, nodesProcessDatasMap);
        this.nodeValueDimension = nodeValueDimension;
    }

    public double[] value(double[] center, LinearSegment2D bnd) {
        MixResult mixResult = mix(center, bnd);
        double[] output = new double[WithDiffOrderUtil.outputLength2D(getDiffOrder()) * nodeValueDimension];
        for (int i = 0; i < mixResult.nodeIds.size(); i++) {
            int nodeId = mixResult.nodeIds.getQuick(i);
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
