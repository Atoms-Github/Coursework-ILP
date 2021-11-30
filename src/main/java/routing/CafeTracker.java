package routing;

import orders.Cafe;
import inputOutput.IOMenus;
import world.MapPoint;
import orders.Order;
import orders.OrderItem;

import java.util.HashMap;
import java.util.List;

public class CafeTracker {
    public IOMenus menus;
    public List<Cafe> cafes;
    public HashMap<Cafe, Integer> itemsLeftPerCafe; // Storing this here instead of in cafes, so ProcessedCafes can be immutable.

    public CafeTracker(IOMenus menus, List<Cafe> cafes, List<Order> orders) {
        this.menus = menus;
        this.cafes = cafes;
        this.itemsLeftPerCafe = new HashMap<>();
        for (Cafe cafe : cafes){
            this.itemsLeftPerCafe.put(cafe, 0);
        }
        for (Order order : orders){
            for (OrderItem item : order.orderItems){
                assert this.itemsLeftPerCafe.containsKey(item.shop); // If not, then we've been passed orders which contain a cafe not in the list of cafes.
                this.itemsLeftPerCafe.put(item.shop, this.itemsLeftPerCafe.get(item.shop) + 1);
            }
        }
    }
    public void completeOrder(Order order){
        for (OrderItem item : order.orderItems){
            this.itemsLeftPerCafe.put(item.shop, this.itemsLeftPerCafe.get(item.shop) - 1);
        }
    }

    // Have data about what shops have items left.
    public MapPoint getClosestShopWithItemsLeft(MapPoint location){
        MapPoint closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (Cafe cafe : cafes){
            if (itemsLeftPerCafe.get(cafe) > 0){
                double myDistance = location.distanceTo(cafe.location.point);
                if (myDistance < closestDistance){
                    closest = cafe.location.point;
                    closestDistance = myDistance;
                }
            }
        }
        return closest;
    }
}

// TODO: Could add garbo @NotNulls everywhere.