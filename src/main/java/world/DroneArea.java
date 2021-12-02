package world;

import com.mapbox.geojson.*;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import drone.DroneTaskPoint;
import drone.MoveList;
import routing.DroneRouter;
import inputOutput.IOMenus;
import debug.VisualDebug;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class DroneArea {
    public ArrayList<Area> noFlyZones;
    public IOMenus parsedMenus;
    public ArrayList<MapPoint> waypoints;

    public static final double CLEARANCE = DroneRouter.SHORT_MOVE_LENGTH; // This can't be anything smaller, as then we may be misaligned for next move.


    public DroneArea(FeatureCollection noFlyZones, FeatureCollection landmarks, IOMenus parsedMenus) {
        this.parsedMenus = parsedMenus;
        this.waypoints = new ArrayList<>();
        this.noFlyZones = new ArrayList<>();

        assert landmarks.features() != null;
        for (Feature feature : landmarks.features()){
            if (feature.geometry() instanceof Point){
                Point point = (Point) feature.geometry();
                this.waypoints.add(new MapPoint(point.longitude(), point.latitude()));
            }
        }

        assert noFlyZones.features() != null;
        for (Feature feature : noFlyZones.features()){
            if (feature.geometry() instanceof Polygon){
                Polygon polygon = (Polygon) feature.geometry();
                Area thisNoFlyZone = new Area();
                assert polygon.coordinates().size() == 1;
                Path2D path = new Path2D.Double();
                path.moveTo(polygon.coordinates().get(0).get(0).longitude(), polygon.coordinates().get(0).get(0).latitude());
                for (Point p : polygon.coordinates().get(0)){
                    path.lineTo(p.longitude(), p.latitude());
                }
                System.out.println("Loaded no-fly zone with " + polygon.coordinates().get(0).size() + " points.");
                path.closePath();
                waypoints.addAll(getPathsBoundingBox(path, DroneRouter.SHORT_MOVE_LENGTH * 1.1)); // Since need 1x on each edge, this'll safely allow past.

                thisNoFlyZone.add(new Area(path));
                VisualDebug.drawArea(new Area(path), Color.LIGHT_GRAY);
                this.noFlyZones.add(thisNoFlyZone);
            }
        }
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

    public MoveList pathfind_recursive(MapPoint start, MapPoint end, int depth){
        if (canFlyBetween(start, end)){
            var moveList = new MoveList(new ArrayList<>());
            moveList.points.add(new DroneTaskPoint(start, false));
            moveList.points.add(new DroneTaskPoint(end, false));
            return moveList;
        }else{ // Can't go straight. Need to use waypoints.
            // If we're not at max depth, try to go deeper.
            if (depth < 2) {
                MoveList shortestGoodMove = null;
                double shortestDistance = Double.MAX_VALUE;
                for (MapPoint waypoint : waypoints) {
                    if (canFlyBetween(start, waypoint)) {
                        MoveList maybeRoute = pathfind_recursive(waypoint, end, depth + 1);
                        if (maybeRoute != null) {
                            maybeRoute.points.add(0, new DroneTaskPoint(start, false));
                            double myDistance = maybeRoute.getTotalMoveLength();
                            if (myDistance < shortestDistance){
                                shortestDistance = myDistance;
                                shortestGoodMove = maybeRoute;
                            }
                        }
                    }
                }
                if(shortestGoodMove != null){
                    return shortestGoodMove;
                }
            }
            // We've failed.
            return null;
        }

    }

    public MoveList pathfind(MapPoint start, MapPoint end){
        return pathfind_recursive(start, end, 0);
    }
    public boolean canFlyBetween(MapPoint start, MapPoint end){
        MapPoint diff = new MapPoint(end.x - start.x, end.y - start.y);
        if (Math.abs(diff.x) < 0.00000015 && Math.abs(diff.y) < 0.00000015){
            return true;
        }
        MapPoint center = new MapPoint((start.x + end.x) / 2, (start.y + end.y) / 2);

        VisualDebug.setupVisualTest();
        double width = start.distanceTo(end) + CLEARANCE * 2.0; // Clearance on both ends.
        Rectangle2D.Double flyLineRect = new Rectangle2D.Double(-width / 2.0, -CLEARANCE, width, CLEARANCE * 2.0);
        AffineTransform at = new AffineTransform();
        at.translate(center.x, center.y);
        at.rotate(Math.toRadians(start.angleTo(end)));

        Rectangle2D.Double startRect = new Rectangle2D.Double(start.x, start.y,  DroneRouter.SHORT_MOVE_LENGTH, DroneRouter.SHORT_MOVE_LENGTH);
        Rectangle2D.Double endRect = new Rectangle2D.Double(end.x, end.y,  DroneRouter.SHORT_MOVE_LENGTH, DroneRouter.SHORT_MOVE_LENGTH);
//        VisualTests.drawArea(new Area(startRect), Color.GREEN);
//        VisualTests.drawArea(new Area(endRect), Color.RED);

        for (Area a : noFlyZones){
            Shape flyLine = at.createTransformedShape(flyLineRect);
            Area flyLineArea = new Area(flyLine);

            flyLineArea.intersect(a);
            boolean intersects = !flyLineArea.isEmpty();
            if (intersects){
                return false;
            }
        }
        Shape flyLine = at.createTransformedShape(flyLineRect);
        Area flyLineArea = new Area(flyLine);
//        VisualTests.drawArea(flyLineArea, Color.DARK_GRAY);

        return true;
    }
//    private double distanceBetweenLines(MapPoint start1, MapPoint end1, MapPoint start2, MapPoint end2){
//        Line2D line1 = new Line2D.Double(start1.x, start1.y, end1.x, end1.y);
//        Line2D line2 = new Line2D.Double(start2.x, start2.y, end2.x, end2.y);
//
//        line1.ptLineDist()
//        return 0.0;
//    }
}
