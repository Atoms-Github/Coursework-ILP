package inputOutput.output;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import inputOutput.DatabaseHandle;
import routing.DroneRouter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OutputWriter {
    /**
     * The name of the file to write the geojson to.
     */
    private final String filename;
    /**
     * The database to write the flightpath and deliveries to.
     */
    private final DatabaseHandle database;

    public OutputWriter(String filename, DatabaseHandle database) {
        this.filename = filename;
        this.database = database;
    }
    private void writeGeoJson(ArrayList<IODroneAction> droneActions) throws IOException {
        ArrayList<Point> lineStringPoints = new ArrayList<>();
        if (droneActions.size() > 0){
            // Add the first point to the output geojson linestring (since it won't be covered later).
            lineStringPoints.add(droneActions.get(0).from.toGeoPoint());
            // Check that the end of each drone action is the same as the start of the next one.
            for (int i = 1; i < droneActions.size(); i++) {
                double distanceToFrom = droneActions.get(i - 1).to.distanceTo(droneActions.get(i).from);
                if (distanceToFrom > 0.0){
                    System.err.println("Drone actions included teleport?");
                }

            }
        }
        for (IODroneAction action : droneActions){
            // Add the action's 'to' point to the geojson output.
            lineStringPoints.add(action.to.toGeoPoint());
            // Check that the distance is either 0 for hover, or the proper length for a movement.
            double distanceFromTo = action.from.distanceTo(action.to);
            double diff15 = Math.abs(distanceFromTo - DroneRouter.SHORT_MOVE_LENGTH);
            boolean good = distanceFromTo == 0.0 || diff15 < DroneRouter.SHORT_MOVE_LENGTH / 10;
            if (!good){
                System.err.println("Bad distance! " + distanceFromTo + " : " + diff15);
            }
        }
        // Write the linestring to a geojson file.
        LineString lines = LineString.fromLngLats(lineStringPoints);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(lines));
        String json = featureCollection.toJson();
        Files.write( Paths.get(filename), json.getBytes());
    }

    /**
     * Writes the given drone route to a geojson file, and to the database.
     * @param droneActions All the actions the drone did.
     * @param orders All the orders that the drone completed.
     * @throws SQLException Problem accessing database.
     * @throws IOException Problem writing geojson file.
     */
    public void write(ArrayList<IODroneAction> droneActions, List<IOCompletedOrder> orders) throws SQLException, IOException {
        System.out.println("Writing " + droneActions.size() + " drone actions to geojson and database.");
        System.out.println("Writing " + orders.size() + " completed orders to database.");
        writeGeoJson(droneActions);
        database.writeTodatabase(droneActions, orders);
    }
}
