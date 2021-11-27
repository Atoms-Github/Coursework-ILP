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

    public void append(DroneMoveList other){
        var firstOtherPoint = other.points.remove(0);
        if (!firstOtherPoint.equals(points.get(points.size() - 1))){
            throw new IllegalArgumentException("First must match last!");
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
        return 0.0; // TODO:
    }
    public int totalShortMoveCountEstimate(){
        return (int) (totalMoveLength() / SHORT_MOVE_LENGTH) + 1; // Round up.
    }
}
