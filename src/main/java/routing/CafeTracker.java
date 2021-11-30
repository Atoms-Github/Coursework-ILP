package routing;

import data.MapPoint;
import data.ProcessedCafe;
import data.ProcessedOrder;
import data.ProcessedOrderItem;
import inputOutput.JsonMenus;

import java.util.HashMap;
import java.util.List;

public class CafeTracker {
    public JsonMenus menus;
    public List<ProcessedCafe> cafes;
    public HashMap<ProcessedCafe, Integer> itemsLeftPerCafe; // Storing this here instead of in cafes, so ProcessedCafes can be immutable.

    public CafeTracker(JsonMenus menus, List<ProcessedCafe> cafes, List<ProcessedOrder> orders) {
        this.menus = menus;
        this.cafes = cafes;
        this.itemsLeftPerCafe = new HashMap<>();
        for (ProcessedCafe cafe : cafes){
            this.itemsLeftPerCafe.put(cafe, 0);
        }
        for (ProcessedOrder order : orders){
            for (ProcessedOrderItem item : order.orderItems){
                assert this.itemsLeftPerCafe.containsKey(item.shop); // If not, then we've been passed orders which contain a cafe not in the list of cafes.
                this.itemsLeftPerCafe.put(item.shop, this.itemsLeftPerCafe.get(item.shop) + 1);
            }
        }
    }
    public void completeOrder(ProcessedOrder order){
        for (ProcessedOrderItem item : order.orderItems){
            this.itemsLeftPerCafe.put(item.shop, this.itemsLeftPerCafe.get(item.shop) - 1);
        }
    }

    // Have data about what shops have items left.
    public MapPoint getClosestShopWithItemsLeft(MapPoint location){
        MapPoint closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (ProcessedCafe cafe : cafes){
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