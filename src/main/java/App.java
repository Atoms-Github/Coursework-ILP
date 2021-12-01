import routing.DroneRouteResults;
import routing.DroneRouter;
import routing.PathingTechnique;
import world.drone.DroneArea;
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
        ArrayList<Cafe> cafes = menus.getProcessedCafes(website);
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









// TODO: Do DB order thing and only request for 1 day. We don't care about multiple days. Don't load them all.

// TODO: Test the max orders thing, by running from some earlier dates. Max should win then.


// TODO: Make sure moves forward 1 extra to get closer where it can, instead of settling for 1 off, since that may, tho unlikely, cause issue - check going close to no fly zone.


// TODO: Test Test, especially close to no-fly-zones, and hovering. Also test 'current order' field of DB. Should no include back to shop flight.

// TODO: Debug print, with colour per order?