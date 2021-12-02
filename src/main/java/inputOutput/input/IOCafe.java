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
    public List<IOMenus.MenuItem> menu;


    public Cafe process(WebsiteHandle website) throws IOException, InterruptedException {
        HashMap<String, Integer> menuMap = new HashMap<>();
        for (IOMenus.MenuItem menuItem : menu){
            menuMap.put(menuItem.item, menuItem.pence);
        }
        return new Cafe(menuMap, website.fetchWhatThreeWordsBox(location));
    }
}
