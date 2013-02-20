/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author epsilon
 */
public class CommonFrame extends JFrame {

    BasicModelPanel basicModelPanel;

    public CommonFrame() {
        this(new BasicModelPanel());
    }

    public CommonFrame(BasicModelPanel basicModelPanelUI) {
        super();
        this.basicModelPanel = basicModelPanelUI;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(basicModelPanelUI);
    }

    public BasicModelPanel getBasicModelPanel() {
        return basicModelPanel;
    }

    public void setDefaultModelOriginAndScale(double originX, double originY, double scale) {
        basicModelPanel.setDefaultModelOriginAndScale(originX, originY, scale);
    }

    public JPanel getMainPanel() {
        return basicModelPanel;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        basicModelPanel.setZoomAllNeeded(true);
        basicModelPanel.repaint();
    }
}
