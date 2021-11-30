package dataDownload;

import routing.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DBOrder {
    public final String orderNo;
    public final Date deliveryDate;
    public final String customerID;
    public final String deliveryTarget;
    public final List<String> orderItems;

    public DBOrder(String orderNo, Date deliveryDate, String customerID, String deliveryTarget, List<String> orderItems) {
        this.orderNo = orderNo;
        this.deliveryDate = deliveryDate;
        this.customerID = customerID;
        this.deliveryTarget = deliveryTarget;
        this.orderItems = orderItems;
    }
    public ProcessedOrder process(WebsiteHandle handle, List<ProcessedCafe> cafes){
        ArrayList<ProcessedOrderItem> processedOrderItems = new ArrayList<>();
        for (String orderItemName : orderItems){
            for (ProcessedCafe cafe : cafes){
                if (cafe.menu.containsKey(orderItemName)){
                    processedOrderItems.add(new ProcessedOrderItem(orderItemName, cafe, cafe.menu.get(orderItemName)));
                    break;
                }
            }
        }

        return new ProcessedOrder(orderNo, handle.fetchWhatThreeWordsBox(deliveryTarget), processedOrderItems);
    }
}














