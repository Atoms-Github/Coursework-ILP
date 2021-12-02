package drone;

import inputOutput.IODroneAction;
import routing.DroneRouter;
import world.DroneArea;
import world.MapPoint;

import java.util.ArrayList;
import java.util.List;

import static routing.DroneRouter.SHORT_MOVE_LENGTH;

public class MoveList {
    // Yes, having this also include the start point makes us create and destroy a fair number
    // of unnecessary points, but it makes the code cleaner overall.
    public List<DroneTaskPoint> points;

    public MoveList(List<DroneTaskPoint> points) {
        this.points = points;
    }
    public MoveList() {
        this.points = new ArrayList<>();
    }

    public void append(MoveList other){
        // If we've got some points, check that our end lines up with other's start.
        if (points.size() > 0){
            DroneTaskPoint firstOtherPoint = other.points.remove(0);
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
        MoveList newRoute = area.pathfind(getLastLocation(), target);
        append(newRoute);
    }
    public MapPoint getLastLocation(){
        return points.get(points.size() - 1).point;
    }
    public DroneTaskPoint getLastWaypoint(){
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


    public ArrayList<IODroneAction> genDroneActions(String orderNo, MapPoint exactStartLocation){
        MapPoint exactCurrentLocation = exactStartLocation;
        ArrayList<IODroneAction> actions = new ArrayList<>();
        for (int firstIndex = 0; firstIndex < points.size() - 1; firstIndex++) {
            DroneTaskPoint fromPoint = points.get(firstIndex);
            DroneTaskPoint toPoint = points.get(firstIndex + 1);
            int iterations = 0;
            while (!exactCurrentLocation.closeTo(toPoint.point)){
                double angleExact = exactCurrentLocation.angleTo(toPoint.point);
                double angleRounded = (double) (10 * (Math.round(angleExact / 10))); // Round to nearest 10.
                int droneAngle = (int) angleRounded;
                MapPoint nextPoint = exactCurrentLocation.nextPosition(droneAngle, SHORT_MOVE_LENGTH);
                actions.add(IODroneAction.moveActionOrder(orderNo, droneAngle, exactCurrentLocation, nextPoint));
                iterations ++;
                if (iterations > 50_000){
                    System.err.println("Failed to break long line into small drone moves.");
                    break;
                }
                exactCurrentLocation = nextPoint;
            }
            if (toPoint.mustHover){
                actions.add(IODroneAction.hoverAction(orderNo, exactCurrentLocation));
            }
        }
        return actions;
    }
}












