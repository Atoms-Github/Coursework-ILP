package inputOutput;

import cafes.ProcessedCafe;

import java.util.HashMap;
import java.util.List;

public class IOCafe {
    public String name;
    /**
     * The WhatThreeWords location of the cafe.
     */
    public String location;
    public List<IOMenus.MenuItem> menu;


    public ProcessedCafe process(WebsiteHandle website){
        HashMap<String, Integer> menuMap = new HashMap<>();
        for (IOMenus.MenuItem menuItem : menu){
            menuMap.put(menuItem.item, menuItem.pence);
        }
        return new ProcessedCafe(menuMap, website.fetchWhatThreeWordsBox(location));
    }
}
