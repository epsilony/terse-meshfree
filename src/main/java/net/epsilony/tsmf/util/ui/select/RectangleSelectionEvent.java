/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.ui.select;

import java.awt.geom.Rectangle2D;
import java.util.EventObject;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleSelectionEvent extends EventObject {

    private final Rectangle2D rectangle;
    private final boolean keepFormerSelections;

    public RectangleSelectionEvent(Object source, Rectangle2D rectangle, boolean keepFormerSelections) {
        super(source);
        this.rectangle = rectangle;
        this.keepFormerSelections = keepFormerSelections;
    }

    public boolean isKeepFormerSelections() {
        return keepFormerSelections;
    }

    public Rectangle2D getRectangle() {
        return rectangle;
    }
}
