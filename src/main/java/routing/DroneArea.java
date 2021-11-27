package routing;

import com.mapbox.geojson.*;
import dataDownload.CafeMenus;
import uk.ac.ed.inf.MapPoint;

import java.util.ArrayList;
import java.util.List;

public class DroneArea {
    public FeatureCollection noFlyZones;
    public CafeMenus parsedMenus;
    public ArrayList<MapPoint> additionalWaypoints;

    public DroneArea(FeatureCollection noFlyZones, CafeMenus parsedMenus) {
        this.noFlyZones = noFlyZones;
        this.parsedMenus = parsedMenus;
        // TODO: Work out additionalWaypoints using noFlyZones.
    }

    public DroneMoveList pathfind(MapPoint start, MapPoint end){
        // TODO: Returns whole route, including start and end point.
        var moveList = new DroneMoveList(new ArrayList<>());
        moveList.points.add(new DroneWaypoint(start, false));
        moveList.points.add(new DroneWaypoint(end, false));
        return moveList;
    }
}
