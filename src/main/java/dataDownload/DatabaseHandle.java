package dataDownload;

import routing.ProcessedCafe;
import routing.ProcessedOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public ArrayList<ProcessedOrder> getProcessedOrders(WebsiteHandle website, List<ProcessedCafe> cafes) throws SQLException {
        ArrayList<ProcessedOrder> processedOrders = new ArrayList<>();
        var orders = getOrders();
        for (DBOrder order : orders){
            processedOrders.add(order.process(website, cafes));
        }
        return processedOrders;
    }

    public ArrayList<DBOrder> getOrders() throws SQLException {
        HashMap<String, ArrayList<String>> orderDetails = new HashMap<>();
        ResultSet orderDetailsResults = getConnection().createStatement().executeQuery("SELECT * FROM orderdetails");
        while (orderDetailsResults.next()){
            String orderNumber = orderDetailsResults.getString("OrderNo");
            if (!orderDetails.containsKey(orderNumber)){
                orderDetails.put(orderNumber, new ArrayList<>());
            }
            orderDetails.get(orderNumber).add(orderDetailsResults.getString("Item"));
        }

        ArrayList<DBOrder> foundOrders = new ArrayList<>();
        ResultSet ordersResultsSet = getConnection().createStatement().executeQuery("SELECT * FROM orders WHERE deliverydate = '2023-12-27'");  // TODO: Query from input.
        while (ordersResultsSet.next()){
            String orderNumber = ordersResultsSet.getString("OrderNo");
            DBOrder newOrder = new DBOrder(
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
