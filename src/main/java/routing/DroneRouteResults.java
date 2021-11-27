package routing;

import uk.ac.ed.inf.MapPoint;

import java.util.ArrayList;

public class DroneRouteResults {
    public int remainingShortMoves = 1500;
    public MapPoint currentLocation;
    public ArrayList<ProcessedOrder> completedOrders = new ArrayList<>();

    public void addMove(DroneMoveList moves){
        currentLocation = moves.getLastLocation();
        System.out.println("Adding a move with " + remainingShortMoves + " moves left. Move len: " + moves.points.size() +
                " Move should be " + moves.totalShortMoveCountEstimate());
    }
    public void addOrder(ProcessedOrder order, DroneMoveList moves){
        // TODO: Implement, including decrementing remainingshortmoves.
        remainingShortMoves -= moves.totalShortMoveCountEstimate();

        System.out.println("Adding an order " + order.getTotalPrice() + " with " + moves.points.size()
                + " moves. " + remainingShortMoves + " moves left.");
        currentLocation = moves.getLastLocation();
        completedOrders.add(order);
    }

    public void writeToOutput(){
        int totalMoney = 0;
        for (ProcessedOrder order : completedOrders){
            totalMoney += order.getTotalPrice();
        }
        System.out.println("Writing to database. Total value: " + totalMoney) ;

    }
}
