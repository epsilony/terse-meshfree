/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.shape_func.Linear2D;
import net.epsilony.tsmf.util.IntIdentityMap;
import net.epsilony.tsmf.util.synchron.SynchronizedClonable;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LinearLagrangeDirichletProcessor implements SynchronizedClonable<LinearLagrangeDirichletProcessor> {

    private final IntIdentityMap<Node, ProcessNodeData> nodesProcessDatasMap;
    TIntArrayList lagrangleAssemblyIndes = new TIntArrayList();
    TDoubleArrayList lagrangleShapeFunction = new TDoubleArrayList();

    public void process(WeakformQuadraturePoint pt) {
        lagrangleAssemblyIndes.resetQuick();
        lagrangleShapeFunction.resetQuick();
        lagrangleAssemblyIndes.ensureCapacity(2);
        lagrangleShapeFunction.ensureCapacity(2);
        Node head = pt.segment.getHead();
        Node rear = pt.segment.getRear();
        lagrangleAssemblyIndes.add(getLagrangeId(head));
        lagrangleAssemblyIndes.add(getLagrangeId(rear));
        double[] funcV = Linear2D.values(pt.coord, head.getCoord(), rear.getCoord(), null);
        lagrangleShapeFunction.addAll(funcV);
    }

    public TIntArrayList getLagrangleAssemblyIndes() {
        return lagrangleAssemblyIndes;
    }

    public TDoubleArrayList getLagrangleShapeFunctionValue() {
        return lagrangleShapeFunction;
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
