/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model;

import net.epsilony.tb.solid.Polygon2D;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.LinearSegment2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Model2D {

    public final static int DIMENSION = 2;
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
        this.spaceNodes = new ArrayList<>(spaceNodes);
        allNodes = new ArrayList<>(spaceNodes);
        if (null != polygon) {
            LinkedList<Node> segNds = new LinkedList<>();
            for (LinearSegment2D seg : polygon) {
                segNds.add(seg.getHead());
            }
            allNodes.addAll(segNds);
        }
        int id = 0;
        for (Node nd : allNodes) {
            nd.setId(id);
            id++;
        }
    }
}
