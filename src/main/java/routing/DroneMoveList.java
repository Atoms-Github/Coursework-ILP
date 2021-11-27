package routing;

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
    public int totalShortMoveCountEstimate(){
        return (int) (totalMoveLength() / SHORT_MOVE_LENGTH) + 1; // Round up.
    }
}
