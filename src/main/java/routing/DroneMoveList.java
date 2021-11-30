package routing;

import visualTests.VisualTests;

import java.awt.*;
import java.util.ArrayList;

import static routing.DroneRouter.SHORT_MOVE_LENGTH;

public class DroneMoveList {
    // Yes, having this also include the start point makes us create and destroy a fair number
    // of unnecessary points, but it makes the code cleaner overall.
    public ArrayList<DroneWaypoint> points;

    public DroneMoveList(ArrayList<DroneWaypoint> points) {
        this.points = points;
    }
    public DroneMoveList() {
        this.points = new ArrayList<>();
    }

    public void append(DroneMoveList other){
        // If we've got some points, check that our end lines up with other's start.
        if (points.size() > 0){
            DroneWaypoint firstOtherPoint = other.points.remove(0);
            if (!firstOtherPoint.point.equals(getLastLocation())){
                throw new IllegalArgumentException("First must match last! Last: " + getLastLocation() + " first: " + firstOtherPoint.point);
            }
            if (firstOtherPoint.mustHover){
                getLastWaypoint().mustHover = true;
            }
        }

        // Merge all. (First point of other.points has already been removed).
        points.addAll(other.points);
    }
    public void addRoutedDestination(MapPoint target, DroneArea area){
        assert points.size() > 0;
        DroneMoveList newRoute = area.pathfind(getLastLocation(), target);
        append(newRoute);
    }
    public MapPoint getLastLocation(){
        return points.get(points.size() - 1).point;
    }
    public DroneWaypoint getLastWaypoint(){
        return points.get(points.size() - 1);
    }

    public double totalMoveLength(){
        if (points.size() == 0){
            return 0.0;
        }
        double totalLength = 0.0;
        for (int i = 1; i < this.points.size(); i++) {
            totalLength += points.get(i - 1).point.distanceTo(points.get(i).point);
        }
        return totalLength;
    }
    public int shortMoveSafeEstimate(){
        // Round up. See report for unlucky zig zag modifier.
        return (int) (totalMoveLength() * DroneRouter.UNLUCKY_ZIG_ZAG_MULTIPLIER / SHORT_MOVE_LENGTH) + 1 /* Round up */
                + points.size() /* Assuming max 1 hover per waypoint */
                + points.size() /* Assuming 1 excess move wasted to get in closer to point */;
    }


    public ArrayList<DroneAction> genDroneActions(ProcessedOrder currentOrder, MapPoint exactStartLocation){
        MapPoint exactCurrentLocation = exactStartLocation;
        ArrayList<DroneAction> actions = new ArrayList<>();
        for (int firstIndex = 0; firstIndex < points.size() - 1; firstIndex++) {
            DroneWaypoint fromPoint = points.get(firstIndex);
            DroneWaypoint toPoint = points.get(firstIndex + 1);
            int iterations = 0;
            while (!exactCurrentLocation.closeTo(toPoint.point)){
                double angleExact = exactCurrentLocation.angleTo(toPoint.point);
                double angleRounded = (double) (10 * (Math.round(angleExact / 10))); // Round to nearest 10.
                int droneAngle = (int) angleRounded;
                MapPoint nextPoint = exactCurrentLocation.nextPosition(droneAngle, SHORT_MOVE_LENGTH);
                actions.add(DroneAction.moveActionOrder(currentOrder, droneAngle, exactCurrentLocation, nextPoint));
                iterations ++;
                if (iterations > 10000){
                    int e = 2;
                    VisualTests.setupVisualTest();
                    VisualTests.drawPoint(exactCurrentLocation, Color.GREEN); // Green ontop of pink.
                    VisualTests.drawPoint(toPoint.point, Color.RED);
                    VisualTests.drawPoint(nextPoint, Color.PINK);
                    while(true){

                    }
//                    throw new RuntimeException("Can't find small route."); // TODO.
                }
                exactCurrentLocation = nextPoint;
            }
            if (toPoint.mustHover){
                actions.add(DroneAction.hoverAction(currentOrder, exactCurrentLocation));
            }
        }
        return actions;
    }
}













