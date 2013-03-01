/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive.demo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import net.epsilony.tsmf.adaptive.AdaptiveCell;
import net.epsilony.tsmf.adaptive.AdaptiveUtils;
import net.epsilony.tsmf.util.ui.BasicModelPanel;
import net.epsilony.tsmf.util.ui.ModelDrawer;

/**
 *
 * @author epsilon
 */
public class ClickToFission extends MouseAdapter {
    BasicModelPanel basicPanel;
    private final JCheckBox recursivelyFissionCheckBox;

    public ClickToFission(BasicModelPanel basicPanel, JCheckBox recursivelyFissionCheckBox) {
        prepare(basicPanel);
        this.recursivelyFissionCheckBox = recursivelyFissionCheckBox;
    }

    private void prepare(BasicModelPanel basicPanel) {
        this.basicPanel = basicPanel;
        basicPanel.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 1) {
            return;
        }
        List<ModelDrawer> modelDrawers = basicPanel.getModelDrawers();
        LinkedList<ModelDrawer> newDrawers = new LinkedList<>();
        for (ModelDrawer md : modelDrawers) {
            if (md instanceof AdaptiveCellDemoDrawer) {
                AdaptiveCellDemoDrawer quadDrawer = (AdaptiveCellDemoDrawer) md;
                AdaptiveCell cell = quadDrawer.getCell();
                try {
                    if (cell.getChildren() == null && quadDrawer.isComponentPointInside(e.getX(), e.getY())) {
                        fission(cell, newDrawers);
                    }
                } catch (NoninvertibleTransformException ex) {
                    Logger.getLogger(ClickToFission.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (ModelDrawer nd : newDrawers) {
            basicPanel.addAndSetupModelDrawer(nd);
        }
        if (newDrawers.size() > 0) {
            basicPanel.repaint();
        }
    }

    private void fission(AdaptiveCell cell, Collection<ModelDrawer> newDrawers) {
        LinkedList<AdaptiveCell> newCells = new LinkedList<>();
        if (recursivelyFissionCheckBox.isSelected()) {
            AdaptiveUtils.recursivelyFission(cell, newCells);
        } else if (cell.isAbleToFissionToChildren()) {
            cell.fissionToChildren();
            newCells.addAll(Arrays.asList(cell.getChildren()));
        }
        for (AdaptiveCell newCell : newCells) {
            newDrawers.add(new AdaptiveCellDemoDrawer(newCell));
        }
    }
    
}
