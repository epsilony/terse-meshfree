/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.shape_func.Linear2D;
import net.epsilony.tsmf.util.IntIdentityMap;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LinearLagrangeDirichletProcessor {

    private final IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap;

    public void process(WeakformQuadraturePoint pt, TIntArrayList nodesIds, TDoubleArrayList shapeFuncVal) {
        nodesIds.ensureCapacity(nodesIds.size() + 2);
        shapeFuncVal.ensureCapacity(shapeFuncVal.size() + 2);
        Node head = pt.segment.getHead();
        Node rear = pt.segment.getRear();
        nodesIds.add(getLagrangeId(head));
        nodesIds.add(getLagrangeId(rear));
        double[] funcV = Linear2D.values(pt.coord, head.getCoord(), rear.getCoord(), null);
        shapeFuncVal.addAll(funcV);
    }

    public LinearLagrangeDirichletProcessor(IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap) {
        this.nodesProcessDatasMap = nodesProcessDatasMap;
    }

    public int getLagrangeId(Node nd) {
        return nodesProcessDatasMap.get(nd).getLagrangeAssemblyIndex();
    }

    public int getDirichletNodesSize() {
        int num = 0;
        for (ProcessNodeData ndData : nodesProcessDatasMap) {
            if (ndData.getLagrangeAssemblyIndex() >= 0) {
                num++;
            }
        }
        return num;
    }
}
