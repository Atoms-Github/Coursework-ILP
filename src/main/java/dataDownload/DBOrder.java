package dataDownload;

import org.apache.derby.client.am.DateTime;
import routing.DroneArea;
import routing.DroneMoveList;
import routing.ProcessedOrder;
import uk.ac.ed.inf.DroneUtils;
import uk.ac.ed.inf.MapPoint;

import java.sql.Date;
import java.util.ArrayList;

public class DBOrder {
    public final String orderNo;
    public final Date deliveryDate;
    public final String customerID;
    public final String deliveryTarget;
    public final ArrayList<String> orderItems;

    public DBOrder(String orderNo, Date deliveryDate, String customerID, String deliveryTarget, ArrayList<String> orderItems) {
        this.orderNo = orderNo;
        this.deliveryDate = deliveryDate;
        this.customerID = customerID;
        this.deliveryTarget = deliveryTarget;
        this.orderItems = orderItems;
    }


    public ProcessedOrder process(){
        return null; // TODO:
    }
}














