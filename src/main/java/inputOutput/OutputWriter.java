package inputOutput;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import routing.DroneRouter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OutputWriter {
    private final String filename;
    private final DatabaseHandle database;

    public OutputWriter(String filename, DatabaseHandle database) {
        this.filename = filename;
        this.database = database;
    }
    private void writeGeoJson(ArrayList<IODroneAction> droneActions){
        ArrayList<Point> points = new ArrayList<>();
        if (droneActions.size() > 0){
            points.add(droneActions.get(0).from.toGeoPoint());
            for (int i = 1; i < droneActions.size(); i++) {
                double distanceToFrom = droneActions.get(i - 1).to.distanceTo(droneActions.get(i).from);
                if (distanceToFrom > 0.0){
                    System.err.println("GeoJson seemed to jump?");
                }

            }
        }
        for (IODroneAction action : droneActions){
            double distanceFromTo = action.from.distanceTo(action.to);
            double diff15 = Math.abs(distanceFromTo - DroneRouter.SHORT_MOVE_LENGTH);
            boolean good = distanceFromTo == 0.0 || diff15 < DroneRouter.SHORT_MOVE_LENGTH / 10;
            if (!good){
                System.err.println("Bad distance! " + distanceFromTo + " : " + diff15);
            }
            points.add(action.to.toGeoPoint());
        }

        LineString lines = LineString.fromLngLats(points);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(lines));
        String json = featureCollection.toJson();
        try {
            Files.write( Paths.get(filename), json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void write(ArrayList<IODroneAction> droneActions, List<IOCompletedOrder> orders) throws SQLException {
        writeGeoJson(droneActions);
        database.writeTodatabase(droneActions, orders);
    }
}
