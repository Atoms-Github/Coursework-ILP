package routing;

import java.util.HashMap;

public class ProcessedCafe {
    public final HashMap<String, Integer> menu;
    public final NamedMapPoint location;

    public ProcessedCafe(HashMap<String, Integer> menu, NamedMapPoint location) {
        this.menu = menu;
        this.location = location;
    }
}
