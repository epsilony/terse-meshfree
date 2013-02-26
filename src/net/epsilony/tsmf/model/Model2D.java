/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tsmf.model.search.LRTreeNodesSphereSearcher;
import net.epsilony.tsmf.model.search.LRTreeSegment2DIntersectingSphereSearcher;
import net.epsilony.tsmf.model.search.SphereSearcher;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a> St-Pierre</a>
 */
public class Model2D {

    public final static int DIMENSION = 2;
    SphereSearcher<Segment2D> segmentsIntersectingSphereSearcher;
    SphereSearcher<Node> allNodesSearcher;
    ArrayList<Node> allNodes;
    ArrayList<Node> spaceNodes;   //allNode except polygon.getVertes()
    private Polygon2D polygon;

    public Polygon2D getPolygon() {
        return polygon;
    }

    public ArrayList<Node> getSpaceNodes() {
        return spaceNodes;
    }

    public ArrayList<Node> getAllNodes() {
        return allNodes;
    }

    public Model2D(Polygon2D polygon, List<Node> spaceNodes) {
        this.polygon = polygon;
        segmentsIntersectingSphereSearcher = new LRTreeSegment2DIntersectingSphereSearcher(polygon);
        this.spaceNodes = new ArrayList<>(spaceNodes);
        allNodes = new ArrayList<>(spaceNodes);
        LinkedList<Node> segNds = new LinkedList<>();
        for (Segment2D seg : polygon) {
            segNds.add(seg.getHead());
        }
        allNodes.addAll(segNds);
        allNodesSearcher = new LRTreeNodesSphereSearcher(allNodes, DIMENSION);
        int id = 0;
        for (Node nd : allNodes) {
            nd.setId(id);
            id++;
        }
    }
}
