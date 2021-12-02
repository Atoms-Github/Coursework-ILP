package orders;

import world.NamedMapPoint;

import java.util.HashMap;

/**
 * Represents a cafe in the drone's world.
 */
public class Cafe {
    public final HashMap<String, Integer> menu;
    public final NamedMapPoint location;

    public Cafe(HashMap<String, Integer> menu, NamedMapPoint location) {
        this.menu = menu;
        this.location = location;
    }
}
