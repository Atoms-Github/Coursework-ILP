package routing;

import inputOutput.IODroneAction;
import inputOutput.IOCompletedOrder;
import inputOutput.OutputWriter;
import world.drone.MoveList;
import world.MapPoint;
import orders.Order;
import inputOutput.DatabaseHandle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DroneRouteResults {
    public int remainingShortMoves;
    public MapPoint currentLocation; // This is for pathfinding. Not exact movement. I.e. 'Closest destination'.
    public MapPoint exactCurrentLocation; // This is for converting the path to a list of small moves. Not for pathfinding.
    public ArrayList<Order> completedOrders = new ArrayList<>();
    ArrayList<IODroneAction> droneActions = new ArrayList<>();

    public DroneRouteResults(int remainingShortMoves, MapPoint currentLocation) {
        this.remainingShortMoves = remainingShortMoves;
        this.currentLocation = currentLocation;
        this.exactCurrentLocation = currentLocation;
    }

    public void addMove(String orderNo, MoveList moves){
        // TODO: Check that return to shop isn't in this list with this order.
        ArrayList<IODroneAction> actions = moves.genDroneActions(orderNo, exactCurrentLocation);
        int movesUsed = actions.size();
        remainingShortMoves -= movesUsed;

        System.out.println("Adding a move with " + remainingShortMoves + " moves left. Moves used " + movesUsed);
        currentLocation = moves.getLastLocation();
        if (actions.size() > 0){
            exactCurrentLocation = actions.get(actions.size() - 1).to;
        }
        droneActions.addAll(actions);
    }
    public void addOrder(Order order, MoveList moves){
        System.out.println("Routing order " + order.orderNo + " worth " + order.getTotalPrice());
        completedOrders.add(order);
        addMove(order.orderNo, moves);
    }

    public void writeToOutput(String filename, DatabaseHandle database) throws SQLException {
        System.out.println("Writing to database. Total value: " + getTotalPrice());
        OutputWriter writer = new OutputWriter(filename, database);
        writer.write(droneActions, getOutputOrders());
    }
    private List<IOCompletedOrder> getOutputOrders(){
        List<IOCompletedOrder> orders = new ArrayList<>();
        for (Order order : completedOrders){
            orders.add(new IOCompletedOrder(order.orderNo, order.deliveryTarget.whatThreeWordsLoc, order.getTotalPrice()));
        }
        return orders;
    }

    public int getTotalPrice() {
        int totalMoney = 0;
        for (Order order : completedOrders){
            totalMoney += order.getTotalPrice();
        }
        return totalMoney;
    }
}
// TODO: Don't use DroneWaypoint classname, since waypoint is something else.