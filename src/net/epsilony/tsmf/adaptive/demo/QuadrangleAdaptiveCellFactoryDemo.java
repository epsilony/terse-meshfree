/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.adaptive.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.adaptive.QuadrangleAdaptiveCell;
import net.epsilony.tsmf.adaptive.QuadrangleAdaptiveCellFactory;
import net.epsilony.tsmf.model.ui.NodeDrawer;
import net.epsilony.tsmf.util.TestTool;
import net.epsilony.tsmf.util.ui.BasicModelPanel;
import net.epsilony.tsmf.util.ui.CommonFrame;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class QuadrangleAdaptiveCellFactoryDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createDemoUI();
            }
        });
    }

    public static void createDemoUI() {
        CommonFrame frame = new CommonFrame();
        QuadrangleAdaptiveCell[][] cells = QuadrangleAdaptiveCellFactory.byCoordGrid(
                TestTool.linSpace(0, 200, 10), TestTool.linSpace(100, 0, 5));
        final BasicModelPanel mainPanel = frame.getMainPanel();
        Color nodeColor = NodeDrawer.DEFAULT_COLOR;
        nodeColor = new Color(
                nodeColor.getRed(),
                nodeColor.getGreen(),
                nodeColor.getBlue(), nodeColor.getAlpha() / 4);
        NodeDrawer.DEFAULT_COLOR = nodeColor;
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
}
