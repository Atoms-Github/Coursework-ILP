package routing;

import uk.ac.ed.inf.MapPoint;

public class DroneRouteResults {
    public int remainingShortMoves = 1500;
    public MapPoint currentLocation;

    public void addMove(DroneMoveList moves){

    }
    public void addOrder(ProcessedOrder order, DroneMoveList moves){
        // TODO: Implement, including decrementing remainingshortmoves.
        remainingShortMoves -= 200;
        System.out.println("Adding an order with " + moves.points.size() + " moves. " + remainingShortMoves + " moves left.");
    }

    public void writeToDatabase(){

    }
}
