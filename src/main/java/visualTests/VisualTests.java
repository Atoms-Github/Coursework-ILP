package visualTests;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

// Taken from https://stackoverflow.com/questions/20927189/detecting-collision-of-two-sprites-that-can-rotate/20928531#20928531.

public class VisualTests {
    private static VisualTests visual;
    private final ArrayList<ColouredArea> shapes = new ArrayList<>();
    public static void setupVisualTest(){
        if (visual == null){
            visual = new VisualTests();
        }
    }
    public static void drawArea(Area area){
        visual.shapes.add(new ColouredArea(area, Color.BLACK));
    }
    public static void drawArea(Area area, Color color){
        visual.shapes.add(new ColouredArea(area, color));
    }

    public VisualTests() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    private static class ColouredArea{
        private Shape shape;
        private Color colour;

        public ColouredArea(Shape shape, Color colour) {
            this.shape = shape;
            this.colour = colour;
        }
    }

    public class TestPane extends JPanel {

        private Rectangle rect01;
        private Rectangle rect02;

        private int angle = 0;

        public TestPane() {

            rect01 = new Rectangle(0, 0, 100, 100);
            rect02 = new Rectangle(0, 0, 100, 100);
//            visual.shapes.add(rect01);
            Timer timer = new Timer(40, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    angle++;
                    repaint();
                }
            });
            timer.start();

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(250, 250);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();


            for (ColouredArea area : visual.shapes){
                g2d.setColor(Color.BLACK);
                AffineTransform atMy = new AffineTransform();
                atMy.translate(getWidth() / 2.0, getHeight() / 2.0);
                double scale = 100000.0;
                atMy.scale(scale, -scale);

                atMy.translate(3.1882,-55.9447);
//                atMy.translate(getWidth() / 2.0,getHeight() / 2.0);
                Path2D.Double pathMy2d = new Path2D.Double();
                pathMy2d.append(area.shape.getPathIterator(atMy), true);
                g2d.setColor(area.colour);
                g2d.fill(pathMy2d);
            }

            g2d.dispose();
        }

    }

}




//            AffineTransform at = new AffineTransform();
//            GeneralPath path1 = new GeneralPath();
//            path1.append(rect01.getPathIterator(at), true);
//            g2d.fill(path1);