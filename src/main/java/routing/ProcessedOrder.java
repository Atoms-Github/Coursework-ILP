package routing;

import dataDownload.CafeMenus;
import uk.ac.ed.inf.DroneUtils;
import uk.ac.ed.inf.MapPoint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProcessedOrder {
    public final String orderNo;
    public final NamedMapPoint deliveryTarget;
    public final ArrayList<ProcessedOrderItem> orderItems;

    public ProcessedOrder(String orderNo, NamedMapPoint deliveryTarget, ArrayList<ProcessedOrderItem> orderItems) {
        this.orderNo = orderNo;
        this.deliveryTarget = deliveryTarget;
        this.orderItems = orderItems;
    }

    public ArrayList<MapPoint> getShopLocations(){
        HashSet<MapPoint> locationsSet = new HashSet<>();
        for (ProcessedOrderItem item : orderItems){
            locationsSet.add(item.shop.location.point);
        }
        ArrayList<MapPoint> locations = new ArrayList<>(locationsSet);
        assert locations.size() >= 1 && locations.size() <= 2;
        return locations;
    }
    public DroneMoveList getDroneMovesForOrder(MapPoint start, DroneArea area){
        ArrayList<MapPoint> orderShops = getShopLocations();
        DroneMoveList totalRoute = new DroneMoveList(new ArrayList<>());
        totalRoute.points.add(new DroneWaypoint(start, false));
        while (orderShops.size() > 0){
            // TODO: Add hover information.
            MapPoint closestShop = DroneUtils.getClosestPoint(orderShops, start);
            DroneMoveList pathToClosest = area.pathfind(totalRoute.getLastLocation(), closestShop);
            totalRoute.append(pathToClosest);
            orderShops.remove(closestShop);
        }
        // Now we need to get from the last shop to the customer.
        var pathToEnd = area.pathfind(totalRoute.getLastLocation(), deliveryTarget.point);
        totalRoute.append(pathToEnd);

        return totalRoute;
    }
    public int getTotalPrice(){
        int total = 50; // 50p delivery charge.
        for (ProcessedOrderItem item : orderItems){
            total += item.price;
        }
        return total;
    }
}
