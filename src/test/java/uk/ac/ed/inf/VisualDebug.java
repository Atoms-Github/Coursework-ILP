package uk.ac.ed.inf;

import routing.DroneRouter;
import world.MapPoint;

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

public class VisualDebug {
    private static VisualDebug visual;
    public static final ArrayList<ColouredArea> shapes = new ArrayList<>();
    public static void setupVisualTest(){
        if (visual == null){
            visual = new VisualDebug();
        }
    }
    public static void drawArea(Area area){
        drawArea(area, Color.BLACK);
    }
    public static void drawPoint(MapPoint point, Color color){
        drawPoint(point, color, false);
    }
    public static void drawPoint(MapPoint point, Color color, boolean random){
        MapPoint realPoint;
        if (random){
            double wobbleDist = DroneRouter.SHORT_MOVE_LENGTH / 3.0;
            realPoint = new MapPoint(point.x + Math.random() * wobbleDist,
                    point.y + Math.random() * wobbleDist);
        }else{
            realPoint = point;
        }
        double width = 0.00005;
        drawArea(new Area(new Rectangle2D.Double(realPoint.x - width / 2, realPoint.y - width / 2, width, width)), color);
    }
    static Color[] colours = new Color[]{Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE};
    public static Color hashStringToColor(String string){
        if (string.equals("noOrders")){
            return Color.BLACK;
        }
        int index = Math.abs(string.hashCode()) % colours.length;
        if (1==1){
            return colours[index];
        }
        float value = (float) Math.abs(string.hashCode()) / (float) Integer.MAX_VALUE;
        return new Color(value, 1-value, 1.0f);
    }
    public static void drawArea(Area area, Color color){
        shapes.add(new ColouredArea(area, color));
    }
    public static void drawLine(MapPoint start, MapPoint end, Color color, double thickness){
        MapPoint center = new MapPoint((start.x + end.x) / 2, (start.y + end.y) / 2);

        double width = start.distanceTo(end); // Clearance on both ends.
        Rectangle2D.Double flyLineRect = new Rectangle2D.Double(-width / 2.0, -thickness / 2.0, width, thickness);
        AffineTransform at = new AffineTransform();
        at.translate(center.x, center.y);
        at.rotate(Math.toRadians(start.angleTo(end)));

        Shape flyLine = at.createTransformedShape(flyLineRect);
        Area flyLineArea = new Area(flyLine);

        drawArea(flyLineArea, color);
    }


    public VisualDebug() {


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
                double scale = 300000.0;
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