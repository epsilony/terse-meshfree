/* (c) Copyright by Man YUAN */
package net.epsilony.mf.model.support_domain;

import java.util.Iterator;
import java.util.LinkedList;
import net.epsilony.tb.solid.Node;
import net.epsilony.tb.solid.Segment2D;
import static net.epsilony.tb.Math2D.cross;
import static net.epsilony.tb.Math2D.isSegmentsIntersecting;
import net.epsilony.tb.pair.PairPack;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
@Deprecated
public class VisibleSupportDomainSearcher implements SupportDomainSearcher {

    public static final boolean DEFAULT_IGNORE_INVISIBLE_NODES_INFORMATION = true;
    SupportDomainSearcher supportDomainSearcher;
    boolean ignoreInvisibleNodesInformation;

    public VisibleSupportDomainSearcher(
            SupportDomainSearcher supportDomainSearcher,
            boolean ignoreInvisibleNodesInformation) {
        this.supportDomainSearcher = supportDomainSearcher;
        this.ignoreInvisibleNodesInformation = ignoreInvisibleNodesInformation;
    }

    public VisibleSupportDomainSearcher(SupportDomainSearcher supportDomainSearcher) {
        this(supportDomainSearcher, DEFAULT_IGNORE_INVISIBLE_NODES_INFORMATION);
    }

    @Override
    public SupportDomainData searchSupportDomain(double[] center, Segment2D bndOfCenter, double radius) {
        SupportDomainData result = supportDomainSearcher.searchSupportDomain(center, bndOfCenter, radius);
        prepairResult(result);
        if (result.segments == null || result.segments.isEmpty()) {
            result.visibleNodes.addAll(result.allNodes);
            return result;
        }

        filetAllNodesToVisibleNodesByBndOfCenter(bndOfCenter, result);

        filetVisibleNodeBySegments(center, bndOfCenter, result);
        return result;
    }

    protected void prepairResult(SupportDomainData result) {
        result.visibleNodes = new LinkedList<>();
        if (!ignoreInvisibleNodesInformation) {
            result.invisibleNodesAndBlockingSegments = new LinkedList<>();
        }
    }

    protected void filetVisibleNodeBySegments(double[] center, Segment2D bndOfCenter, SupportDomainData result) {
        for (Segment2D seg : result.segments) {
            if (seg == bndOfCenter) {
                continue;
            }
            Iterator<Node> rsIter = result.visibleNodes.iterator();
            Node head = seg.getHead();
            Node rear = seg.getRear();
            double[] hCoord = head.getCoord();
            double[] rCoord = rear.getCoord();
            while (rsIter.hasNext()) {
                Node nd = rsIter.next();
                if (nd == head || nd == rear) {
                    continue;
                }
                if (isSegmentsIntersecting(center, nd.getCoord(), hCoord, rCoord)) {
                    rsIter.remove();
                    if (!isIgnoreInvisibleNodesInformation()) {
                        result.invisibleNodesAndBlockingSegments.add(new PairPack<>(nd, seg));
                    }
                }
            }
        }
    }

    protected void filetAllNodesToVisibleNodesByBndOfCenter(Segment2D bndOfCenter, SupportDomainData result) {

        if (null == bndOfCenter) {
            result.visibleNodes.addAll(result.allNodes);
        } else {
            double[] hc = bndOfCenter.getHeadCoord();
            double[] rc = bndOfCenter.getRearCoord();
            double dx = rc[0] - hc[0];
            double dy = rc[1] - hc[1];
            Iterator<Node> rsIter = result.allNodes.iterator();
            while (rsIter.hasNext()) {
                Node nd = rsIter.next();
                double[] nc = nd.getCoord();
                if (cross(dx, dy, nc[0] - hc[0], nc[1] - hc[1]) < 0) {
                    if (!isIgnoreInvisibleNodesInformation()) {
                        result.invisibleNodesAndBlockingSegments.add(new PairPack<>(nd, bndOfCenter));
                    }
                } else {
                    result.visibleNodes.add(nd);
                }
            }
        }
    }

    public boolean isIgnoreInvisibleNodesInformation() {
        return ignoreInvisibleNodesInformation;
    }

    public void setIgnoreInvisibleNodesInformation(boolean ignoreInvisibleNodesInformation) {
        this.ignoreInvisibleNodesInformation = ignoreInvisibleNodesInformation;
    }
}
