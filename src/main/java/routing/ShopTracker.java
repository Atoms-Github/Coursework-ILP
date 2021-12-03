package routing;

import orders.Shop;
import world.MapPoint;
import orders.Order;
import orders.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopTracker {
    private final HashMap<Shop, Integer> itemsLeftPerShop; // Storing this here instead of in shops, so ProcessedShops can be immutable.

    public ShopTracker(List<Shop> shops, List<Order> orders) {
        this.itemsLeftPerShop = new HashMap<>();
        // Init all shops to 0.
        for (Shop shop : shops){
            this.itemsLeftPerShop.put(shop, 0);
        }
        // Count how many items are in each shop.
        for (Order order : orders){
            for (OrderItem item : order.orderItems){
                assert this.itemsLeftPerShop.containsKey(item.shop); // If not, then we've been passed orders which contain a shop not in the list of shops.
                this.itemsLeftPerShop.put(item.shop, this.itemsLeftPerShop.get(item.shop) + 1);
            }
        }
    }

    /**
     * Mark an order as completed.
     */
    public void completeOrder(Order order){
        // Decrement itemsLeftPerShop for all.
        for (OrderItem item : order.orderItems){
            this.itemsLeftPerShop.put(item.shop, this.itemsLeftPerShop.get(item.shop) - 1);
        }
    }

    /**
     * @param location What point to try to get closest to.
     * @param shops List of all shops to consider.
     * @return Position of shop.
     */
    public MapPoint getClosestShopWithItemsLeft(MapPoint location, List<Shop> shops){
        ArrayList<MapPoint> positions = new ArrayList<>();
        for (Shop shop : shops){
            // Only care about those with items left.
            if (itemsLeftPerShop.get(shop) > 0){
                positions.add(shop.location.point);
            }
        }
        return location.getClosestPoint(positions);
    }
}

