package inputOutput.input;

import inputOutput.WebsiteHandle;
import orders.Shop;
import orders.Order;
import orders.OrderItem;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class IOOrder {
    /**
     * Order ID as 8 char string.
     */
    public final String orderNo;
    /**
     * Date this order is for.
     */
    public final Date deliveryDate;
    /**
     * Id of receiving customer.
     */
    public final String customerID;
    /**
     * WhatThreeWords location of where the customer will collect this from.
     */
    public final String deliveryTarget;
    /**
     * A list of item names which make up the order.
     */
    public final List<String> orderItems;

    public IOOrder(String orderNo, Date deliveryDate, String customerID, String deliveryTarget, List<String> orderItems) {
        this.orderNo = orderNo;
        this.deliveryDate = deliveryDate;
        this.customerID = customerID;
        this.deliveryTarget = deliveryTarget;
        this.orderItems = orderItems;
    }

    /**
     * Converts this IOOrder into the more useful Order instance.
     * @param handle Website handle to resolve WhatThreeWords addresses.
     * @param shops Shops that the order items present in this order are from.
     * @return The processed order.
     * @throws IOException If problem contacting website.
     * @throws InterruptedException If problem contacting website.
     */
    public Order process(WebsiteHandle handle, List<Shop> shops) throws IOException, InterruptedException {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        for (String orderItemName : this.orderItems){
            // Check all shops to see who sells this order item.
            for (Shop shop : shops){
                if (shop.menu.containsKey(orderItemName)){
                    orderItems.add(new OrderItem(orderItemName, shop, shop.menu.get(orderItemName)));
                    break;
                }
            }
        }
        return new Order(orderNo, handle.fetchWhatThreeWordsBox(deliveryTarget), orderItems);
    }
}














