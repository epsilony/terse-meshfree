/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.model;

import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class ModelSearchResult {

    public List<Node> allNodes;
    public List<Node> visibleNodes;
    public List<Segment2D> segments;
    public List<Node> invisibleNodes;
    public List<Segment2D> invisibleNodesBlockedBy;
}
