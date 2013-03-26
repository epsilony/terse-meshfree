/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import net.epsilony.tsmf.model.LinearSegment2D;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.support_domain.SupportDomainSearcher;
import net.epsilony.tsmf.shape_func.ShapeFunction;
import net.epsilony.tsmf.util.IntIdentityMap;
import net.epsilony.tsmf.util.WithDiffOrderUtil;
import no.uib.cipr.matrix.Vector;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class PostProcessor extends Mixer {

    Vector nodesProcessDatasMap;

    public PostProcessor(
            ShapeFunction shapeFunction, 
            SupportDomainSearcher supportDomainSearcher, 
            IntIdentityMap<Node,ProcessNodeData> nodesProcessDatasMap, 
            Vector nodesValues) {
        super(shapeFunction, supportDomainSearcher, nodesProcessDatasMap);
        this.nodesProcessDatasMap = nodesValues;
    }

    public double[] value(double[] center, LinearSegment2D bnd) {
        MixResult mixResult = mix(center, bnd);
        double[] output = new double[WithDiffOrderUtil.outputLength2D(getDiffOrder()) * 2];
        for (int i = 0; i < mixResult.nodeIds.size(); i++) {
            int nodeId = mixResult.nodeIds.getQuick(i);
            double nu = nodesProcessDatasMap.get(nodeId * 2);
            double nv = nodesProcessDatasMap.get(nodeId * 2 + 1);
            for (int j = 0; j < mixResult.shapeFunctionValueLists.length; j++) {
                double sv = mixResult.shapeFunctionValueLists[j].get(i);
                output[j * 2] += nu * sv;
                output[j * 2 + 1] += nv * sv;
            }
        }
        return output;
    }
}
