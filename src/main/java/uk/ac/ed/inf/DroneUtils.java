package uk.ac.ed.inf;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class DroneUtils { // TODO: Tidy. Don't have utils point :).
    public static final double SHORT_MOVE_LENGTH = 0.00015;
    public static final double UNLUCKY_ZIG_ZAG_MULTIPLIER = 1.15; // See report for where this comes from. // TODO: Real calculation.
    public static final int DRONE_ANGLE_HOVER = -999;


    public static MapPoint getClosestPoint(List<MapPoint> points, MapPoint start){
        MapPoint closest = null;
        double bestDistance = Double.MAX_VALUE;
        for (MapPoint point : points){
            double distance = point.distanceTo(start);
            if (distance < bestDistance){
                bestDistance = distance;
                closest = point;
            }
        }
        return closest;
    }

    public static ArrayList<MapPoint> getPathsBoundingBox(Path2D path, double border){
        ArrayList<MapPoint> points = new ArrayList<>();
        var bounds = path.getBounds2D();
        points.add(new MapPoint(bounds.getX() - border, bounds.getY() - border));
        points.add(new MapPoint(bounds.getX() + bounds.getWidth() + border, bounds.getY() - border));
        points.add(new MapPoint(bounds.getX() - border, bounds.getY() + bounds.getHeight() + border));
        points.add(new MapPoint(bounds.getX() + bounds.getWidth() + border, bounds.getY() + bounds.getHeight() + border));
        return points;
    }
}
