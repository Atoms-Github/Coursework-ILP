package inputOutput.input;

import inputOutput.WebsiteHandle;
import orders.Cafe;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class IOCafe {
    public String name;
    /**
     * The WhatThreeWords location of the cafe.
     */
    public String location;
    /**
     * The items on the cafe's menu.
     */
    public List<MenuItem> menu;


    /**
     * Converts the IOCafe into a useful state, replacing WhatThreeWords location with real coordinates location.
     * @param website The website handle, to resolve the WhatThreeWords location with.
     * @return The newly made Cafe instance.
     * @throws IOException If can't fetch what three words location.
     * @throws InterruptedException If can't fetch what three words location.
     */
    public Cafe process(WebsiteHandle website) throws IOException, InterruptedException {
        HashMap<String, Integer> menuMap = new HashMap<>();
        for (MenuItem menuItem : menu){
            menuMap.put(menuItem.item, menuItem.pence);
        }
        return new Cafe(menuMap, website.fetchWhatThreeWordsBox(location));
    }

    public static class MenuItem{
        /**
         * The full long item name.
         */
        public String item;
        /**
         * The cost of the item.
         */
        public Integer pence;
    }
}
