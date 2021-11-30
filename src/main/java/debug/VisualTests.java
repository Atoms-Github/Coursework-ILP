package debug;

import data.MapPoint;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
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
        drawArea(area, Color.BLACK);
    }
    public static void drawPoint(MapPoint point, Color color){
        double width = 0.00011;
        drawArea(new Area(new Rectangle2D.Double(point.x - width / 2, point.y - width / 2, width, width)), color);
    }
    public static void drawArea(Area area, Color color){
        if (visual != null){
            visual.shapes.add(new ColouredArea(area, color));
        }
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
        private final Shape shape;
        private final Color colour;

        public ColouredArea(Shape shape, Color colour) {
            this.shape = shape;
            this.colour = colour;
        }
    }

    public class TestPane extends JPanel {
        public TestPane() {
            Timer timer = new Timer(40, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                }
            });
            timer.start();

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 500);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();


            for (ColouredArea area : new ArrayList<>(visual.shapes)){
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