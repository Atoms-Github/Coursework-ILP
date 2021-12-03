package routing;

import inputOutput.output.IODroneAction;
import inputOutput.output.IOCompletedOrder;
import inputOutput.output.OutputWriter;
import drone.MoveList;
import world.MapPoint;
import orders.Order;
import inputOutput.DatabaseHandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DroneRouteResults {
    /**
     * How many moves till out of charge.
     */
    private int remainingDroneActions;
    /**
     * The waypoint that this is **close** to. Not 'exact location'.
     */
    private MapPoint currentCloseWaypoint; // This is for pathfinding. Not exact movement. I.e. 'Closest destination'.
    private MapPoint exactCurrentLocation; // This is for converting the path to a list of small moves. Not for pathfinding.
    private final ArrayList<Order> completedOrders = new ArrayList<>();
    private final ArrayList<IODroneAction> droneActions = new ArrayList<>();

    public DroneRouteResults(int remainingDroneActions, MapPoint currentCloseWaypoint) {
        this.remainingDroneActions = remainingDroneActions;
        this.currentCloseWaypoint = currentCloseWaypoint;
        this.exactCurrentLocation = currentCloseWaypoint;
    }

    public int getRemainingDroneActions() {
        return remainingDroneActions;
    }

    /**
     * @return The waypoint that the drone is currently **close** to.
     */
    public MapPoint getCurrentCloseWaypoint() {
        return currentCloseWaypoint;
    }

    /**
     * Adds a move list to this route result's current move list.
     * @param orderNo The order ID that this move is working towards.
     * @param moves The moves to add.
     */
    public void addMove(String orderNo, MoveList moves){
        // Work out the individual drone actions required for these moves.
        ArrayList<IODroneAction> actions = moves.genDroneActions(orderNo, exactCurrentLocation);
        // Work out exact battery usage.
        int movesUsed = actions.size();
        remainingDroneActions -= movesUsed;

        System.out.println("Adding a move with " + remainingDroneActions + " moves left. Moves used " + movesUsed);
        // Update current closest waypoint.
        currentCloseWaypoint = moves.getLastLocation();
        if (actions.size() > 0){
            // Update exact location.
            exactCurrentLocation = actions.get(actions.size() - 1).to;
        }
        droneActions.addAll(actions);
    }

    /**
     * Marks an order as completed, and adds the moves to this results' move collection.
     * @param order The order to add.
     * @param moves The moves that complete the order.
     */
    public void addOrder(Order order, MoveList moves){
        System.out.println("Routing order " + order.orderNo + " worth " + order.getTotalPrice());
        completedOrders.add(order);
        addMove(order.orderNo, moves);
    }

    /**
     * Writes the data currently collected in this results to output targets.
     * @param filename Filename of the geojson output file.
     * @param database Database to write to.
     * @throws SQLException Database error.
     * @throws IOException File system error.
     */
    public void writeToOutput(String filename, DatabaseHandle database) throws SQLException, IOException {
        System.out.println("Writing to database. Total value: " + getTotalPrice());
        OutputWriter writer = new OutputWriter(filename, database);
        writer.write(droneActions, getOutputOrders());
    }

    /**
     * Converts my list of Orders to a list of IOCompletedOrders for outputting.
     */
    private List<IOCompletedOrder> getOutputOrders(){
        List<IOCompletedOrder> orders = new ArrayList<>();
        for (Order order : completedOrders){
            orders.add(new IOCompletedOrder(order.orderNo, order.deliveryTarget.whatThreeWordsLoc, order.getTotalPrice()));
        }
        return orders;
    }

    /**
     * @return The total price of all the drone orders this result as managed to complete so far.
     */
    public int getTotalPrice() {
        int totalMoney = 0;
        for (Order order : completedOrders){
            totalMoney += order.getTotalPrice();
        }
        return totalMoney;
    }
}
