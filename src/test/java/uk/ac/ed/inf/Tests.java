package uk.ac.ed.inf;

import world.DroneArea;
import world.MapPoint;
import orders.Cafe;
import orders.Order;
import inputOutput.input.IOMenus;
import inputOutput.DatabaseHandle;
import inputOutput.WebsiteHandle;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class Tests {
    private static final MapPoint TOP_LEFT_CAFE = new MapPoint(-3.1913, 	55.9456);
    private static final MapPoint MIDDLE_OF_NO_FLY_ZONES = new MapPoint(-3.1896, 55.9448);
    private static final MapPoint BOTTOM_MIDDLE_G_SQUARE = new MapPoint(	-3.1885, 		55.9440);
    private static final MapPoint BOTTOM_LEFT_WAYPOINT = new MapPoint(-3.1916, 		55.9437);
    private static final MapPoint TOP_RIGHT_WAYPOINT = new MapPoint(-3.1862, 	55.9457);
    private static final MapPoint FAILED_TEST_TOP_LEFT = new MapPoint(-3.1910650730133057, 55.94562530517578);
    private static final MapPoint APPLETON_MAP_POINT = MapPoint.APPLETON_TOWER;



    @Test
    public void testFlyBetween() throws SQLException, IOException, InterruptedException {
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");

        IOMenus menus = website.fetchParsedMenus();
        ArrayList<Cafe> cafes = menus.processCafes(website);
        ArrayList<Order> orders = database.getProcessedOrders(website, cafes, "27-12-2023");


        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchLandmarks());


        assertTrue(area.canFlyBetween(APPLETON_MAP_POINT, BOTTOM_MIDDLE_G_SQUARE));
        assertFalse(area.canFlyBetween(APPLETON_MAP_POINT, TOP_LEFT_CAFE));
        assertFalse(area.canFlyBetween(BOTTOM_MIDDLE_G_SQUARE, TOP_LEFT_CAFE));
        assertFalse(area.canFlyBetween(MIDDLE_OF_NO_FLY_ZONES, TOP_LEFT_CAFE));
        assertFalse(area.canFlyBetween(MIDDLE_OF_NO_FLY_ZONES, APPLETON_MAP_POINT));
        assertFalse(area.canFlyBetween(MIDDLE_OF_NO_FLY_ZONES, BOTTOM_MIDDLE_G_SQUARE));

        assertTrue(area.canFlyBetween(TOP_RIGHT_WAYPOINT, TOP_LEFT_CAFE));
        assertTrue(area.canFlyBetween(TOP_RIGHT_WAYPOINT, APPLETON_MAP_POINT));
        assertFalse(area.canFlyBetween(TOP_RIGHT_WAYPOINT, BOTTOM_LEFT_WAYPOINT));
        assertFalse(area.canFlyBetween(TOP_RIGHT_WAYPOINT, BOTTOM_MIDDLE_G_SQUARE));

        assertTrue(area.canFlyBetween(BOTTOM_LEFT_WAYPOINT, TOP_LEFT_CAFE));
        assertTrue(area.canFlyBetween(BOTTOM_LEFT_WAYPOINT, APPLETON_MAP_POINT));
        assertFalse(area.canFlyBetween(BOTTOM_LEFT_WAYPOINT, TOP_RIGHT_WAYPOINT));
        assertTrue(area.canFlyBetween(BOTTOM_LEFT_WAYPOINT, BOTTOM_MIDDLE_G_SQUARE));


        while(true){
            if (1==2){
                break;
            }
        }
    }
    @Test
    public void testAngles(){
        for (int angle = 0; angle <= 360; angle+= 45.0) {
            MapPoint start = new MapPoint(0.0,0.0);
            MapPoint afterMove = start.nextPosition(angle, 1.0);
            double angleTo = start.angleTo(afterMove);
            double diff = Math.abs(angleTo - angle);

            assertTrue("Innacurate at " + angle + " : " + angleTo + " : " + afterMove, diff < 0.01);

        }
    }
    @Test
    public void testFlyBetweenVisual() throws SQLException, IOException, InterruptedException {
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");

        IOMenus menus = website.fetchParsedMenus();
        ArrayList<Cafe> cafes = menus.processCafes(website);
        ArrayList<Order> orders = database.getProcessedOrders(website, cafes, "27-12-2023");


        DroneArea area = new DroneArea(website.fetchNoFlyZones(), website.fetchLandmarks());
        boolean result = area.canFlyBetween(TOP_RIGHT_WAYPOINT, BOTTOM_MIDDLE_G_SQUARE);
        while(true){
            if (1==2){
                break;
            }
        }
        assertFalse(result);
    }
    @Test
    public void testBadWobble() {
        MapPoint start = new MapPoint(0.0,0.0);
        MapPoint five = start.nextPosition(5, 1.0);
        MapPoint ten = start.nextPosition(10, 1.0);
        MapPoint diff = new MapPoint(five.x - ten.x, five.y - ten.y);
        System.out.println(diff.distanceTo(start));
        System.out.println(("Perfect: " + five));
        System.out.println(("Bad: " + ten));
    }
}
