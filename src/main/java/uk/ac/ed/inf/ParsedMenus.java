package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsed representation of all the items on the website.
 */
public class ParsedMenus {
    public List<Cafe> cafes;
    public static class Cafe{
        public String name;
        /**
         * The WhatThreeWords location of the cafe.
         */
        public String location;
        public List<MenuItem> menu;
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

    /**
     * Parses a menu.
     * @param jsonString The menu, in json representation (starting with a json list []).
     */
    public ParsedMenus(String jsonString){
        Type listType = new TypeToken<ArrayList<Cafe>>() {}.getType();
        this.cafes = new Gson().<ArrayList<Cafe>>fromJson(jsonString, listType);
    }

    /**
     * Finds an item in the menu.
     * @param itemName The full name of the item requested.
     * @return The item if found, or null if not.
     */
    public MenuItem getItem(String itemName){
        for (ParsedMenus.Cafe cafe: cafes){
            for (ParsedMenus.MenuItem item: cafe.menu){
                if (item.item.equals(itemName)){
                    return item;
                }
            }
        }
        return null;
    }
}
