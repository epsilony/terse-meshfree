/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.ui.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.util.ui.BasicModelPanel;
import net.epsilony.tsmf.util.ui.ModelDrawerAdapter;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class BasicModelPanelDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                creatDemoFrame();
            }
        });
    }

    public static void creatDemoFrame() {
        JFrame frame = new JFrame("OriginTransformListener");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BasicModelPanel myPanel = new BasicModelPanel();
        frame.getContentPane().add(myPanel);
        myPanel.setPreferredSize(new Dimension(800, 600));
        myPanel.addAndSetupModelDrawer(new ModelDrawerAdapter() {
            Rectangle2D rect = new Rectangle2D.Double(0, 0, 100, 50);

            @Override
            public void drawModel(Graphics2D g2) {
                g2.setColor(Color.BLACK);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.draw(getModelToComponentTransform().createTransformedShape(rect));
            }

            @Override
            public Rectangle2D getBoundsInModelSpace() {
                return rect;
            }
        });
        frame.pack();

        myPanel.setZoomAllNeeded(true);
        frame.setVisible(true);
    }
}
