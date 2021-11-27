package routing;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import uk.ac.ed.inf.MapPoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DroneRouteResults {
    public int remainingShortMoves = 1500;
    public MapPoint currentLocation;
    public ArrayList<ProcessedOrder> completedOrders = new ArrayList<>();
    public DroneMoveList currentMoves = new DroneMoveList();

    public void addMove(DroneMoveList moves){
        System.out.println("Adding a move with " + remainingShortMoves + " moves left. Moves using " + moves.totalShortMoveCountEstimate());
        currentMoves.append(moves);
        currentLocation = moves.getLastLocation();
    }
    public void addOrder(ProcessedOrder order, DroneMoveList newMoves){
        // TODO: Implement, including decrementing remainingshortmoves.
        remainingShortMoves -= newMoves.totalShortMoveCountEstimate();

        System.out.println("Adding an order " + order.getTotalPrice() + " with " + newMoves.points.size()
                + " moves. " + remainingShortMoves + " moves left.");
        completedOrders.add(order);
        currentMoves.append(newMoves);
        currentLocation = currentMoves.getLastLocation();
    }
    public void writeToOutput(String filename){
        int totalMoney = 0;
        for (ProcessedOrder order : completedOrders){
            totalMoney += order.getTotalPrice();
        }

        ArrayList<Point> points = new ArrayList<>();

        for (DroneWaypoint waypoint : currentMoves.points){
            points.add(Point.fromLngLat(waypoint.point.x, waypoint.point.y));
        }

        LineString lines = LineString.fromLngLats(points);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(lines));
        String json = featureCollection.toJson();

        try {
            Files.write( Paths.get(filename), json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Writing to database. Total value: " + totalMoney);
    }
}