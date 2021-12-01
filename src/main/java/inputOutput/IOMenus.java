package inputOutput;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import orders.Cafe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A parsed representation of all the items on the website.
 */
public class IOMenus {
    public List<IOCafe> cafes;

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
    public IOMenus(String jsonString){
        Type listType = new TypeToken<ArrayList<IOCafe>>() {}.getType();
        this.cafes = new Gson().<ArrayList<IOCafe>>fromJson(jsonString, listType);
    }


    public ArrayList<Cafe> getProcessedCafes(WebsiteHandle website){
        ArrayList<Cafe> cafes = new ArrayList<>();
        for (IOCafe cafe: this.cafes){
            cafes.add(cafe.process(website));
        }
        return cafes;
    }
}
