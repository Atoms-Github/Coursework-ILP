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
    public static final MapPoint TOP_LEFT_CAFE = new MapPoint(-3.1913, 	55.9456);
    public static final MapPoint BOTTOM_MIDDLE_G_SQUARE = new MapPoint(	-3.1885, 		55.9440);
    public static final MapPoint APPLETON_MAP_POINT = MapPoint.APPLETON_TOWER;
    @Test
    public void testFlyBetween() throws SQLException {
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");

        CafeMenus menus = website.fetchParsedMenus();
        ArrayList<ProcessedCafe> processedCafes = menus.getProcessedCafes(website);
        ArrayList<ProcessedOrder> processedOrders = database.getProcessedOrders(website, processedCafes);


        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchParsedMenus());


        assertTrue(area.canFlyBetween(APPLETON_MAP_POINT, BOTTOM_MIDDLE_G_SQUARE));
        assertFalse(area.canFlyBetween(APPLETON_MAP_POINT, TOP_LEFT_CAFE));
        assertFalse(area.canFlyBetween(BOTTOM_MIDDLE_G_SQUARE, TOP_LEFT_CAFE));
    }
}
