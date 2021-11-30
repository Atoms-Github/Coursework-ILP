package routing;

import data.MapPoint;
import data.ProcessedCafe;
import data.ProcessedOrder;
import inputOutput.JsonMenus;
import inputOutput.DatabaseHandle;
import inputOutput.WebsiteHandle;

import java.sql.SQLException;
import java.util.ArrayList;

public class App
{
    public static void main( String[] args ) throws SQLException {
        String dateStringFilename = args[0] + "-" + args[1] + "-" + args[2];
        String dateStringDatabase = args[2] + "-" + args[1] + "-" + args[0];

        long milis = System.currentTimeMillis();
        WebsiteHandle website = new WebsiteHandle("localhost", args[3]);
        DatabaseHandle database = new DatabaseHandle("localhost", args[4]);

        JsonMenus menus = website.fetchParsedMenus();
        ArrayList<ProcessedCafe> processedCafes = menus.getProcessedCafes(website);
        ArrayList<ProcessedOrder> processedOrders = database.getProcessedOrders(website, processedCafes, dateStringDatabase);

        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchLandmarks(), website.fetchParsedMenus());
        DroneRouter router = new DroneRouter(area, menus, processedCafes);
        DroneRouteResults resultsPricePerMove = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, processedOrders, PathingTechnique.MAX_PRICE_PER_MOVE);
        DroneRouteResults resultsMaxOrders = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, processedOrders, PathingTechnique.MAX_ORDER_COUNT);

        DroneRouteResults bestResults;
        if (resultsPricePerMove.getTotalPrice() > resultsMaxOrders.getTotalPrice()){
            bestResults = resultsPricePerMove;
            System.out.println("Best results by price_per_move.");
        }else{
            bestResults = resultsMaxOrders;
            System.out.println("Best results by maximizing_orders.");
        }
        bestResults.writeToOutput("drone-" + dateStringFilename + ".geojson", database);

        System.out.println("Completed in " + (System.currentTimeMillis() - milis) + "ms.");
    }
}
// TODO: Check dependency output thing for geobox json, as mentioned. Runtime dependency.



// TODO: Wash everything with private, and getters setters, or final.




// TODO: Do DB order thing and only request for 1 day. We don't care about multiple days. Don't load them all.

// TODO: Test the max orders thing, by running from some earlier dates. Max should win then.


// TODO: Make sure moves forward 1 extra to get closer where it can, instead of settling for 1 off, since that may, tho unlikely, cause issue - check going close to no fly zone.


// TODO: Test Test, especially close to no-fly-zones, and hovering. Also test 'current order' field of DB. Should no include back to shop flight.
