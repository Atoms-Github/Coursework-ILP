package world;

import com.mapbox.geojson.*;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import drone.DroneTaskPoint;
import drone.MoveList;
import routing.DroneRouter;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class DroneArea {
    private final int MAX_PATHFINDING_DEPTH = 2;

    private final ArrayList<Area> noFlyZones;
    private final ArrayList<MapPoint> waypoints;


    /**
     * Width of 'fat line'. See report. This can't be anything smaller, as then we may be misaligned for next move.
     */
    private static final double LINE_WIDTH = DroneRouter.SHORT_MOVE_LENGTH * 2.0;


    public DroneArea(FeatureCollection noFlyZones) {
        this.waypoints = new ArrayList<>();
        this.noFlyZones = new ArrayList<>();

        assert noFlyZones.features() != null; // Not expecting invalid input.
        // Convert all no-fly zones from geojson features to Areas.
        for (Feature feature : noFlyZones.features()){
            Path2D path = featureToPath(feature);
            // Add a waypoint on a all 4 corners of the no-fly zone's bounding box.
            waypoints.addAll(getPathsBoundingBox(path, DroneRouter.SHORT_MOVE_LENGTH * 1.1)); // Since need 1x on each edge, this'll safely allow past.
            this.noFlyZones.add(new Area(path));
        }
    }

    /**
     * Converts a geojson feature to a Path2D.
     */
    private static Path2D featureToPath(Feature feature){
        Polygon polygon = (Polygon) feature.geometry(); // Not expecting invalid input. Cast should be safe.

        assert polygon.coordinates().size() == 1; // Only expecting 1 polygon per no-fly zone.
        // Convert the list of points to
        Path2D path = new Path2D.Double();
        path.moveTo(polygon.coordinates().get(0).get(0).longitude(), polygon.coordinates().get(0).get(0).latitude());
        for (Point p : polygon.coordinates().get(0)){
            path.lineTo(p.longitude(), p.latitude());
        }
        System.out.println("Loaded no-fly zone with " + polygon.coordinates().get(0).size() + " points.");
        path.closePath();
        return path;
    }
    private static ArrayList<MapPoint> getPathsBoundingBox(Path2D path, double border){
        ArrayList<MapPoint> points = new ArrayList<>();
        var bounds = path.getBounds2D();
        // Add a point offset in all 4 ways.
        points.add(new MapPoint(bounds.getX() - border, bounds.getY() - border));
        points.add(new MapPoint(bounds.getX() + bounds.getWidth() + border, bounds.getY() - border));
        points.add(new MapPoint(bounds.getX() - border, bounds.getY() + bounds.getHeight() + border));
        points.add(new MapPoint(bounds.getX() + bounds.getWidth() + border, bounds.getY() + bounds.getHeight() + border));
        return points;
    }

    private MoveList pathfind_recursive(MapPoint start, MapPoint end, int depth){
        // If you can go straight to it, then go straight to it.
        if (canFlyBetween(start, end)){
            var moveList = new MoveList(new ArrayList<>());
            moveList.pushEnd(new DroneTaskPoint(start, false));
            moveList.pushEnd(new DroneTaskPoint(end, false));
            return moveList;
        }else{ // Can't go straight. Need to use waypoints.
            // If we're not at max depth, try to go deeper.
            if (depth < MAX_PATHFINDING_DEPTH) {
                // 'FindMin' algorithm for all child recursions' routes.
                MoveList shortestGoodMove = null;
                double shortestDistance = Double.MAX_VALUE;
                for (MapPoint waypoint : waypoints) {
                    // If we can get to a waypoint, try using it.
                    if (canFlyBetween(start, waypoint)) {
                        MoveList maybeRoute = pathfind_recursive(waypoint, end, depth + 1);
                        if (maybeRoute != null) { // If we could find a path:
                            // Complete the route by adding the real start point:
                            maybeRoute.pushStart(new DroneTaskPoint(start, false));
                            // 'FindMin' algorithm part to find shortest route.
                            double thisRouteDistance = maybeRoute.getTotalLength();
                            if (thisRouteDistance < shortestDistance){
                                shortestDistance = thisRouteDistance;
                                shortestGoodMove = maybeRoute;
                            }
                        }
                    }
                }
                // If we could find a good route, return it.
                if(shortestGoodMove != null){
                    return shortestGoodMove;
                }
            }
            // We've failed. Can't fly straight, nor using waypoints, and already reached depth.
            return null;
        }

    }

    /**
     * Pathfinds between two MapPoints, avoiding no-fly zone. This is relatively expensive.
     * @param start Path's start.
     * @param end Path's end.
     * @return A list of moves for the drone.
     */
    public MoveList pathfind(MapPoint start, MapPoint end){
        return pathfind_recursive(start, end, 0);
    }

    /**
     * Whether can fly in a straight line between two points.
     */
    private boolean canFlyBetween(MapPoint start, MapPoint end){
        MapPoint diff = new MapPoint(end.x - start.x, end.y - start.y);
        // If the points are very close together, call it ok. This is to avoid dividing by a very small number.
        if (Math.abs(diff.x) < 0.00000015 && Math.abs(diff.y) < 0.00000015){
            return true;
        }
        // The midpoint between the start and end.
        MapPoint center = new MapPoint((start.x + end.x) / 2, (start.y + end.y) / 2);

        double fatLineLength = start.distanceTo(end) + LINE_WIDTH; // +LineWidth to give clearance on both ends.
        Rectangle2D.Double flyLineRect = new Rectangle2D.Double(-fatLineLength / 2.0, -LINE_WIDTH / 2.0, fatLineLength, LINE_WIDTH);
        // Make a transform which moves the rectangle to where we want it.
        AffineTransform at = new AffineTransform();
        at.translate(center.x, center.y);
        at.rotate(Math.toRadians(start.angleTo(end)));

        // Check intersection with the rectangle and all the no-fly zones.
        for (Area noFlyArea : noFlyZones){
            // We need to create a new rectangle shape for each no-fly zone, because the .intersects() method deforms the rectangle.
            Shape flyLine = at.createTransformedShape(flyLineRect);
            Area flyLineArea = new Area(flyLine);

            // Check for intersection. This deforms flyLineArea.
            flyLineArea.intersect(noFlyArea);
            boolean intersects = !flyLineArea.isEmpty();
            if (intersects){
                return false;
            }
        }
        return true;
    }
}
