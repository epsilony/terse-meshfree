/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.adaptive.QuadrangleAdaptiveCell;
import net.epsilony.tsmf.adaptive.QuadrangleAdaptiveCellFactory;
import net.epsilony.tsmf.model.ui.NodeDrawer;
import net.epsilony.tsmf.util.TestTool;
import net.epsilony.tsmf.util.ui.BasicModelPanel;
import net.epsilony.tsmf.util.ui.CommonFrame;
import net.epsilony.tsmf.util.ui.ModelDrawer;

/**
 *
 * @author epsilon
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
        BasicModelPanel mainPanel = frame.getMainPanel();
        Color nodeColor = NodeDrawer.DEFAULT_COLOR;
        nodeColor = new Color(
                nodeColor.getRed(),
                nodeColor.getGreen(),
                nodeColor.getBlue(), nodeColor.getAlpha() / 4);
        NodeDrawer.DEFAULT_COLOR = nodeColor;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                mainPanel.addAndSetupModelDrawer(new QuadrangleCellDemoDrawer(cells[i][j]));
            }
        }

        JCheckBox checkbox = new JCheckBox("debug", false);
        ClickToFission clickToFission = new ClickToFission(mainPanel, checkbox);
        frame.getContentPane().add(checkbox);
        frame.setLayout(new FlowLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));
        frame.setVisible(true);
    }

    public static class ClickToFission extends MouseAdapter {

        BasicModelPanel basicPanel;
        private final JCheckBox checkbox;

        public ClickToFission(BasicModelPanel basicPanel, JCheckBox checkBox) {
            prepare(basicPanel);
            this.checkbox = checkBox;
        }

        private void prepare(BasicModelPanel basicPanel) {
            this.basicPanel = basicPanel;
            basicPanel.addMouseListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (checkbox.isSelected()) {
                System.out.println("here = ");
            }
            List<ModelDrawer> modelDrawers = basicPanel.getModelDrawers();
            LinkedList<ModelDrawer> newDrawers = new LinkedList<>();
            for (ModelDrawer md : modelDrawers) {
                if (md instanceof QuadrangleCellDemoDrawer) {
                    QuadrangleCellDemoDrawer quadDrawer = (QuadrangleCellDemoDrawer) md;
                    QuadrangleAdaptiveCell cell = quadDrawer.getCell();
                    try {
                        if (cell.getChildren() == null && quadDrawer.isComponentPointInside(e.getX(), e.getY())) {
                            if (cell.isAbleToFissionToChildren()) {
                                cell.fissionToChildren();
                                QuadrangleAdaptiveCell[] children = cell.getChildren();
                                for (QuadrangleAdaptiveCell child : children) {
                                    newDrawers.add(new QuadrangleCellDemoDrawer(child));
                                }
                                basicPanel.repaint();
                            }
                        }
                    } catch (NoninvertibleTransformException ex) {
                        Logger.getLogger(QuadrangleAdaptiveCellFactoryDemo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            for (ModelDrawer nd : newDrawers) {
                basicPanel.addAndSetupModelDrawer(nd);
            }
        }
    }
}
