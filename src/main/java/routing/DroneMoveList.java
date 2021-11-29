package routing;

import uk.ac.ed.inf.DroneUtils;
import uk.ac.ed.inf.MapPoint;

import java.util.ArrayList;

import static uk.ac.ed.inf.DroneUtils.SHORT_MOVE_LENGTH;

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
            var firstOtherPoint = other.points.remove(0).point;
            if (!firstOtherPoint.equals(getLastLocation())){
                throw new IllegalArgumentException("First must match last! Last: " + getLastLocation() + " first: " + firstOtherPoint);
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
        return (int) (totalMoveLength() / SHORT_MOVE_LENGTH) + 1 /* Round up */ + points.size() /* Assuming max 1 hover per waypoint */;
    }


    public ArrayList<DroneAction> genDroneActions(MapPoint exactStartLocation){
        MapPoint exactCurrentLocation = exactStartLocation;
        ArrayList<DroneAction> actions = new ArrayList<>();
        for (int firstIndex = 0; firstIndex < points.size() - 1; firstIndex++) {
            DroneWaypoint fromPoint = points.get(firstIndex);
            DroneWaypoint toPoint = points.get(firstIndex + 1);
            while (!exactCurrentLocation.closeTo(toPoint.point)){
                double angleExact = exactCurrentLocation.angleTo(toPoint.point); // TODO: Need to add/subtract 90 probs.
                double angleRounded = DroneUtils.round(angleExact, 10); // Round to nearest 10.
                int droneAngle = (int) angleRounded;
                

            }
        }

        return actions;
    }
}













