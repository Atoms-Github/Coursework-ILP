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
        long milis = System.currentTimeMillis();
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");

        CafeMenus menus = website.fetchParsedMenus();
        ArrayList<ProcessedCafe> processedCafes = menus.getProcessedCafes(website);
        ArrayList<ProcessedOrder> processedOrders = database.getProcessedOrders(website, processedCafes);



        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchLandmarks(), website.fetchParsedMenus());
        DroneRouter router = new DroneRouter(area, menus, processedCafes);
        DroneRouteResults results = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, processedOrders);
        results.writeToOutput("drone-27-12-2023.geojson", database); // TODO: Run it twice, once trying to get all orders, and post the best one. (Maybe?).

        System.out.println("Completed in " + (System.currentTimeMillis() - milis) + "ms.");
    }
}
