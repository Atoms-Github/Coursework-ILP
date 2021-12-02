package inputOutput.input;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import inputOutput.WebsiteHandle;
import orders.Cafe;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsed representation of all the items on the website.
 */
public class IOMenus {
    /**
     * Cafes present on this menu. This is the list the customers would see.
     */
    public List<IOCafe> cafes;

    /**
     * Parses a new instance from json formatted string.
     * @param jsonString The menu, in json form.
     * @return The newly parsed IOMenus instance.
     */
    public static IOMenus parseFromString(String jsonString){
        IOMenus menus = new IOMenus();
        Type listType = new TypeToken<ArrayList<IOCafe>>() {}.getType();
        menus.cafes = new Gson().<ArrayList<IOCafe>>fromJson(jsonString, listType);
        return menus;
    }

    /**
     * Processes the cafe's in this IOMenus into a more useful form.
     * @param website Website to resolve the WhatThreeWords locations.
     * @return List of processed cafes.
     * @throws IOException If problem contacting website.
     * @throws InterruptedException If problem contacting website.
     */
    public ArrayList<Cafe> processCafes(WebsiteHandle website) throws IOException, InterruptedException {
        ArrayList<Cafe> processedCafes = new ArrayList<>();
        for (IOCafe cafe: this.cafes){
            processedCafes.add(cafe.process(website));
        }
        return processedCafes;
    }
}
