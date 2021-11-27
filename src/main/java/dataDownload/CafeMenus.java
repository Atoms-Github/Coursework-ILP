package dataDownload;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import routing.DBCafe;
import routing.ProcessedCafe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsed representation of all the items on the website.
 */
public class CafeMenus {
    public List<DBCafe> cafes;

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
    public CafeMenus(String jsonString){
        Type listType = new TypeToken<ArrayList<DBCafe>>() {}.getType();
        this.cafes = new Gson().<ArrayList<DBCafe>>fromJson(jsonString, listType);
    }

    // TODO: Tidy?
    public ArrayList<ProcessedCafe> getProcessedCafes(WebsiteHandle website){
        ArrayList<ProcessedCafe> processedCafes = new ArrayList<>();
        for (DBCafe cafe: cafes){
            processedCafes.add(cafe.process(website));
        }
        return processedCafes;
    }
}
