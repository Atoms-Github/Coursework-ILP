package routing;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import dataDownload.DatabaseHandle;
import dataDownload.WebsiteHandle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class OutputWriter {
    private final String filename;
    private final DatabaseHandle database;

    public OutputWriter(String filename, DatabaseHandle database) {
        this.filename = filename;
        this.database = database;
    }

    public void write(ArrayList<DroneAction> droneActions, ArrayList<ProcessedOrder> completedOrders){
//        ArrayList<Point> points = new ArrayList<>();
//        for (DroneWaypoint waypoint : currentMoves.points){
//            points.add(Point.fromLngLat(waypoint.point.x, waypoint.point.y));
//        }
//        LineString lines = LineString.fromLngLats(points);
//        FeatureCollection featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(lines));
//        String json = featureCollection.toJson();
//
//        try {
//            Files.write( Paths.get(filename), json.getBytes());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        // TODO.
    }
}
