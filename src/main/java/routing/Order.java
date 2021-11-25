package routing;

import org.apache.derby.client.am.DateTime;

import java.sql.Date;
import java.util.ArrayList;

public class Order {
    public final String orderNo;
    public final Date deliveryDate;
    public final String customerID;
    public final String deliveryTarget;
    public final ArrayList<String> orderItems;

    public Order(String orderNo, Date deliveryDate, String customerID, String deliveryTarget, ArrayList<String> orderItems) {
        this.orderNo = orderNo;
        this.deliveryDate = deliveryDate;
        this.customerID = customerID;
        this.deliveryTarget = deliveryTarget;
        this.orderItems = orderItems;
    }
}
