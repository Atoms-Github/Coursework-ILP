package routing;

import com.mapbox.geojson.*;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import dataDownload.CafeMenus;
import uk.ac.ed.inf.DroneUtils;
import uk.ac.ed.inf.MapPoint;
import visualTests.VisualTests;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class DroneArea {
    public ArrayList<Area> noFlyZones;
    public CafeMenus parsedMenus;
    public ArrayList<MapPoint> waypoints; // TODO: Work out additionalWaypoints using noFlyZones, and load real waypoints. Use FeatureCollection.BoundingBox!

//    private static final double clearance = 0.00001;
    private static final double clearance = DroneUtils.SHORT_MOVE_LENGTH / 4.0;

    public DroneArea(FeatureCollection noFlyZones, CafeMenus parsedMenus) {
        this.parsedMenus = parsedMenus;
        this.waypoints = new ArrayList<>();
        this.noFlyZones = new ArrayList<>();

        this.waypoints.add(new MapPoint(-3.1913, 55.9456));
        this.waypoints.add(new MapPoint(-3.1861, 55.9447));

        assert noFlyZones.features() != null;
        for (Feature feature : noFlyZones.features()){
            if (feature.geometry() instanceof Polygon){
                Polygon polygon = (Polygon) feature.geometry();
                Area thisNoFlyZone = new Area();
                assert polygon.coordinates().size() == 1; // TODO: Maybe put into loop.
                Path2D path = new Path2D.Double();
                path.moveTo(polygon.coordinates().get(0).get(0).longitude(), polygon.coordinates().get(0).get(0).latitude());
                for (Point p : polygon.coordinates().get(0)){
                    path.lineTo(p.longitude(), p.latitude());
                }
                System.out.println("Loaded no-fly zone with " + polygon.coordinates().get(0).size() + " points.");
                path.closePath();
                thisNoFlyZone.add(new Area(path));
                this.noFlyZones.add(thisNoFlyZone);
                // TODO: Add extra waypoints.
            }
        }
    }

    public DroneMoveList pathfind_recursive(MapPoint start, MapPoint end, int depth){
        if (canFlyBetween(start, end)){
            var moveList = new DroneMoveList(new ArrayList<>());
            moveList.points.add(new DroneWaypoint(start, false));
            moveList.points.add(new DroneWaypoint(end, false));
            return moveList;
        }else{ // Can't go straight. Need to use waypoints.
            // If we're not at max depth, try to go deeper.
            if (depth < 3) {
                // TODO: Do "FindMin" to find best way around here. Shouldn't be too nasty. Just save them as you gen them, then pick min.
                for (MapPoint waypoint : waypoints) {
                    if (canFlyBetween(start, waypoint)) {
                        DroneMoveList maybeRoute = pathfind_recursive(waypoint, end, depth + 1);
                        if (maybeRoute != null) {
                            maybeRoute.points.add(0, new DroneWaypoint(start, false));
                            return maybeRoute;
                        }
                    }
                }
            }
            if (depth == 0) {
                throw new RuntimeException("Can't path!");
            }
            // We've failed.
            return null;
        }
        // TODO: Returns whole route, including start and end point.

    }
    public DroneMoveList pathfind(MapPoint start, MapPoint end){
        return pathfind_recursive(start, end, 0);
    }
    public boolean canFlyBetween(MapPoint start, MapPoint end){
        MapPoint diff = new MapPoint(end.x - start.x, end.y - start.y);
        if (Math.abs(diff.x) < 0.00000015 && Math.abs(diff.y) < 0.00000015){
            return true;
        }
        MapPoint center = new MapPoint((start.x + end.x) / 2, (start.y + end.y) / 2);

        VisualTests.setupVisualTest();
        Rectangle2D.Double flyLineRect = new Rectangle2D.Double(-clearance, -clearance, start.distanceTo(end), clearance * 2.0);
        VisualTests.drawArea(new Area(flyLineRect));
        AffineTransform at = new AffineTransform();
        at.translate(center.x, center.y);
        at.rotate(Math.atan2(diff.y, diff.x));

//        VisualTests.drawArea(new Area(new Rectangle2D.Double(0.0,0.0,200.0,200.0)));
        for (Area a : noFlyZones){
            Shape flyLine = at.createTransformedShape(flyLineRect);
            Area flyLineArea = new Area(flyLine);
            VisualTests.drawArea((Area) flyLineArea.clone(), Color.BLUE);
            flyLineArea.intersect(a);
            boolean intersects = !flyLineArea.isEmpty();
            if (intersects){
                return false;
            }
            VisualTests.drawArea(a);
        }
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
