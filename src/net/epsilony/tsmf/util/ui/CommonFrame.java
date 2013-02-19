/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JPanel;

/**
 *
 * @author epsilon
 */
public class CommonFrame extends JFrame {

    BasicModelPanelUI<JPanel> basicModelPanelUI;
    JPanel mainPanel;

    public CommonFrame() {
        super();
        basicModelPanelUI = new BasicModelPanelUI<>();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel();
        add(new JLayer<>(mainPanel, basicModelPanelUI));
    }

    public BasicModelPanelUI<JPanel> getBasicModelPanelUI() {
        return basicModelPanelUI;
    }

    public void setDefaultModelOriginAndScale(double originX, double originY, double scale) {
        basicModelPanelUI.setDefaultModelOriginAndScale(originX, originY, scale);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        basicModelPanelUI.setZoomAllNeeded(true);
        mainPanel.repaint();
    }
}
