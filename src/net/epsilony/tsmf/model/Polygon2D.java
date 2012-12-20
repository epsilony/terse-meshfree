/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author epsilon
 */
public class Polygon2D {

    ArrayList<Segment2D> chains;

    public Polygon2D(List<? extends List<? extends Node>> nodeChains) {
        if (nodeChains.isEmpty()) {
            throw new IllegalArgumentException("There is at least 1 chain in a Polygon");
        }
        chains.ensureCapacity(nodeChains.size());
        for (List< ? extends Node> nds : nodeChains) {
            if (nds.size() < 3) {
                throw new IllegalArgumentException(String.format("Each chain in a polygon must contain at least 3 nodes as vertes!%n nodesChain[%d] has only %d nodes", nodeChains.indexOf(nds), nds.size()));
            }
            Segment2D chainHead = new Segment2D();
            Segment2D seg = chainHead;
            for (Node nd : nds) {
                seg.head = nd;
                Segment2D succ = new Segment2D();
                seg.succ = succ;
                succ.pred = seg;
                seg = succ;
            }
            chainHead.pred = seg.pred;
            chainHead.pred.succ = chainHead;
            chains.add(chainHead);
        }
    }

    public static Polygon2D byCoordChains(double[][][] coordChains) {
        ArrayList<ArrayList<Node>> nodeChains = new ArrayList<>(coordChains.length);
        for (double[][] coords : coordChains) {
            ArrayList<Node> nodes = new ArrayList<>(coords.length);
            nodeChains.add(nodes);
            for (double[] coord : coords) {
                nodes.add(new Node(coord));
            }
        }
        return new Polygon2D(nodeChains);
    }
}
