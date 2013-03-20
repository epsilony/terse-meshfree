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
    double[] tds = new double[2];
    int dirichletNodesSize;

    public void process(TaskUnit pt, TIntArrayList nodesIds, TDoubleArrayList shapeFuncVal) {
        nodesIds.ensureCapacity(nodesIds.size() + 2);
        shapeFuncVal.ensureCapacity(shapeFuncVal.size() + 2);
        Node head = pt.segment.getHead();
        Node rear = pt.segment.getRear();
        nodesIds.add(getLagrangeId(head));
        nodesIds.add(getLagrangeId(rear));
        double[] funcV = Linear2D.values(pt.coord, head.coord, rear.coord, tds);
        shapeFuncVal.addAll(funcV);
    }

    public LinearLagrangeDirichletProcessor(List<TaskUnit> pts, int baseNodesNum) {
        idMap = new int[baseNodesNum];
        Arrays.fill(idMap, -1);
        int id = baseNodesNum;
        for (TaskUnit pt : pts) {
            Segment2D seg = pt.segment;
            Node head = seg.getHead();
            Node rear = seg.getRear();
            if (idMap[head.id] < 0) {
                idMap[head.id] = id;
                id++;
            }
            if (idMap[rear.id] < 0) {
                idMap[rear.id] = id;
                id++;
            }
        }
        dirichletNodesSize = id - baseNodesNum;
    }

    public int getLagrangeId(Node nd) {
        return idMap[nd.id];
    }

    public int getDirichletNodesSize() {
        return dirichletNodesSize;
    }
}
