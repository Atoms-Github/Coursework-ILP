package routing;

import dataDownload.DatabaseHandle;
import uk.ac.ed.inf.MapPoint;

import java.util.ArrayList;

public class DroneRouteResults {
    public int remainingShortMoves = 1500;
    public MapPoint currentLocation;
    public ArrayList<ProcessedOrder> completedOrders = new ArrayList<>();
    ArrayList<DroneAction> droneActions = new ArrayList<>();


    public void addMove(DroneMoveList moves){
        ArrayList<DroneAction> actions = moves.genDroneActions(); // TODO: Need to increase accuracy everywhere. Need to know exact start location. // Nice. I've got 'currentLocation' :).
        int movesUsed = actions.size();
        remainingShortMoves -= movesUsed;

        System.out.println("Adding a move with " + remainingShortMoves + " moves left. Moves used " + movesUsed);
        currentLocation = moves.getLastLocation();
    }
    public void addOrder(ProcessedOrder order, DroneMoveList moves){
        System.out.println("Routing order " + order.orderNo + " worth " + order.getTotalPrice());
        completedOrders.add(order);
        addMove(moves);
    }

    public void writeToOutput(String filename, DatabaseHandle database){
        System.out.println("Writing to database. Total value: " + getTotalPrice());
        OutputWriter writer = new OutputWriter(filename, database);
        writer.write(droneActions, completedOrders);

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