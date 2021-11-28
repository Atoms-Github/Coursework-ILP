package uk.ac.ed.inf;

import dataDownload.CafeMenus;
import dataDownload.DatabaseHandle;
import dataDownload.WebsiteHandle;
import org.junit.Test;
import routing.*;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class Tests {
    private static final MapPoint TOP_LEFT_CAFE = new MapPoint(-3.1913, 	55.9456);
    private static final MapPoint MIDDLE_OF_NO_FLY_ZONES = new MapPoint(-3.1896, 55.9448);
    private static final MapPoint BOTTOM_MIDDLE_G_SQUARE = new MapPoint(	-3.1885, 		55.9440);
    private static final MapPoint BOTTOM_LEFT_WAYPOINT = new MapPoint(-3.1916, 		55.9437);
    private static final MapPoint TOP_RIGHT_WAYPOINT = new MapPoint(-3.1862, 	55.9457);
    private static final MapPoint APPLETON_MAP_POINT = MapPoint.APPLETON_TOWER;

    @Test
    public void testFlyBetween() throws SQLException {
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");

        CafeMenus menus = website.fetchParsedMenus();
        ArrayList<ProcessedCafe> processedCafes = menus.getProcessedCafes(website);
        ArrayList<ProcessedOrder> processedOrders = database.getProcessedOrders(website, processedCafes);


        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchParsedMenus());

        assertFalse(area.canFlyBetween(TOP_RIGHT_WAYPOINT, BOTTOM_MIDDLE_G_SQUARE));

        assertTrue(area.canFlyBetween(APPLETON_MAP_POINT, BOTTOM_MIDDLE_G_SQUARE));
        assertFalse(area.canFlyBetween(APPLETON_MAP_POINT, TOP_LEFT_CAFE));
        assertFalse(area.canFlyBetween(BOTTOM_MIDDLE_G_SQUARE, TOP_LEFT_CAFE));
        assertFalse(area.canFlyBetween(MIDDLE_OF_NO_FLY_ZONES, TOP_LEFT_CAFE));
        assertFalse(area.canFlyBetween(MIDDLE_OF_NO_FLY_ZONES, APPLETON_MAP_POINT));
        assertFalse(area.canFlyBetween(MIDDLE_OF_NO_FLY_ZONES, BOTTOM_MIDDLE_G_SQUARE));

        assertTrue(area.canFlyBetween(TOP_RIGHT_WAYPOINT, TOP_LEFT_CAFE));
        assertTrue(area.canFlyBetween(TOP_RIGHT_WAYPOINT, APPLETON_MAP_POINT));
        assertFalse(area.canFlyBetween(TOP_RIGHT_WAYPOINT, BOTTOM_LEFT_WAYPOINT));
    }
    @Test
    public void testFlyBetweenVisual() throws SQLException {
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");

        CafeMenus menus = website.fetchParsedMenus();
        ArrayList<ProcessedCafe> processedCafes = menus.getProcessedCafes(website);
        ArrayList<ProcessedOrder> processedOrders = database.getProcessedOrders(website, processedCafes);


        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchParsedMenus());
        boolean result = area.canFlyBetween(TOP_RIGHT_WAYPOINT, BOTTOM_MIDDLE_G_SQUARE);
        while(true){
            if (1==2){
                break;
            }
        }
        assertFalse(result);
    }
}
