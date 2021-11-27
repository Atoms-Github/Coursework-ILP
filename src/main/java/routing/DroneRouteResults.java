package routing;

import uk.ac.ed.inf.MapPoint;

public class DroneRouteResults {
    public int remainingShortMoves = 1500;
    public MapPoint currentLocation;

    public void addPickup(){

    }
    public void addDeliery(){

    }

    public void addHover(){

    }

    public void addMove(DroneMoveList moves){

    }
    public void addOrder(ProcessedOrder order, DroneMoveList moves){
        // TODO: Implement, including decrementing remainingshortmoves.
    }

    public void writeToDatabase(){

    }
}
