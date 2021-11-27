package routing;

import dataDownload.CafeMenus;
import uk.ac.ed.inf.MapPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CafeTracker {
    public CafeMenus menus;
    public ArrayList<ProcessedCafe> cafes;
    public HashMap<ProcessedCafe, Integer> itemsLeftPerCafe; // Storing this here instead of in cafes, so ProcessedCafes can be immutable.
    // TODO: Decrement itemsLeftPerCafe somewhere.

    public CafeTracker(CafeMenus menus, ArrayList<ProcessedCafe> cafes, List<ProcessedOrder> orders) {
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