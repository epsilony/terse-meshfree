/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import java.util.Arrays;
import java.util.List;
import net.epsilony.tsmf.process.ProcessPoint;
import net.epsilony.tsmf.shape_func.Linear2D;

/**
 *
 * @author epsilon
 */
public class LinearLagrangeDirichletProcessor {

    int[] idMap;
    double[] tds = new double[2];
    int dirichletNodesSize;

    public void process(ProcessPoint pt, TIntArrayList nodesIds, TDoubleArrayList shapeFuncVal) {
        nodesIds.ensureCapacity(nodesIds.size() + 2);
        shapeFuncVal.ensureCapacity(shapeFuncVal.size() + 2);
        Node head = pt.seg.getHead();
        Node rear = pt.seg.getRear();
        nodesIds.add(getLagrangeId(head));
        nodesIds.add(getLagrangeId(rear));
        double[] funcV = Linear2D.values(pt.coord, head.coord, rear.coord, tds);
        shapeFuncVal.addAll(funcV);
    }

    public LinearLagrangeDirichletProcessor(List<ProcessPoint> pts, int baseNodesNum) {
        idMap=new int[baseNodesNum];
        Arrays.fill(idMap, -1);
        int id=baseNodesNum;
        for (ProcessPoint pt : pts) {
            Segment2D seg = pt.seg;
            Node head = seg.getHead();
            Node rear = seg.getRear();
            if(idMap[head.id]<0){
                idMap[head.id]=id;
                id++;
            }
            if(idMap[rear.id]<0){
                idMap[rear.id]=id;
                id++;
            }
        }
        dirichletNodesSize=id-baseNodesNum;
    }

    public int getLagrangeId(Node nd) {
        return idMap[nd.id];
    }

    public int getDirichletNodesSize() {
        return dirichletNodesSize;
    }
}
