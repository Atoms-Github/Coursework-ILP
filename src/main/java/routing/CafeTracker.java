package routing;

import orders.Cafe;
import inputOutput.input.IOMenus;
import world.MapPoint;
import orders.Order;
import orders.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CafeTracker {
    private final HashMap<Cafe, Integer> itemsLeftPerCafe; // Storing this here instead of in cafes, so ProcessedCafes can be immutable.

    public CafeTracker(List<Cafe> cafes, List<Order> orders) {
        this.itemsLeftPerCafe = new HashMap<>();
        // Init all cafes to 0.
        for (Cafe cafe : cafes){
            this.itemsLeftPerCafe.put(cafe, 0);
        }
        // Count how many items are in each cafe.
        for (Order order : orders){
            for (OrderItem item : order.orderItems){
                assert this.itemsLeftPerCafe.containsKey(item.shop); // If not, then we've been passed orders which contain a cafe not in the list of cafes.
                this.itemsLeftPerCafe.put(item.shop, this.itemsLeftPerCafe.get(item.shop) + 1);
            }
        }
    }

    /**
     * Mark an order as completed.
     */
    public void completeOrder(Order order){
        // Decrement itemsLeftPerCafe for all.
        for (OrderItem item : order.orderItems){
            this.itemsLeftPerCafe.put(item.shop, this.itemsLeftPerCafe.get(item.shop) - 1);
        }
    }

    /**
     * @param location What point to try to get closest to.
     * @param cafes List of all cafes to consider.
     * @return Position of shop.
     */
    public MapPoint getClosestShopWithItemsLeft(MapPoint location, List<Cafe> cafes){
        ArrayList<MapPoint> positions = new ArrayList<>();
        for (Cafe cafe : cafes){
            // Only care about those with items left.
            if (itemsLeftPerCafe.get(cafe) > 0){
                positions.add(cafe.location.point);
            }
        }
        return location.getClosestPoint(positions);
    }
}

