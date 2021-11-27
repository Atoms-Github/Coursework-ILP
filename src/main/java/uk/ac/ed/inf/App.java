package uk.ac.ed.inf;


import dataDownload.DatabaseHandle;
import dataDownload.WebsiteHandle;
import routing.DroneArea;
import routing.DroneRouteResults;
import routing.DroneRouter;
import dataDownload.DBOrder;
import routing.ProcessedOrder;

import java.sql.SQLException;
import java.util.ArrayList;

public class App
{
    public static void main( String[] args ) throws SQLException {
        // So. What information do we need?
        // 1. The orders, from the database.
        // 2. The shop information, from the website.
        // 3. The no-fly zones, from the website.

        // Got them.

        // The plan:
        // Try 3 different algorithms. Choose the one with the most profit.
        // Algorithms are:
        // 1. Greedy - choose nearest next order's starting point.
        // 2. Profit - choose next order which will give most profit per distance travelled.
        // 3. Complicated - design my own algorithm to try to get something good. E.g. brute force with a timer, or similar.

        // We're going to put a 'landmark' on all 4 corners of a rectangle surrounding the polygon of the no-fly zones. (out a bit, so no intersection).

        System.out.println("Starting!");
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");
        var processedOrders = new ArrayList<ProcessedOrder>();
        var orders = database.getOrders();
        for (DBOrder order : orders){
            processedOrders.add(order.process());
        }

        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchParsedMenus());
        DroneRouter router = new DroneRouter(area);
        DroneRouteResults results = router.calculateDroneMoves(MapPoint.APPLETON_TOWER, processedOrders);

    }
}
