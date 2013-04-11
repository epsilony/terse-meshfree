/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util.ui.select.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.epsilony.tsmf.util.ui.UIUtils;
import net.epsilony.tsmf.util.ui.select.RectangleMouseSelector;
import net.epsilony.tsmf.util.ui.select.RectangleSelectionEvent;
import net.epsilony.tsmf.util.ui.select.RectangleSelectionListener;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class RectangleMouseSelectorDemo {

    static class MyPanel extends JPanel implements RectangleSelectionListener {

        static final Color CANDIDATE_COLOR = Color.BLUE;
        static final Color SELECTED_COLOR = Color.RED;
        Rectangle2D rectangle = new Rectangle2D.Double();
        Color color;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(color);
            UIUtils.tidyRectangle2D(rectangle, rectangle);
            g2.draw(rectangle);

        }

        @Override
        public void candidateRectangleSelected(RectangleSelectionEvent e) {
            setRectangleAndRepaint(e);
            color = CANDIDATE_COLOR;
        }

        @Override
        public void rectangleSelected(RectangleSelectionEvent e) {
            setRectangleAndRepaint(e);
            color = SELECTED_COLOR;
        }

        private void setRectangleAndRepaint(RectangleSelectionEvent e) {
            Rectangle2D rc = new Rectangle2D.Double();
            rc.setRect(rectangle);
            rectangle = e.getRectangle();
            UIUtils.tidyRectangle2D(rectangle, rectangle);
            Rectangle2D.union(rc, rectangle, rc);
            UIUtils.repaintRectangle2D(this, rc);
        }
    }

    public static void createUI() {
        RectangleMouseSelector mouseSelector = new RectangleMouseSelector();
        MyPanel panel = new MyPanel();
        mouseSelector.addRectangleSelectionListener(panel);
        panel.addMouseListener(mouseSelector);
        panel.addMouseMotionListener(mouseSelector);
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createUI();
            }
        });
    }
}
