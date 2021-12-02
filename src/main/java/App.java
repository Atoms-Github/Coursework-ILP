import routing.DroneRouteResults;
import routing.DroneRouter;
import routing.PathingTechnique;
import world.DroneArea;
import world.MapPoint;
import orders.Cafe;
import orders.Order;
import inputOutput.input.IOMenus;
import inputOutput.DatabaseHandle;
import inputOutput.WebsiteHandle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class App
{
    public static void main( String[] args ) throws SQLException, IOException, InterruptedException {
        String dateStringFilename = args[0] + "-" + args[1] + "-" + args[2];
        String dateStringDatabase = args[2] + "-" + args[1] + "-" + args[0];

        long startTimeMilis = System.currentTimeMillis();
        WebsiteHandle website = new WebsiteHandle("localhost", args[3]);
        DatabaseHandle database = new DatabaseHandle("localhost", args[4]);

        IOMenus menus = website.fetchParsedMenus();
        ArrayList<Cafe> cafes = menus.processCafes(website);
        ArrayList<Order> orders = database.getProcessedOrders(website, cafes, dateStringDatabase);

        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchLandmarks());
        DroneRouter router = new DroneRouter(area, cafes);
        DroneRouteResults resultsPricePerMove = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, orders, PathingTechnique.MAX_PRICE_PER_MOVE);
//        DroneRouteResults resultsMaxOrders = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, orders, PathingTechnique.MAX_ORDER_COUNT);

//        DroneRouteResults bestResults;
//        System.out.println("Price_per_move value = " + resultsPricePerMove.getTotalPrice());
//        System.out.println("Maximizing_orders value = " + resultsMaxOrders.getTotalPrice());
//        if (resultsPricePerMove.getTotalPrice() > resultsMaxOrders.getTotalPrice()){
//            bestResults = resultsPricePerMove;
//            System.out.println("Best results by price_per_move.");
//        }else{
//            bestResults = resultsMaxOrders;
//            System.out.println("Best results by maximizing_orders.");
//        }
        resultsPricePerMove.writeToOutput("drone-" + dateStringFilename + ".geojson", database);

        System.out.println("Completed in " + (System.currentTimeMillis() - startTimeMilis) + "ms.");
    }
    // TODO: Split into 3 large parts, as in report.
    private static void runOperations(String dateStringFilename, String dateStringDatabase, String portWebsite, String portDatabase){

    }
}
















