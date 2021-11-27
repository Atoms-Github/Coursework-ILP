package routing;

import dataDownload.CafeMenus;
import dataDownload.WebsiteHandle;

import java.util.HashMap;
import java.util.List;

public class DBCafe {
    public String name;
    /**
     * The WhatThreeWords location of the cafe.
     */
    public String location;
    public List<CafeMenus.MenuItem> menu;


    public ProcessedCafe process(WebsiteHandle website){
        HashMap<String, Integer> menuMap = new HashMap<>();
        for (CafeMenus.MenuItem menuItem : menu){
            menuMap.put(menuItem.item, menuItem.pence);
        }
        return new ProcessedCafe(menuMap, website.fetchWhatThreeWordsBox(location));
    }
}
