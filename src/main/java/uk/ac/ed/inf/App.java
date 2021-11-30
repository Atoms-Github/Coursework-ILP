package uk.ac.ed.inf;

import dataDownload.CafeMenus;
import dataDownload.DatabaseHandle;
import dataDownload.WebsiteHandle;
import routing.*;

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

        CafeMenus menus = website.fetchParsedMenus();
        ArrayList<ProcessedCafe> processedCafes = menus.getProcessedCafes(website);
        ArrayList<ProcessedOrder> processedOrders = database.getProcessedOrders(website, processedCafes, dateStringDatabase);

        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchLandmarks(), website.fetchParsedMenus());
        DroneRouter router = new DroneRouter(area, menus, processedCafes);
        DroneRouteResults results = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, processedOrders);
        results.writeToOutput("drone-" + dateStringFilename + ".geojson", database); // TODO: Run it twice, once trying to get all orders, and post the best one. (Maybe?).

        System.out.println("Completed in " + (System.currentTimeMillis() - milis) + "ms.");
    }
}
