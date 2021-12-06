package routing;

import world.DroneArea;
import world.MapPoint;
import orders.Shop;
import orders.Order;
import inputOutput.DatabaseHandle;
import inputOutput.WebsiteHandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class App
{
    public static void main( String[] args ) throws SQLException, IOException, InterruptedException {
        // For timing how long the whole operation takes.
        long startTimeMilis = System.currentTimeMillis();

        String dateStringFilename = args[0] + "-" + args[1] + "-" + args[2];
        String dateStringDatabase = args[2] + "-" + args[1] + "-" + args[0];
        String websitePort = args[3];
        String databasePort = args[4];

        // ------------ ------------ ------------ ------------
        // PART 1: LOADING THE INFORMATION:
        // ------------ ------------ ------------ ------------
        WebsiteHandle website = new WebsiteHandle("localhost", websitePort);
        DatabaseHandle database = new DatabaseHandle("localhost", databasePort);
        ArrayList<Shop> shops = website.getShops();
        ArrayList<Order> orders = database.getProcessedOrders(website, shops, dateStringDatabase);
        DroneArea area = new DroneArea(website.fetchNoFlyZones());
        DroneRouter router = new DroneRouter(area, shops);

        // ------------ ------------ ------------ ------------
        // PART 2: RUNNING THE ALGORITHM:
        // ------------ ------------ ------------ ------------
        DroneRouteResults resultsPricePerMove = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, orders, PathingTechnique.MAX_PRICE_PER_MOVE);
        DroneRouteResults resultsMaxOrders = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, orders, PathingTechnique.MAX_ORDER_COUNT);

        // ------------ ------------ ------------ ------------
        // PART 3: WRITING THE RESULTS:
        // ------------ ------------ ------------ ------------
        DroneRouteResults bestResults;
        System.out.println("Price_per_move value = " + resultsPricePerMove.getTotalPrice());
        System.out.println("Maximizing_orders value = " + resultsMaxOrders.getTotalPrice());
        // Chose the result that gives best total price.
        if (resultsPricePerMove.getTotalPrice() > resultsMaxOrders.getTotalPrice()){
            bestResults = resultsPricePerMove;
            System.out.println("Best results by price_per_move on " + dateStringFilename);
        }else{
            bestResults = resultsMaxOrders;
            System.out.println("Best results by maximizing_orders on " + dateStringFilename);
        }
        bestResults.writeToOutput("drone-" + dateStringFilename + ".geojson", database);

        System.out.println("Completed in " + (System.currentTimeMillis() - startTimeMilis) + "ms.");
    }
}
















