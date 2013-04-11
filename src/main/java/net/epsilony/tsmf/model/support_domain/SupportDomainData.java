/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.model.support_domain;

import java.util.List;
import net.epsilony.tsmf.model.Node;
import net.epsilony.tsmf.model.Segment2D;
import net.epsilony.tsmf.util.pair.WithPair;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class SupportDomainData {

    public List<Node> allNodes;
    public List<Node> visibleNodes;
    public List<Segment2D> segments;
    public List<WithPair<Node, Segment2D>> invisibleNodesAndBlockingSegments;
}