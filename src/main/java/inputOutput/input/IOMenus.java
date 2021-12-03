package inputOutput.input;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import inputOutput.WebsiteHandle;
import orders.Shop;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsed representation of all the items on the website.
 */
public class IOMenus {
    /**
     * Shops present on this menu. This is the list the customers would see.
     */
    public List<IOShop> shops;

    /**
     * Parses a new instance from json formatted string.
     * @param jsonString The menu, in json form.
     * @return The newly parsed IOMenus instance.
     */
    public static IOMenus parseFromString(String jsonString){
        IOMenus menus = new IOMenus();
        Type listType = new TypeToken<ArrayList<IOShop>>() {}.getType();
        menus.shops = new Gson().<ArrayList<IOShop>>fromJson(jsonString, listType);
        return menus;
    }

    /**
     * Processes the shop's in this IOMenus into a more useful form.
     * @param website Website to resolve the WhatThreeWords locations.
     * @return List of processed shops.
     * @throws IOException If problem contacting website.
     * @throws InterruptedException If problem contacting website.
     */
    public ArrayList<Shop> processShops(WebsiteHandle website) throws IOException, InterruptedException {
        ArrayList<Shop> processedShops = new ArrayList<>();
        for (IOShop shop: this.shops){
            processedShops.add(shop.process(website));
        }
        return processedShops;
    }
}
