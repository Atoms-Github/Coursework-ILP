package dataDownload;

import routing.DroneAction;
import routing.ProcessedCafe;
import routing.ProcessedOrder;

import java.sql.*;
import java.util.*;

public class DatabaseHandle {
    private final String machineName;
    private final String port;

    private Connection connection = null;
    private Connection getConnection() throws SQLException {
        if (connection == null){
            connection = DriverManager.getConnection("jdbc:derby://" + machineName + ":" + port + "/derbyDB");
        }
        return connection;
    }
    public DatabaseHandle(String machineName, String port) {
        this.machineName = machineName;
        this.port = port;
    }
    public ArrayList<ProcessedOrder> getProcessedOrders(WebsiteHandle website, List<ProcessedCafe> cafes, String dateString) throws SQLException {
        ArrayList<ProcessedOrder> processedOrders = new ArrayList<>();
        var orders = getOrders(dateString);
        for (DBOrder order : orders){
            processedOrders.add(order.process(website, cafes));
        }
        return processedOrders;
    }

    public ArrayList<DBOrder> getOrders(String dateString) throws SQLException {
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
        ResultSet ordersResultsSet = getConnection().createStatement().executeQuery("SELECT * FROM orders WHERE deliverydate = '" + dateString + "'");  // TODO: Query from input.
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
    private void dropTable(String tableName) throws SQLException{
        DatabaseMetaData databaseMetadata = connection.getMetaData();
        ResultSet resultSet = databaseMetadata.getTables(null, null, tableName, null);
        // If the resultSet is not empty then the table exists, so we can drop it
        if (resultSet.next()) {
            Statement statement = connection.createStatement();
            statement.execute("drop table " + tableName);
        }
    }
    private void setupOutputTables() throws SQLException {
        dropTable("DELIVERIES");
        dropTable("FLIGHTPATH");

        Statement statement = connection.createStatement();
        statement.execute(
                "create table deliveries(orderNo char(8)," +
                        "deliveredTo varchar(19)," +
                        "costInPence int)");
        statement.execute(
                "create table flightpath(orderNo char(8)," +
                        "fromLongitude double," +
                        "fromLatitude double," +
                        "angle integer," +
                        "toLongitude double," +
                        "toLatitude double)");
    }
    public void writeTodatabase(ArrayList<DroneAction> droneActions) throws SQLException {
        HashSet<ProcessedOrder> completedOrders = new HashSet<>();
        setupOutputTables();

        PreparedStatement psActions = connection.prepareStatement("insert into flightpath values " +
                "(?, ?, ?, ?, ?, ?)");
        for (DroneAction action : droneActions){
            String orderNo = "noorders";
            if (action.order != null){
                orderNo = action.order.orderNo;
                completedOrders.add(action.order);
            }
            psActions.setString(1, orderNo);
            psActions.setDouble(2, action.from.x);
            psActions.setDouble(3, action.from.y);
            psActions.setInt(4, action.angle);
            psActions.setDouble(5, action.to.x);
            psActions.setDouble(6, action.to.y);
            psActions.execute();

        }
        PreparedStatement psDeliveries = connection.prepareStatement("insert into deliveries values (?, ?, ?)");
        for(ProcessedOrder order : completedOrders){
            psDeliveries.setString(1, order.orderNo);
            psDeliveries.setString(2, order.deliveryTarget.whatThreeWordsLoc);
            psDeliveries.setInt(3, order.getTotalPrice());
            psDeliveries.execute();
        }

    }
}








