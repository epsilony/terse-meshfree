/* (c) Copyright by Man YUAN */
package net.epsilony.tb.ui;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public interface AnimateModelDrawer extends ModelDrawer {

    public void switchStatus(AnimationStatus status);

    public AnimationStatus getStatus();
}
