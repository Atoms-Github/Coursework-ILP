package uk.ac.ed.inf;


import hostedhandles.DatabaseHandle;
import hostedhandles.WebsiteHandle;
import routing.Order;

import java.sql.SQLException;

public class App
{
    public static void main( String[] args ) throws SQLException {
        // So. What information do we need?
        // 1. The orders, from the database.
        // 2. The shop information, from the website.
        // 3. The no-fly zones, from the website.

        System.out.println("Starting!");
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");
        for (Order order : database.getOrders()){
            System.out.println(order.deliveryDate + " " + order.deliveryTarget + order.customerID);
        }
    }
}
