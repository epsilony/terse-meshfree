/* (c) Copyright by Man YUAN */
package net.epsilony.mf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.IntIdentityMap;
import net.epsilony.tb.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LinearLagrangeDirichletProcessor implements SynchronizedClonable<LinearLagrangeDirichletProcessor> {

    private final IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap;
    TIntArrayList lagrangleAssemblyIndes = new TIntArrayList();
    TDoubleArrayList lagrangleShapeFunctionValue = new TDoubleArrayList();

    public void process(WeakformQuadraturePoint pt) {
        lagrangleAssemblyIndes.resetQuick();
        lagrangleShapeFunctionValue.resetQuick();
        lagrangleAssemblyIndes.ensureCapacity(2);
        lagrangleShapeFunctionValue.ensureCapacity(2);
        Node head = pt.segment.getHead();
        Node rear = pt.segment.getRear();
        lagrangleAssemblyIndes.add(getLagrangeId(head));
        lagrangleAssemblyIndes.add(getLagrangeId(rear));
        lagrangleShapeFunctionValue.add(1 - pt.segmentParameter);
        lagrangleShapeFunctionValue.add(pt.segmentParameter);
    }

    public TIntArrayList getLagrangleAssemblyIndes() {
        return lagrangleAssemblyIndes;
    }

    public TDoubleArrayList getLagrangleShapeFunctionValue() {
        return lagrangleShapeFunctionValue;
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

    @Override
    public LinearLagrangeDirichletProcessor synchronizeClone() {
        return new LinearLagrangeDirichletProcessor(nodesProcessDatasMap);
    }
}
