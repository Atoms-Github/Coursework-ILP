import routing.DroneRouteResults;
import routing.DroneRouter;
import routing.PathingTechnique;
import world.DroneArea;
import world.MapPoint;
import orders.Cafe;
import orders.Order;
import inputOutput.IOMenus;
import inputOutput.DatabaseHandle;
import inputOutput.WebsiteHandle;

import java.sql.SQLException;
import java.util.ArrayList;

public class App
{
    public static void main( String[] args ) throws SQLException {
        String dateStringFilename = args[0] + "-" + args[1] + "-" + args[2];
        String dateStringDatabase = args[2] + "-" + args[1] + "-" + args[0];

        long startTimeMilis = System.currentTimeMillis();
        WebsiteHandle website = new WebsiteHandle("localhost", args[3]);
        DatabaseHandle database = new DatabaseHandle("localhost", args[4]);

        IOMenus menus = website.fetchParsedMenus();
        ArrayList<Cafe> cafes = menus.processCafes(website);
        ArrayList<Order> orders = database.getProcessedOrders(website, cafes, dateStringDatabase);

        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchLandmarks(), website.fetchParsedMenus());
        DroneRouter router = new DroneRouter(area, menus, cafes);
        DroneRouteResults resultsPricePerMove = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, orders, PathingTechnique.MAX_PRICE_PER_MOVE);
        DroneRouteResults resultsMaxOrders = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, orders, PathingTechnique.MAX_ORDER_COUNT);

        DroneRouteResults bestResults;
        if (resultsPricePerMove.getTotalPrice() > resultsMaxOrders.getTotalPrice()){
            bestResults = resultsPricePerMove;
            System.out.println("Best results by price_per_move.");
        }else{
            bestResults = resultsMaxOrders;
            System.out.println("Best results by maximizing_orders.");
        }
        bestResults.writeToOutput("drone-" + dateStringFilename + ".geojson", database);

        System.out.println("Completed in " + (System.currentTimeMillis() - startTimeMilis) + "ms.");
    }
}
















