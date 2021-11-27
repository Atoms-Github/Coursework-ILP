package routing;

import dataDownload.CafeMenus;
import uk.ac.ed.inf.DroneUtils;
import uk.ac.ed.inf.MapPoint;
import java.util.ArrayList;

public class ProcessedOrder {
    public final String orderNo;
    public final NamedMapPoint deliveryTarget;
    public final ArrayList<String> orderItems;

    public ProcessedOrder(String orderNo, NamedMapPoint deliveryTarget, ArrayList<String> orderItems) {
        this.orderNo = orderNo;
        this.deliveryTarget = deliveryTarget;
        this.orderItems = orderItems;
    }

    public ArrayList<MapPoint> getShopLocations(){
        return null; // TODO. Also must clone.
    }
    public DroneMoveList getDroneMovesForOrder(MapPoint start, DroneArea area){
        var orderShops = getShopLocations();
        assert orderShops.size() > 0;
        var totalRoute = new DroneMoveList(new ArrayList<>());
        while (orderShops.size() > 0){
            // TODO: Add hover information.
            var closestShop = DroneUtils.getClosestPoint(orderShops, start);
            var pathToClosest = area.pathfind(start, closestShop);
            totalRoute.append(pathToClosest);
            orderShops.remove(closestShop);
        }
        // Now we need to get from the last shop to the customer.
        var pathToEnd = area.pathfind(totalRoute.points.get(totalRoute.points.size() - 1).point, deliveryTarget.point);
        totalRoute.append(pathToEnd);

        return totalRoute;
    }
    public int getTotalPrice(CafeMenus menus){
        return 0; // TODO:
    }
}
