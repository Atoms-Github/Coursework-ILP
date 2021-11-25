package io;

import routing.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandle {
    private final String machineName;
    private final String port;

    private Connection dbConnection = null;
    private Connection getConnection() throws SQLException {
        if (dbConnection == null){
            dbConnection = DriverManager.getConnection("jdbc:derby://" + machineName + ":" + port + "/derbyDB");
        }
        return dbConnection;
    }
    public DatabaseHandle(String machineName, String port) {
        this.machineName = machineName;
        this.port = port;
    }

    public ArrayList<Order> getOrders() throws SQLException {
        HashMap<String, ArrayList<String>> orderDetails = new HashMap<>();
        ResultSet orderDetailsResults = getConnection().createStatement().executeQuery("SELECT * FROM orderdetails");
        while (orderDetailsResults.next()){
            String orderNumber = orderDetailsResults.getString("OrderNo");
            if (!orderDetails.containsKey(orderNumber)){
                orderDetails.put(orderNumber, new ArrayList<>());
            }
            orderDetails.get(orderNumber).add(orderDetailsResults.getString("Item"));
        }

        ArrayList<Order> foundOrders = new ArrayList<>();
        ResultSet ordersResultsSet = getConnection().createStatement().executeQuery("SELECT * FROM orders");
        while (ordersResultsSet.next()){
            String orderNumber = ordersResultsSet.getString("OrderNo");
            Order newOrder = new Order(
                    orderNumber,
                    ordersResultsSet.getDate("DeliveryDate"),
                    ordersResultsSet.getString("Customer"),
                    ordersResultsSet.getString("DeliverTo"),
                    orderDetails.getOrDefault(orderNumber, new ArrayList<>())
            );
            foundOrders.add(newOrder);
        }


        return foundOrders;
    }
    public void postResultsToDB(){
        // TODO. Also use preparared statements, as in PDF.

    }
}
