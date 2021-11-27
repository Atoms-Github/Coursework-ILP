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
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");

        CafeMenus menu = website.fetchParsedMenus();

        ArrayList<ProcessedCafe> processedCafes = menu.getProcessedCafes(website);
        ArrayList<ProcessedOrder> processedOrders = database.getProcessedOrders(website, processedCafes);


        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchParsedMenus());
        DroneRouter router = new DroneRouter(area);
        DroneRouteResults results = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, processedOrders);
        results.writeToDatabase(); // TODO: Run it twice, once trying to get all orders, and post the best one.
    }
}
