package orders;

import world.*;
import world.DroneArea;
import drone.DroneTaskPoint;
import drone.MoveList;

import java.util.ArrayList;
import java.util.HashSet;

public class Order {
    /**
     * The price in pence of a delivery.
     */
    public static final int DELIVERY_COST = 50;

    public final String orderNo;
    public final NamedMapPoint deliveryTarget;
    public final ArrayList<OrderItem> orderItems;

    public Order(String orderNo, NamedMapPoint deliveryTarget, ArrayList<OrderItem> orderItems) {
        this.orderNo = orderNo;
        this.deliveryTarget = deliveryTarget;
        this.orderItems = orderItems;
    }

    /**
     * Gets where the shops that make up this order are located, not including duplicates.
     * @return List of locations.
     */
    public ArrayList<MapPoint> getShopLocations(){
        // Use a set because we want to remove duplicates.
        HashSet<MapPoint> locationsSet = new HashSet<>();
        for (OrderItem item : orderItems){
            locationsSet.add(item.shop.location.point);
        }
        ArrayList<MapPoint> locations = new ArrayList<>(locationsSet);
        // Must have between 1 to 2 shops. If not, this is an invalid order, and it should have never been formed.
        assert locations.size() >= 1 && locations.size() <= 2;
        return locations;
    }

    /**
     * Works out the DroneTaskPoints that the drone would need to go to to complete this order.
     * @param start Where the drone currently is.
     * @param area The area to fly around, including the no-fly zones.
     * @return A list of DroneTaskPoints, or null if this order is impossible to fulfill from here.
     */
    public MoveList getDroneMovesForOrder(MapPoint start, DroneArea area){
        ArrayList<MapPoint> shopsLocations = getShopLocations();

        // Start the route empty, and add to it.
        MoveList wholeRoute = new MoveList(new ArrayList<>());
        // Start at the start.
        wholeRoute.pushEnd(new DroneTaskPoint(start, false));
        // While there are still shops that need visited:
        while (shopsLocations.size() > 0){
            MapPoint closestShopLocation = start.getClosestPoint(shopsLocations);
            // Pathfind to closest shop.
            MoveList pathToClosest = area.pathfind(wholeRoute.getLastLocation(), closestShopLocation);
            if (pathToClosest == null){
                return null; // Can't route to this shop.
            }
            // Hover at the shop.
            pathToClosest.getLastWaypoint().setMustHover(true);
            // Add this to the list to complete.
            wholeRoute.append(pathToClosest);
            // Don't need to travel to this point anymore.
            shopsLocations.remove(closestShopLocation);
        }
        // Now we need to get from the last shop to the customer.
        MoveList pathToEnd = area.pathfind(wholeRoute.getLastLocation(), deliveryTarget.point);
        if (pathToEnd == null){
            return null; // Can't find way to get to end.
        }
        // Must hover at customer.
        pathToEnd.getLastWaypoint().setMustHover(true);
        // Add this to the list to complete.
        wholeRoute.append(pathToEnd);

        return wholeRoute;
    }

    /**
     * @return The total price for all the items in this order, including the delivery charge.
     */
    public int getTotalPrice(){
        int total = DELIVERY_COST;
        for (OrderItem item : orderItems){
            total += item.price;
        }
        return total;
    }
}
