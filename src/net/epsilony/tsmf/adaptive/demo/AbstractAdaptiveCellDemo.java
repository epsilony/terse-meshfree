/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JCheckBox;
import net.epsilony.tsmf.adaptive.AdaptiveCell;
import net.epsilony.tsmf.model.ui.NodeDrawer;
import net.epsilony.tsmf.util.ui.BasicModelPanel;
import net.epsilony.tsmf.util.ui.CommonFrame;
import net.epsilony.tsmf.util.ui.ModelDrawer;

/**
 *
 * @author epsilon
 */
public abstract class AbstractAdaptiveCellDemo {

    public void createDemoUI() {
        CommonFrame frame = new CommonFrame();
        AdaptiveCell[][] cells = genCells();
        final BasicModelPanel mainPanel = frame.getMainPanel();
        Color nodeColor = NodeDrawer.DEFAULT_COLOR;
        nodeColor = new Color(nodeColor.getRed(), nodeColor.getGreen(), nodeColor.getBlue(), nodeColor.getAlpha() / 4);
        NodeDrawer.DEFAULT_COLOR = nodeColor;
        List<ModelDrawer> extraDrawers = getExtraDrawers();
        if (null != extraDrawers) {
            for (ModelDrawer dr : extraDrawers) {
                mainPanel.addAndSetupModelDrawer(dr);
            }
        }
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                mainPanel.addAndSetupModelDrawer(new AdaptiveCellDemoDrawer(cells[i][j]));
            }
        }
        JCheckBox recursiveBox = new JCheckBox("recursively", true);
        final JCheckBox showOppositesBox = new JCheckBox("opposites", true);
        showOppositesBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdaptiveCellDemoDrawer.showOppositeMarks = showOppositesBox.isSelected();
                mainPanel.repaint();
            }
        });
        ClickToFission clickToFission = new ClickToFission(mainPanel, recursiveBox);
        frame.getContentPane().add(showOppositesBox);
        frame.getContentPane().add(recursiveBox);
        frame.setLayout(new FlowLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));
        frame.setVisible(true);
    }

    protected abstract AdaptiveCell[][] genCells();

    protected List<ModelDrawer> getExtraDrawers() {
        return null;
    }
}
