package drone;

import inputOutput.output.IODroneAction;
import routing.DroneRouter;
import world.DroneArea;
import world.MapPoint;

import java.util.ArrayList;
import java.util.List;

import static routing.DroneRouter.SHORT_MOVE_LENGTH;

public class MoveList {
    // Yes, having this also include the start point makes us create and destroy a fair number
    // of unnecessary points, but it makes the code cleaner overall.
    /**
     * A list of tasks that the drone should go to to perform this MoveList.
     */
    private List<DroneTaskPoint> points;

    public MoveList(List<DroneTaskPoint> points) {
        this.points = points;
    }

    /**
     * Concatenates another movelist on the end of this move list. The end of one must be the same as the start of the other.
     * @param other Other list to concatenate with.
     */
    public void append(MoveList other){
        // If we've got some points, check that our end lines up with other's start.
        if (points.size() > 0){
            DroneTaskPoint firstOtherPoint = other.points.remove(0);
            if (!firstOtherPoint.getPoint().equals(getLastLocation())){
                throw new IllegalArgumentException("First must match last! Last: " + getLastLocation() + " first: " + firstOtherPoint.getPoint());
            }
            // If the one we're deleting had a hover, keep the hover on the point that will overlap.
            if (firstOtherPoint.getMustHover()){
                getLastWaypoint().setMustHover(true);
            }
        }
        // Merge all. (First point of other.points has already been removed).
        points.addAll(other.points);
    }

    /**
     * Adds a point task onto the end of this point task list.
     * @param taskPoint The point to add.
     */
    public void pushEnd(DroneTaskPoint taskPoint){
        points.add(taskPoint);
    }
    /**
     * Adds a point task onto the start of this point task list.
     * @param taskPoint The point to add.
     */
    public void pushStart(DroneTaskPoint taskPoint){
        points.add(0, taskPoint);
    }

    /**
     * Adds a list of points onto the end of this moveList in order to pathfind to a destination.
     * @param target Pathfinding target.
     * @param area Information for pathfinding.
     * @return Whether a route could be found and added to this movelist.
     */
    public boolean addPathfoundDestination(MapPoint target, DroneArea area){
        assert points.size() > 0;
        // Try to pathfind from our end to the target.
        MoveList newRoute = area.pathfind(getLastLocation(), target);
        if (newRoute != null){ // If pathfinding succeeded.
            append(newRoute);
            return true;
        }else{
            return false;
        }
    }

    /**
     * @return The position of the last task of this list.
     */
    public MapPoint getLastLocation(){
        return getLastWaypoint().getPoint();
    }

    /**
     * @return The last task of this list.
     */
    public DroneTaskPoint getLastWaypoint(){
        return points.get(points.size() - 1);
    }

    /**
     * @return The total length of the route represented by this list.
     */
    public double getTotalLength(){
        if (points.size() == 0){
            return 0.0;
        }
        double totalLength = 0.0;
        // Add the distance of each pair of points.
        for (int firstIndex = 0; firstIndex < points.size() - 1; firstIndex++) {
            totalLength += points.get(firstIndex).getPoint().distanceTo(points.get(firstIndex + 1).getPoint());
        }
        for (int i = 1; i < points.size(); i++) {
        }
        return totalLength;
    }

    /**
     * Estimates the number of short drone move actions that it'd take a drone to fly this route.
     * This'll always estimate an equal or higher number than the real count.
     * @return The move estimate.
     */
    public int getShortMoveSafeEstimate(){
        // Round up. See report for unlucky zig zag modifier.
        return (int) (getTotalLength() * DroneRouter.UNLUCKY_WOBBLE_MULTIPLIER / SHORT_MOVE_LENGTH) + 1 /* Round up */
                + points.size() /* Assuming max 1 hover per waypoint */
                + points.size() /* Assuming 1 excess move wasted to get in closer to point */;
    }


    /**
     * Converts this list of waypoints into a list of individual drone actions able to be performed.
     * @param orderNo What order this moveList is working towards.
     * @param exactStartLocation Where the drone starts.
     * @return The generated list of drone actions.
     */
    public ArrayList<IODroneAction> genDroneActions(String orderNo, MapPoint exactStartLocation){
        MapPoint exactCurrentLocation = exactStartLocation;
        ArrayList<IODroneAction> actions = new ArrayList<>();
        // For each pair of points:
        for (int firstIndex = 0; firstIndex < points.size() - 1; firstIndex++) {
            DroneTaskPoint fromPoint = points.get(firstIndex);
            DroneTaskPoint toPoint = points.get(firstIndex + 1);
            int iterations = 0;
            // While we're not at the next task point, move towards it.
            while (!exactCurrentLocation.closeTo(toPoint.getPoint())){
                // Exact angle from current location towards target.
                double angleExact = exactCurrentLocation.angleTo(toPoint.getPoint());
                // Round angle to nearest 10.
                int droneAngle = (int) (10 * (Math.round(angleExact / 10)));
                // Calculate next position after move in this angle.
                MapPoint nextPoint = exactCurrentLocation.nextPosition(droneAngle, SHORT_MOVE_LENGTH);
                actions.add(IODroneAction.moveAction(orderNo, droneAngle, exactCurrentLocation, nextPoint));
                exactCurrentLocation = nextPoint;
                iterations ++;
                // If still can't after a huge number of tries, just give up. Its better to output something than crash, even if the something has a jump in it.
                if (iterations > 10_000){
                    System.err.println("Failed to break long line into small drone moves.");
                    exactCurrentLocation = toPoint.getPoint(); // Just cheat, and teleport to exit.
                }
            }
            if (toPoint.getMustHover()){
                actions.add(IODroneAction.hoverAction(orderNo, exactCurrentLocation));
            }
        }
        return actions;
    }
}













