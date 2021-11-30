package routing;

import dataDownload.DatabaseHandle;
import uk.ac.ed.inf.MapPoint;

import java.sql.SQLException;
import java.util.ArrayList;

public class DroneRouteResults {
    public int remainingShortMoves;
    public MapPoint currentLocation; // This is for pathfinding. Not exact movement. I.e. 'Closest destination'.
    public MapPoint exactCurrentLocation; // TODO use. This is for converting the path to a list of small moves. Not for pathfinding.
    public ArrayList<ProcessedOrder> completedOrders = new ArrayList<>();
    ArrayList<DroneAction> droneActions = new ArrayList<>();

    public DroneRouteResults(int remainingShortMoves, MapPoint currentLocation) {
        this.remainingShortMoves = remainingShortMoves;
        this.currentLocation = currentLocation;
        this.exactCurrentLocation = currentLocation;
    }

    public void addMove(ProcessedOrder order, DroneMoveList moves){
        // TODO: Check that return to shop isn't in this list with this order.
        ArrayList<DroneAction> actions = moves.genDroneActions(order, exactCurrentLocation); // TODO: Need to increase accuracy everywhere. Need to know exact start location. // Nice. I've got 'currentLocation' :).
        int movesUsed = actions.size();
        remainingShortMoves -= movesUsed;

        System.out.println("Adding a move with " + remainingShortMoves + " moves left. Moves used " + movesUsed);
        currentLocation = moves.getLastLocation();
        if (actions.size() > 0){
            exactCurrentLocation = actions.get(actions.size() - 1).to;
        }
        droneActions.addAll(actions);
    }
    public void addOrder(ProcessedOrder order, DroneMoveList moves){
        System.out.println("Routing order " + order.orderNo + " worth " + order.getTotalPrice());
        completedOrders.add(order);
        addMove(order, moves);
    }

    public void writeToOutput(String filename, DatabaseHandle database) throws SQLException {
        System.out.println("Writing to database. Total value: " + getTotalPrice());
        OutputWriter writer = new OutputWriter(filename, database);
        writer.write(droneActions);

    }

    private int getTotalPrice() {
        int totalMoney = 0;
        for (ProcessedOrder order : completedOrders){
            totalMoney += order.getTotalPrice();
        }
        return totalMoney;
    }
}




// TODO: Replace all ArrayLists with List where possible.

// TODO: Don't use DroneWaypoint classname, since waypoint is something else.