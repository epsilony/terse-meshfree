/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.process;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.Arrays;
import java.util.List;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.shape_func.Linear2D;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LinearLagrangeDirichletProcessor {

    int[] idMap;
    int dirichletNodesSize;

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

    public LinearLagrangeDirichletProcessor(List<WeakformQuadraturePoint> pts, int baseNodesNum) {
        idMap = new int[baseNodesNum];
        Arrays.fill(idMap, -1);
        int id = baseNodesNum;
        for (WeakformQuadraturePoint pt : pts) {
            Segment2D seg = pt.segment;
            Node head = seg.getHead();
            Node rear = seg.getRear();
            if (idMap[head.getId()] < 0) {
                idMap[head.getId()] = id;
                id++;
            }
            if (idMap[rear.getId()] < 0) {
                idMap[rear.getId()] = id;
                id++;
            }
        }
        dirichletNodesSize = id - baseNodesNum;
    }

    public int getLagrangeId(Node nd) {
        return idMap[nd.getId()];
    }

    public int getDirichletNodesSize() {
        return dirichletNodesSize;
    }
}
