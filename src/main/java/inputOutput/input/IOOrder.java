package inputOutput.input;

import inputOutput.WebsiteHandle;
import orders.Cafe;
import orders.Order;
import orders.OrderItem;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class IOOrder {
    public final String orderNo;
    public final Date deliveryDate;
    public final String customerID;
    public final String deliveryTarget;
    public final List<String> orderItems;

    public IOOrder(String orderNo, Date deliveryDate, String customerID, String deliveryTarget, List<String> orderItems) {
        this.orderNo = orderNo;
        this.deliveryDate = deliveryDate;
        this.customerID = customerID;
        this.deliveryTarget = deliveryTarget;
        this.orderItems = orderItems;
    }
    public Order process(WebsiteHandle handle, List<Cafe> cafes) throws IOException, InterruptedException {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        for (String orderItemName : this.orderItems){
            for (Cafe cafe : cafes){
                if (cafe.menu.containsKey(orderItemName)){
                    orderItems.add(new OrderItem(orderItemName, cafe, cafe.menu.get(orderItemName)));
                    break;
                }
            }
        }

        return new Order(orderNo, handle.fetchWhatThreeWordsBox(deliveryTarget), orderItems);
    }
}














