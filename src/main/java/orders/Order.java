package orders;

import world.*;
import world.drone.DroneArea;
import world.drone.DroneWaypoint;
import world.drone.MoveList;

import java.util.ArrayList;
import java.util.HashSet;

public class Order {
    public final String orderNo;
    public final NamedMapPoint deliveryTarget;
    public final ArrayList<OrderItem> orderItems;

    public Order(String orderNo, NamedMapPoint deliveryTarget, ArrayList<OrderItem> orderItems) {
        this.orderNo = orderNo;
        this.deliveryTarget = deliveryTarget;
        this.orderItems = orderItems;
    }

    public ArrayList<MapPoint> getShopLocations(){
        HashSet<MapPoint> locationsSet = new HashSet<>();
        for (OrderItem item : orderItems){
            locationsSet.add(item.shop.location.point);
        }
        ArrayList<MapPoint> locations = new ArrayList<>(locationsSet);
        assert locations.size() >= 1 && locations.size() <= 2;
        return locations;
    }
    public MoveList getDroneMovesForOrder(MapPoint start, DroneArea area){
        ArrayList<MapPoint> orderShops = getShopLocations();
        MoveList totalRoute = new MoveList(new ArrayList<>());
        totalRoute.points.add(new DroneWaypoint(start, false));
        while (orderShops.size() > 0){
            MapPoint closestShop = start.getClosestPoint(orderShops);
            MoveList pathToClosest = area.pathfind(totalRoute.getLastLocation(), closestShop);
            pathToClosest.points.get(pathToClosest.points.size() - 1).mustHover = true;
            totalRoute.append(pathToClosest);
            orderShops.remove(closestShop);
        }
        // Now we need to get from the last shop to the customer.
        MoveList pathToEnd = area.pathfind(totalRoute.getLastLocation(), deliveryTarget.point);
        totalRoute.append(pathToEnd);

        return totalRoute;
    }
    public int getTotalPrice(){
        int total = 50; // 50p delivery charge.
        for (OrderItem item : orderItems){
            total += item.price;
        }
        return total;
    }
}
