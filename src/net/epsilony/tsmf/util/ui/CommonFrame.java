/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.ui;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
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
        
        getContentPane().add(basicModelPanelUI);
        basicModelPanelUI.setPreferredSize(new Dimension(400, 300));
    }

    public BasicModelPanel getMainPanel() {
        return basicModelPanel;
    }

    public void setDefaultModelOriginAndScale(double originX, double originY, double scale) {
        basicModelPanel.setDefaultModelOriginAndScale(originX, originY, scale);
    }

    @Override
    public void setVisible(boolean visible) {
        pack();
        basicModelPanel.setZoomAllNeeded(true);
        super.setVisible(visible);
    }
}
