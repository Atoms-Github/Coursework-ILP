package inputOutput;

import inputOutput.input.IOOrder;
import inputOutput.output.IOCompletedOrder;
import inputOutput.output.IODroneAction;
import orders.Shop;
import orders.Order;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DatabaseHandle {
    private final String machineName;
    private final String port;


    /**
     * Lazily initialize the connection, to avoid unnecessary work.
     */
    private Connection connection = null;

    private Connection getConnection() throws SQLException {
        if (connection == null){
            // Try to connect to database.
            connection = DriverManager.getConnection("jdbc:derby://" + machineName + ":" + port + "/derbyDB");
        }
        return connection;
    }
    public DatabaseHandle(String machineName, String port) {
        this.machineName = machineName;
        this.port = port;
    }

    /**
     * Queries the database for orders.
     * @param website Website to resolve WTW locations on.
     * @param shops All shops that sell everything in the orders that'll be returned.
     * @param dateString The date to get orders for. Formatted 'YYYY-MM-DD'.
     * @return List of useful Order instances.
     * @throws SQLException Problem with database.
     * @throws IOException Problem with website.
     * @throws InterruptedException Problem with website.
     */
    public ArrayList<Order> getProcessedOrders(WebsiteHandle website, List<Shop> shops, String dateString) throws SQLException, IOException, InterruptedException {
        ArrayList<Order> processedOrders = new ArrayList<>();
        var orders = getIOOrders(dateString);
        for (IOOrder order : orders){
            processedOrders.add(order.process(website, shops));
        }
        return processedOrders;
    }

    private ArrayList<IOOrder> getIOOrders(String dateString) throws SQLException {
        PreparedStatement detailsPrepared = getConnection().prepareStatement("SELECT * FROM orderdetails WHERE orderno = ?");
        ArrayList<IOOrder> queriedOrders = new ArrayList<>();
        // Query for all orders on the specified day.
        ResultSet ordersResultsSet = getConnection().createStatement().executeQuery("SELECT * FROM orders WHERE deliverydate = '" + dateString + "'");
        while (ordersResultsSet.next()){
            String orderNumber = ordersResultsSet.getString("OrderNo");

            // Query database again, to find all items in this particular order.
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
            queriedOrders.add(newOrder);
        }


        return queriedOrders;
    }

    /**
     * Deletes a table in the database.
     */
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
    private void fillOutputTables(List<IODroneAction> droneActions, List<IOCompletedOrder> completedOrders) throws SQLException {
        // Use prepared statements for efficiency.
        // These classes are already in perfect structure, so we can just read straight from them.
        PreparedStatement psActions = connection.prepareStatement("insert into flightpath values " +
                "(?, ?, ?, ?, ?, ?)");
        for (IODroneAction action : droneActions){
            psActions.setString(1, action.orderNo);
            psActions.setDouble(2, action.from.getLongitude());
            psActions.setDouble(3, action.from.getLatitude());
            psActions.setInt(4, action.angle);
            psActions.setDouble(5, action.to.getLongitude());
            psActions.setDouble(6, action.to.getLatitude());
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
    /**
     * Inserts program output data into the database.
     * @param droneActions The actions the drone performed.
     * @param completedOrders The orders the drone fulfilled.
     * @throws SQLException If problem with database.
     */
    public void writeTodatabase(List<IODroneAction> droneActions, List<IOCompletedOrder> completedOrders) throws SQLException {
        setupOutputTables();
        fillOutputTables(droneActions, completedOrders);
    }
}








