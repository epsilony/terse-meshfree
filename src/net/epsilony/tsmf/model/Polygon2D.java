/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Man YUAN <epsilonyuan@gmail.com>
 */
public class Polygon2D implements Iterable<Segment2D> {

    ArrayList<Segment2D> chains;

    public Polygon2D(List<? extends List<? extends Node>> nodeChains) {
        if (nodeChains.isEmpty()) {
            throw new IllegalArgumentException("There is at least 1 chain in a Polygon");
        }
        chains = new ArrayList<>(nodeChains.size());
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

    /**
     * Originate from:<\br> Joseph O'Rourke, Computational Geometry in C,2ed.
     * Page 244, Code 7.13
     *
     * @param x
     * @param y
     * @return 'i' : inside , 'o' : outside, 'e' on an edge, 'v' on a vertex
     */
    public char rayCrossing(double x, double y) {
        int rCross = 0, lCross = 0;
        for (Segment2D seg : this) {
            Node head = seg.getHead();
            double x1 = head.coord[0];
            double y1 = head.coord[1];
            if (x1 == x && y1 == y) {
                return 'v';
            }
            Node rear = seg.getRear();
            double x2 = rear.coord[0];
            double y2 = rear.coord[1];

            boolean rStrad = (y1 > y) != (y2 > y);
            boolean lStrad = (y1 < y) != (y2 < y);

            if (rStrad || lStrad) {
                if (rStrad && x1 > x && x2 > x) {
                    rCross += 1;
                } else if (lStrad && x1 < x && x2 < x) {
                    lCross += 1;
                } else {
                    double xCross = (x1 * y - x1 * y2 - x2 * y + x2 * y1) / (y1 - y2);
                    if (rStrad && xCross > x) {
                        rCross++;
                    }
                    if (lStrad && xCross < x) {
                        lCross++;
                    }
                }
            }
        }
        rCross %= 2;
        lCross %= 2;
        if (rCross != lCross) {
            return 'e';
        }
        if (rCross == 1) {
            return 'i';
        } else {
            return 'o';
        }
    }

    public double distanceFunc(double x, double y) {
        char rayCrs = rayCrossing(x, y);

        if (rayCrs == 'e' || rayCrs == 'v') {
            return 0;
        }

        double inf = Double.POSITIVE_INFINITY;
        for (Segment2D seg : this) {
            double dst = seg.distanceTo(x, y);
            if (dst < inf) {
                inf = dst;
            }
        }
        return rayCrs == 'i' ? inf : -inf;
    }

    @Override
    public Iterator<Segment2D> iterator() {
        return new SegmentIterator();
    }

    class SegmentIterator implements Iterator<Segment2D> {

        int chainId = 0;
        Segment2D seg = chains.isEmpty() ? null : chains.get(0);
        Segment2D last;

        @Override
        public boolean hasNext() {
            return chainId < chains.size();
        }

        @Override
        public Segment2D next() {
            Segment2D res = seg;
            seg = (Segment2D) seg.succ;
            if (seg == chains.get(chainId)) {
                chainId++;
                if (chainId < chains.size()) {
                    seg = chains.get(chainId);
                }
            }
            last = res;
            return res;
        }

        @Override
        public void remove() {
            if(last.pred.pred==last.succ){
                throw new IllegalStateException("The chain is only a triangle, and no segments can be removed!");
            }
            last.pred.succ = last.succ;
            last.succ.pred = last.pred;
        }
    }
}