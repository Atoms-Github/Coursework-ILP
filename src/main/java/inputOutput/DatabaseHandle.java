package inputOutput;

import inputOutput.input.IOOrder;
import inputOutput.output.IOCompletedOrder;
import inputOutput.output.IODroneAction;
import orders.Cafe;
import orders.Order;

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
    public ArrayList<Order> getProcessedOrders(WebsiteHandle website, List<Cafe> cafes, String dateString) throws SQLException {
        ArrayList<Order> processedOrders = new ArrayList<>();
        var orders = getOrders(dateString);
        for (IOOrder order : orders){
            processedOrders.add(order.process(website, cafes));
        }
        return processedOrders;
    }

    public ArrayList<IOOrder> getOrders(String dateString) throws SQLException {
        PreparedStatement detailsPrepared = getConnection().prepareStatement("SELECT * FROM orderdetails WHERE orderno = ?");
        ArrayList<IOOrder> foundOrders = new ArrayList<>();
        ResultSet ordersResultsSet = getConnection().createStatement().executeQuery("SELECT * FROM orders WHERE deliverydate = '" + dateString + "'");
        while (ordersResultsSet.next()){
            String orderNumber = ordersResultsSet.getString("OrderNo");

            ArrayList<String> orderItems = new ArrayList<>();
            detailsPrepared.setString(1, orderNumber);
            ResultSet detailsResults = detailsPrepared.executeQuery();
            while (detailsResults.next()){
                orderItems.add(detailsResults.getString("Item"));
            }

            IOOrder newOrder = new IOOrder(
                    orderNumber,
                    ordersResultsSet.getDate("DeliveryDate"),
                    ordersResultsSet.getString("Customer"),
                    ordersResultsSet.getString("DeliverTo"),
                    orderItems
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
    public void writeTodatabase(List<IODroneAction> droneActions, List<IOCompletedOrder> completedOrders) throws SQLException {
        setupOutputTables();

        PreparedStatement psActions = connection.prepareStatement("insert into flightpath values " +
                "(?, ?, ?, ?, ?, ?)");
        for (IODroneAction action : droneActions){
            psActions.setString(1, action.orderNo);
            psActions.setDouble(2, action.from.x);
            psActions.setDouble(3, action.from.y);
            psActions.setInt(4, action.angle);
            psActions.setDouble(5, action.to.x);
            psActions.setDouble(6, action.to.y);
            psActions.execute();

        }
        PreparedStatement psDeliveries = connection.prepareStatement("insert into deliveries values (?, ?, ?)");
        for(IOCompletedOrder order : completedOrders){
            psDeliveries.setString(1, order.orderNo);
            psDeliveries.setString(2, order.deliveredTo);
            psDeliveries.setInt(3, order.costPence);
            psDeliveries.execute();
        }

    }

}








