package orders;

import world.NamedMapPoint;

import java.util.HashMap;

/**
 * Represents a shop in the drone's world.
 */
public class Shop {
    public final HashMap<String, Integer> menu;
    public final NamedMapPoint location;

    public Shop(HashMap<String, Integer> menu, NamedMapPoint location) {
        this.menu = menu;
        this.location = location;
    }
}
